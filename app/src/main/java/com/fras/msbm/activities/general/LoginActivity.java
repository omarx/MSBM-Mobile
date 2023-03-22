package com.fras.msbm.activities.general;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fras.msbm.R;
import com.fras.msbm.events.network.NetworkConnectEvent;
import com.fras.msbm.events.network.NetworkDisconnectEvent;
import com.fras.msbm.gallery.ImageGalleryActivity;
import com.fras.msbm.models.User;
import com.fras.msbm.models.courses.Course;
import com.fras.msbm.models.data.MoodleData;
import com.fras.msbm.services.MoodleUrlBuilder;
import com.fras.msbm.services.MoodleUrlBuilderFactory;
import com.fras.msbm.services.NullUrlBuilder;
import com.fras.msbm.services.OurvleUrlBuilder;
import com.fras.msbm.utils.MoodleUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.circular_progress_bar) CircularProgressBar circularProgressBar;
    @BindView(R.id.radio_group_moodle) RadioGroup moodleGroupMoodle;
    @BindView(R.id.radio_group_moodleLocation) RadioGroup moodleGroupCampus;
//    @BindView(R.id.relative_content) RelativeLayout contentRelativeLayout;
//    @BindView(R.id.image_background) ImageView backgroundImageView;
    @BindView(R.id.edit_text_username) TextInputEditText usernameEditText;
    @BindView(R.id.edit_text_password) TextInputEditText passwordEditText;
    @BindView(R.id.button_moodle_login) Button loginButton;
    @BindView(R.id.button_skip) Button skipButton;

    final OkHttpClient client = new OkHttpClient();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference userRef = database.getReference("users");

    MoodleUrlBuilder moodleUrlBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            closeLoginAndOpenHomeActivity();

        setContentView(R.layout.activity_login_msbm);
        ButterKnife.bind(this);

//        backgroundImageView.setColorFilter(getResources()
//                .getColor(R.color.black), PorterDuff.Mode.MULTIPLY);

//        usernameEditText.setText("620087337");
//        passwordEditText.setText("19930831");
    }

    @OnClick(R.id.button_moodle_login)
    public void onMoodleButtonClick() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        boolean isValid = validateUsername(username) && validatePassword(password);

