package com.fras.msbm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fras.msbm.R;
import com.fras.msbm.activities.courses.ShowCourseActivity;
import com.fras.msbm.adapter.recycler.CourseAdapter;
import com.fras.msbm.events.CourseClickEvent;
import com.fras.msbm.events.network.NetworkConnectEvent;
import com.fras.msbm.events.network.NetworkDisconnectEvent;
import com.fras.msbm.events.users.UserLoginEvent;
import com.fras.msbm.exceptions.moodle.InvalidCredentialsException;
import com.fras.msbm.models.Lecture;
import com.fras.msbm.models.User;
import com.fras.msbm.models.courses.Course;
import com.fras.msbm.services.MoodleUrlBuilder;
import com.fras.msbm.services.MoodleUrlBuilderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ListCoursesFragment extends BaseFragment {
    public static final String TAG = ListCoursesFragment.class.getSimpleName();

    @BindView(R.id.recycler_courses) RecyclerView recyclerCourses;
    @BindView(R.id.linear_layout_empty) LinearLayout relativeLayoutEmpty;
    @BindView(R.id.circular_progress_bar) CircularProgressBar circularProgressBar;
    @BindView(R.id.text_empty) TextView textViewEmpty;
    @BindView(R.id.button_empty) Button buttonEmpty;
    @BindView(R.id.image_empty) ImageView imageViewEmpty;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference userRef = database.getReference("users");
    private final OkHttpClient client = new OkHttpClient();

    private MoodleUrlBuilder moodleUrlBuilder;
    private CourseAdapter courseAdapter;
    private List<Lecture> lectures;
    private String userToken;
    private String userID;

    public ListCoursesFragment() {
        // Required empty public constructor
    }

    public static ListCoursesFragment newInstance() {
        return new ListCoursesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list_courses, container, false);

        ButterKnife.bind(this, rootView);
        setToolbarTitle("Courses");
        setupViews();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.e(TAG, "Current User: " + user.toString());
        if (user != null ){
            findUserById(user.getUid());
        }else{
            circularProgressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "You Must Be Logged In.", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void setupViews() {
        relativeLayoutEmpty.setVisibility(View.GONE);
        courseAdapter = new CourseAdapter(getContext());
        recyclerCourses.setAdapter(courseAdapter);
        recyclerCourses.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void findUserById(@NonNull String userId) {
        userRef.child(userId).child("moodle")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            final User user = dataSnapshot.getValue(User.class);
                            Log.i(TAG, "userCourses:" + user.toString());
                            loadUserCourses(user);
                        }catch(Exception e){
                            circularProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You Must Be Logged In.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showErrorViews();
                        FirebaseCrash.report(databaseError.toException());
                    }
                });
    }

    private void showErrorViews() {
        // TODO
    }

    private void loadUserCourses(@NonNull User user) {
        requestCourses(user)
                .flatMap(this::createListFromResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleCourses());
    }

    private Subscriber<List<Course>> handleCourses() {
        return new Subscriber<List<Course>>() {
            @Override
            public void onCompleted() {
                circularProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "courses:error", e);
                FirebaseCrash.report(e);
                circularProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load courses", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<Course> courses) {
                courseAdapter.setEntities(courses);
                if (courses.size() <= 0) showEmptyView();
                circularProgressBar.setVisibility(View.GONE);
            }
        };
    }

    private void showEmptyView() {
        relativeLayoutEmpty.setVisibility(View.VISIBLE);
    }

    private Observable<List<Course>> createListFromResponse(Response response) {
        return Observable.create(new Observable.OnSubscribe<List<Course>>() {
            @Override
            public void call(Subscriber<? super List<Course>> subscriber) {
                try {
                    if (!response.isSuccessful()) throw new IOException();
                    final List<Course> courses = extractCoursesFromResponse(response);
                    subscriber.onNext(courses);
                    subscriber.onCompleted();
                } catch (IOException | InvalidCredentialsException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observable<Response> requestCourses(User user) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    final String token = user.getToken();
                    final String userId = String.valueOf(user.getUserId());
                    final String moodleInstance = user.getMoodleInstance();

                    userToken = token;
                    userID = userId;
                    final MoodleUrlBuilder moodleUrlBuilder = getMoodleUrlBuilder(moodleInstance);
                    final String url = moodleUrlBuilder.buildCoursesUrl(token, userId);
                    Log.i(TAG, "coursesUrl:" + url);
                    final Request courseRequest = new Request.Builder().url(url).build();
                    final Response courseResponse = client.newCall(courseRequest).execute();
                    subscriber.onNext(courseResponse);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    private MoodleUrlBuilder getMoodleUrlBuilder(@NonNull String moodleInstance) {
        final MoodleUrlBuilderFactory factory = new MoodleUrlBuilderFactory(getContext());
        return factory.getUrlBuilder(moodleInstance);
    }

    private List<Course> extractCoursesFromResponse(Response response) throws IOException, InvalidCredentialsException {
        final ResponseBody responseBody = response.body();
        final String courseResponseAsString = responseBody.string();
        Log.i(TAG, "coursesReponse:" + courseResponseAsString);

        try {
            final JsonArray coursesJsonArray = new JsonParser().parse(courseResponseAsString).getAsJsonArray();
            return extractCoursesFromJsonArray(coursesJsonArray);
        } catch (IllegalStateException e){
            final JsonObject errorObject = new JsonParser().parse(courseResponseAsString).getAsJsonObject();

            InvalidCredentialsException error = new InvalidCredentialsException();

            if (errorObject.has("errorcode") && errorObject.has("message")) {
                String message = errorObject.get("message").toString();
                String errorCode = errorObject.get("errorcode").toString();

                error.setErrorMessage(message);
                error.setErrorCode(errorCode);
            }
            throw error;
        }
    }

    private List<Course> extractCoursesFromJsonArray(JsonArray coursesJsonArray) {
        final Gson gson = new Gson();
        final List<Course> courses = new ArrayList<>();

        for (JsonElement courseJsonElement : coursesJsonArray) {
            final Course course = gson.fromJson(courseJsonElement, Course.class);
            courses.add(course);
        }

        return courses;
    }

    @OnClick(R.id.button_empty)
    public void onButtonEmptyClicked(Button button) {

    }

    @Subscribe
    public void onEventMainThread(NetworkDisconnectEvent event) {
        if (courseAdapter.getEntities().size() == 0) {
            textViewEmpty.setText("No Internet");
            relativeLayoutEmpty.setVisibility(View.VISIBLE);
            recyclerCourses.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onEventMainThread(NetworkConnectEvent event) {

    }

    @Subscribe
    public void onEventMainThread(UserLoginEvent event) {
        final FirebaseUser firebaseUser = event.getUser();
        final String userId = firebaseUser.getUid();
        findUserById(userId);
        Log.i(TAG, "course:userLoggedIn");
    }

    @Subscribe
    public void onEvent(CourseClickEvent event) {
        final Course course = event.getCourse();
        final Intent courseIntent = new Intent(getActivity(), ShowCourseActivity.class);
        courseIntent.putExtra("course", course);
//        courseIntent.putExtra("Token", userToken);
//        courseIntent.putExtra("userID", userID);
        startActivity(courseIntent);
    }
}
