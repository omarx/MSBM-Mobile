package com.fras.msbm.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fras.msbm.R;
import com.fras.msbm.adapter.recycler.LectureAdapter;
import com.fras.msbm.events.users.UserLoginEvent;
import com.fras.msbm.events.users.UserLogoutEvent;
import com.fras.msbm.models.Lecture;
import com.fras.msbm.utils.StringUtils;
import com.fras.msbm.views.recyclers.SimpleDividerItemDecoration;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class ListLecturesFragment extends BaseFragment {
    public static final String TAG = ListLecturesFragment.class.getName();

    @BindView(R.id.recycler_lectures) RecyclerView lecturesRecyclerView;
    @BindView(R.id.circular_progress_bar) CircularProgressBar progressBar;

    private OkHttpClient client = new OkHttpClient();
    private LectureAdapter lectureAdapter;

    public ListLecturesFragment() {
        // Required empty public constructor
    }

    public static ListLecturesFragment newInstance() {
        return new ListLecturesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list_lecture, container, false);
        ButterKnife.bind(this, rootView);
        setupRecyclerView();
        loadEvents();
        return rootView;
    }

    private void loadEvents() {
        Request lectureRequest = buildRequest();

        Log.i(TAG, "in listlecturefrag");

        progressBar.setVisibility(View.VISIBLE);

        generateResponseFromLecturerRequest(lectureRequest)
                .flatMap(this::generateLecturesFromResponse)
                .flatMap(new Func1<List<Lecture>, Observable<List<Lecture>>>() {
                    @Override
                    public Observable<List<Lecture>> call(List<Lecture> lectures) {
                        return Observable.create(new Observable.OnSubscribe<List<Lecture>>() {
                            @Override
                            public void call(Subscriber<? super List<Lecture>> subscriber) {
                                for (int i = 0; i < lectures.size(); i++) {
                                    Log.i(TAG, "lecture[" + i + "]" + "=" + lectures.get(i));
                                }
                                subscriber.onNext(lectures);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
//                .map(this::remapLectures)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleLectureLoading());
    }

    private Subscriber<List<Lecture>> handleLectureLoading() {
        return new Subscriber<List<Lecture>>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "lecture:complete");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "lecture:error", e);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNext(List<Lecture> lectures) {
                Log.i(TAG, "lecture:next");
                lectureAdapter.addEntities(lectures);
            }
        };
    }

    private List<Lecture> remapLectures(@NonNull List<Lecture> lectures) {
        List<Lecture> updatedLectures = new ArrayList<>();
        for (Lecture lecture : lectures)
            updatedLectures.add(mapLectureData(lecture));
        return updatedLectures;
    }

    private Observable<List<Lecture>> generateLecturesFromResponse(@NonNull Response response) {
        return Observable.create(new Observable.OnSubscribe<List<Lecture>>() {
            @Override
            public void call(Subscriber<? super List<Lecture>> subscriber) {
                try {
                    if (!response.isSuccessful()) throw new IOException();

                    final List<Lecture> lectures = extractLecturesFromResponse(response);
                    subscriber.onNext(lectures);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private List<Lecture> extractLecturesFromResponse(@NonNull Response response) throws IOException {
        final String classesResponse = response.body().string();
        final JsonArray classesJsonArray = new JsonParser().parse(classesResponse).getAsJsonArray();
        return extractLecturesFromJsonArray(classesJsonArray);
    }

    private Observable<Response> generateResponseFromLecturerRequest(@NonNull Request request) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private Request buildRequest() {
        final String url = "http://mobile.antoniofearon.com/api/v1/bookings.php";

        final RequestBody body = new FormBody.Builder()
                .add("request_type", "classes")
                .build();

        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    private List<Lecture> extractLecturesFromJsonArray(@NonNull JsonArray jsonArray) {
        final Gson gson = new Gson();
        final List<Lecture> lectures = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            final Lecture lecture = gson.fromJson(jsonElement, Lecture.class);
            Log.i(TAG, "lecture:" + lecture.toString());

            final Lecture mappedLecture = mapLectureData(lecture);
            lectures.add(mappedLecture);
        }

        return lectures;
    }

    private Lecture mapLectureData(Lecture lecture) {
        final String[] data = StringUtils.tokenizeLectureName(lecture.getName());
        try {
            final String name = data[0];
            final String location = data[1];
            final String lecturer = data[2];

            lecture.setName(name);
            lecture.setLocation(location);
            lecture.setLecturer(lecturer);
        } catch (IndexOutOfBoundsException e) {
            Log.i(TAG, ":lecture:" + lecture.toString());
        }
        return lecture;
    }

    private void setupRecyclerView() {
        lectureAdapter = new LectureAdapter(getContext());
        lecturesRecyclerView.setAdapter(lectureAdapter);
        lecturesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lecturesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    }

    @Subscribe
    public void onEvent(UserLoginEvent event){

    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {

    }
}
