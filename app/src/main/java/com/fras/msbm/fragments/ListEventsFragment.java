package com.fras.msbm.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fras.msbm.R;
import com.fras.msbm.adapter.recycler.EventsAdapter;
import com.fras.msbm.adapter.recycler.LectureAdapter;
import com.fras.msbm.constants.MSBMConstants;
import com.fras.msbm.events.network.NetworkConnectEvent;
import com.fras.msbm.events.users.UserLoginEvent;
import com.fras.msbm.events.users.UserLogoutEvent;
import com.fras.msbm.models.Lecture;
import com.fras.msbm.utils.StringUtils;
import com.fras.msbm.views.recyclers.SimpleDividerItemDecoration;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ListEventsFragment extends BaseFragment {
    public static final String TAG = ListEventsFragment.class.getName();

    @BindView(R.id.circular_progress_bar) CircularProgressBar progressBar;
    @BindView(R.id.recycler_lectures) RecyclerView lectureRecyclerView;

    private final OkHttpClient client = new OkHttpClient();
    private EventsAdapter lectureAdapter;

    public ListEventsFragment() {}

    public static ListEventsFragment newInstance() {
        return new ListEventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle(getString(R.string.title_events));
        setupLayouts();
        loadLectures(MSBMConstants.BOOKINGS_URL);
        return rootView;
    }

    private void loadLectures(@NonNull String url) {
        progressBar.setVisibility(View.VISIBLE);

        generateResponseObservableFromUrl(url)
                .flatMap(this::convertResponseToLecturerListObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleLecturesEmitted());
    }

    private Observable<List<Lecture>> convertResponseToLecturerListObservable(Response response) {
        return Observable.create(subscriber -> {
            try {
                if ( ! response.isSuccessful()) throw new IOException();

                List<Lecture> lecturesList = extractLecturesFromResponse(response);
                Set<Lecture> noDuplicates = new LinkedHashSet<>(lecturesList);
                lecturesList.clear();
                lecturesList.addAll(noDuplicates);



                subscriber.onNext(lecturesList);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<Response> generateResponseObservableFromUrl(@NonNull String url) {
        return Observable.create(subscriber -> {
            try {
                Request request = buildLectureRequest(url);
                Response response = client.newCall(request).execute();
                subscriber.onNext(response);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Subscriber<List<Lecture>> handleLecturesEmitted() {
        return new Subscriber<List<Lecture>>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "lectures:completed");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "lectures:error", e);
                FirebaseCrash.report(e);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNext(List<Lecture> lectures) {
                for (int i = 0; i < lectures.size(); i++)
                    Log.i(TAG, "lecture[" + i + "]=" + lectures.get(i));
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "lectures:next");
                lectureAdapter.addEntities(lectures);
            }
        };
    }

    private List<Lecture> extractLecturesFromResponse(Response response) throws IOException {
        List<Lecture> lecturesList = new ArrayList<>();
        Gson gson = new Gson();
        String responseBodyAsString = response.body().string();
        JsonArray jsonPayloadAsArray = new JsonParser().parse(responseBodyAsString).getAsJsonArray();

        for (JsonElement lectureElement : jsonPayloadAsArray) {
            final Lecture lecture = gson.fromJson(lectureElement, Lecture.class);
            final Lecture remappedLecture = remapLecture(lecture);
            lecturesList.add(remappedLecture);
        }

        return lecturesList;
    }

    private Lecture remapLecture(Lecture lecture) {
        String[] data = lecture.getName().split("-");
//        lecture.setName(data[0].trim());
//        lecture.setNotes(data[1].trim());
        try {
            lecture.setLecturer(data[2].trim());
        } catch (IndexOutOfBoundsException e) {
            Log.i(TAG, "failed to load lecture");
            FirebaseCrash.report(e);
        }

        try {
            Date date = Lecture.dateFormat.parse(lecture.getDate());
            lecture.setDate(new SimpleDateFormat("EEE", Locale.ENGLISH).format(date));
        } catch (ParseException e) {
            Log.i(TAG, "failed to parse date");
            FirebaseCrash.report(e);
        }

        return lecture;
    }

    private void setupLayouts() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        lectureRecyclerView.setLayoutManager(linearLayoutManager);

        lectureAdapter = new EventsAdapter(getContext());
        lectureRecyclerView.setAdapter(lectureAdapter);
    }

    private Request buildLectureRequest(String url) {
        final RequestBody requestBody = new FormBody.Builder()
                .add("request_type", "events").build();

        return new Request.Builder()
                .url(url)
                .post(requestBody).build();
    }

    @Subscribe
    public void onEvent(NetworkConnectEvent event) {

    }

    @Subscribe
    public void onEventMainThread(UserLoginEvent event) {
        FirebaseUser user = event.getUser();
    }

    @Subscribe
    public void onEventMainThread(UserLogoutEvent event) {

    }
}