//        if (!isValid) {
//            Log.i(TAG, "button:credentialsInvalid");
//            return;
//        }

        Credentials credentials = new Credentials(username, password);
        attemptLogin(credentials);
    }

    @OnClick(R.id.button_skip)
    public void onSkipButtonClick() {
//        firebaseAuth.signInAnonymously()
//                .addOnSuccessListener(authSuccessResult -> Log.i(TAG, "anonymous:success"))
//                .addOnFailureListener(e -> Log.e(TAG, "anonymous:failed", e))
//                .addOnCompleteListener(task -> closeLoginAndOpenHomeActivity());
//        closeLoginAndOpenHomeActivity();
        openActivity(HomeActivity.class);
    }

    private String getMoodleType() {
        int radioOption = moodleGroupMoodle.getCheckedRadioButtonId();

        switch (radioOption) {
            case R.id.radio_ourvle:
                return MoodleUrlBuilderFactory.OURVLE;
            case R.id.radio_msbm:
                return MoodleUrlBuilderFactory.MSBM;
        }
        return "";
    }

    private String getCampusLocation(){
        int radioOption = moodleGroupCampus.getCheckedRadioButtonId();

        switch(radioOption){
            case R.id.radio_mona:
                return "mona";
            case R.id.radio_wcj:
                return "wcj";
        }

        return "";
    }

    private MoodleUrlBuilder createMoodleUrlBuilder() {
        final MoodleUrlBuilderFactory moodleFactory = new MoodleUrlBuilderFactory(this);
        final String moodleType = getMoodleType();
        return moodleFactory.getUrlBuilder(moodleType);
    }

    private void closeLoginAndOpenHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    private void attemptLogin(@NonNull Credentials credentials) {
        moodleUrlBuilder = createMoodleUrlBuilder();

        if (moodleUrlBuilder instanceof NullUrlBuilder) {
            Toast.makeText(this, "Error in form", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingStarted();

        generateResponseObservableFromCredentials(credentials)
                .flatMap(this::mapResponseToTokenObservable)
                .map(token -> moodleUrlBuilder.buildSiteInfoUrl(token))
                .flatMap(this::requestUserBasicInformation)
                .flatMap(this::requestUserDetailInformation)
                .flatMap(this::mapResponseToUserObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleLoginResult());
    }

    private Observable<Response> requestUserDetailInformation(Response response) {
        return Observable.create(subscriber -> {
            try {
                if (! response.isSuccessful()) throw new IOException();

                MoodleData data = extractBasicInfoFromResponse(response);
                data.userId = extractUserIdFromResponse(response);

                Log.i(TAG, "token = " + data.token);
                Log.i(TAG, "user id = " + data.userId);

                final String url = moodleUrlBuilder.buildUserDetailsUrl(data.token, data.userId);
                final Response userResponse = executeCallWithUrl(url);
                subscriber.onNext(userResponse);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private String extractUserIdFromResponse(Response response) throws IOException {
        String payload = response.body().string();
        JsonObject json = new JsonParser().parse(payload).getAsJsonObject();
        return json.get("userid").getAsString();
    }

    private MoodleData extractBasicInfoFromResponse(Response response) throws IOException {
        final Request oldRequest = response.request();
        final String oldUrl = oldRequest.url().toString();

        Log.i(TAG, "oldurl - " + oldUrl);

        return MoodleUtil.parseUrlForTokenAndUserId(oldUrl);
    }

    private void showLoadingStarted() {
        loginButton.setEnabled(false);
        skipButton.setEnabled(false);
        circularProgressBar.setVisibility(View.VISIBLE);
//        contentRelativeLayout.setVisibility(View.GONE);
    }

    private void showLoadingCompleted() {
        loginButton.setEnabled(true);
        skipButton.setEnabled(true);
        circularProgressBar.setVisibility(View.GONE);
//        contentRelativeLayout.setVisibility(View.VISIBLE);
    }

    private Subscriber<User> handleLoginResult() {
        return new Subscriber<User>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "login:completed");
                showLoadingCompleted();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "login:error", e);
                showLoadingCompleted();
                FirebaseCrash.report(e);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(User user) {
                Log.i(TAG, "login:next");
                Log.i(TAG, "user:data - " + user.toString());
                connectToFirebaseAuthentication(user);
            }
        };
    }

    private void connectToFirebaseAuthentication(@NonNull User user) {
        final String email = user.getEmail();
        final String password = user.getUsername(); // token
        user.setMoodleLocation(getCampusLocation());

        Log.e(TAG, "Campus Location: " + user.getMoodleLocation());
        Log.i(TAG, "email - " + email + " | password - " + password);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.i(TAG, "login:success");
                    saveUserDetailsToFirebase(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "login:fail", e);
                    registerUsersInFirebase(user);
                });
    }

    private void registerUsersInFirebase(@NonNull User user) {
        final String email = user.getEmail();
        final String password = user.getUsername();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.i(TAG, "register:success");
                })
                .addOnFailureListener(e1 -> Log.e(TAG, "register:failed", e1))
                .addOnCompleteListener(task -> showLoadingCompleted());
    }

    private void saveUserDetailsToFirebase(@NonNull User user) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "firebase:connectFailed");
            return;
        }

        final String moodleInstance = (moodleUrlBuilder instanceof OurvleUrlBuilder)
                ? MoodleUrlBuilderFactory.OURVLE
                : MoodleUrlBuilderFactory.MSBM;

        // TODO : should be a better way to set this
        user.setMoodleInstance(moodleInstance);

        String id = firebaseUser.getUid();
        userRef.child(id).child("moodle").setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "user:success");
                    openActivity(HomeActivity.class);
                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "user:details", e))
                .addOnCompleteListener(task -> showLoadingCompleted());
    }

    private Observable<User> mapResponseToUserObservable(Response response) {
        return Observable.create(subscriber -> {
            try {
                if (!response.isSuccessful()) throw new IOException();
                User user = extractUserFromResponse(response);
                MoodleData data = MoodleUtil.parseUrlForTokenAndUserId(response.request().url().toString());
                user.setToken(data.token);
                Log.i(TAG, "url - " + response.request().url().toString());
                Log.i(TAG, "user:here - " + user.toString());
                subscriber.onNext(user);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private User extractUserFromResponse(Response response) throws IOException {
        final String responseAsString = response.body().string();
        final JsonArray jsonPayload = new JsonParser().parse(responseAsString).getAsJsonArray();
        final JsonObject userJsonData = jsonPayload.get(0).getAsJsonObject();

        final Gson gson = new Gson();
        final User user = gson.fromJson(userJsonData, User.class);

//        final JsonArray coursesJsonData = userJsonData.get("enrolledcourses").getAsJsonArray();
//        final List<Course> courses = extractCoursesFromJsonArray(coursesJsonData);
//
//        user.setCourses(courses);

        final String fullName = userJsonData.get("fullname").getAsString();
        String names[] = fullName.split(" ");

        user.setFirstName(names[0]);
        user.setLastName(names[1]);

        return user;
    }

    private List<Course> extractCoursesFromJsonArray(JsonArray coursesJsonData) {
        Gson gson = new Gson();
        List<Course> courses = new ArrayList<>();
        for (JsonElement courseElement : coursesJsonData) {
            Course course = gson.fromJson(courseElement, Course.class);
            courses.add(course);
        }
        return courses;
    }

    private Observable<Response> requestUserBasicInformation(String url) {
        return Observable.create(subscriber -> {
            try {
                Response response = executeCallWithUrl(url);
                subscriber.onNext(response);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<String> mapResponseToTokenObservable(Response response) {
        return Observable.create(subscriber -> {
                try {
                    if (!response.isSuccessful()) throw new IOException();
                    String token = extractTokenFromResponse(response);
                    subscriber.onNext(token);
                } catch (IOException | CredentialInvalidException e) {
                    subscriber.onError(e);
                }
        });
    }

    private String extractTokenFromResponse(Response response) throws IOException, CredentialInvalidException {
        String responseAsString = response.body().string();
        JsonObject jsonPayload = new JsonParser().parse(responseAsString).getAsJsonObject();
        if (jsonPayload.has("error")) {
            String errorMessage = jsonPayload.get("error").getAsString();
            throw new CredentialInvalidException(errorMessage);
        }
        return jsonPayload.get("token").getAsString();
    }

    private Observable<Response> generateResponseObservableFromCredentials(Credentials credentials) {
        return Observable.create(subscriber -> {
                try {
                    final String username = credentials.getUsername();
                    final String password = credentials.getPassword();

                    String tokenUrl = moodleUrlBuilder.buildTokenUrl(username, password);

                    Log.i(TAG, "token url - " + tokenUrl);

                    Response response = executeCallWithUrl(tokenUrl);

                    subscriber.onNext(response);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            });
    }

    private Response executeCallWithUrl(@NonNull String url) throws IOException {
        final Request request = new Request.Builder().url(url).build();
        return client.newCall(request).execute();
    }

    private boolean validateUsername(@NonNull String username) {
        boolean isValid = username.length() == 9;

//        if (! isValid)
//            Toast.makeText(this, "Enter a valid username", Toast.LENGTH_SHORT)
//                    .show();

        Log.i(TAG, "username:isValid=" + isValid);

        return isValid;
    }

    private boolean validatePassword(@NonNull String password) {
        boolean isValid = ! password.isEmpty();

        if ( ! isValid) Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT)
                .show();

        Log.i(TAG, "password:isValid=" + isValid);

        return isValid;
    }

    @Subscribe
    public void onEvent(NetworkConnectEvent event) {
        Log.i(TAG, "network:connected");
        loginButton.setEnabled(true);
    }

    @Subscribe
    public void onEvent(NetworkDisconnectEvent event) {
        Log.i(TAG, "network:disconnect");
        loginButton.setEnabled(false);
    }

    private class Credentials {
        private String username;
        private String password;

        public Credentials(@NonNull String username, @NonNull String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    private class CredentialInvalidException extends Exception {
        public CredentialInvalidException(String message) {
            super(message);
        }
    }
}
