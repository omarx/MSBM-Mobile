package com.fras.msbm.activities.courses;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

//import com.fras.msbm.Manifest;
import android.Manifest;
import com.fras.msbm.R;
import com.fras.msbm.activities.bookings.BookingsActivity;
import com.fras.msbm.activities.general.BaseActivity;
import com.fras.msbm.adapter.recycler.TopicAdapter;
import com.fras.msbm.events.clicks.ModuleClickEvent;
import com.fras.msbm.models.User;
import com.fras.msbm.models.courses.Content;
import com.fras.msbm.models.courses.Course;
import com.fras.msbm.models.courses.Module;
import com.fras.msbm.models.courses.Topic;
import com.fras.msbm.network.Requestor;
import com.fras.msbm.services.MoodleUrlBuilder;
import com.fras.msbm.utils.MoodleUtil;
import com.fras.msbm.utils.StorageUtil;
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
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShowCourseActivity extends BaseActivity {
    public static final String TAG = ShowCourseActivity.class.getSimpleName();
    private final int EXTERNAL_STORAGE_REQUEST_PERMISSION = 1234;


    @BindView(R.id.coordinator_layout_main) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.recycler_topics)RecyclerView topicsRecyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private long downloadRef;
    private Snackbar snackbarPermissions;

    private Requestor requestor;
    private Handler mHandler;
    private Course course;
    private Content content;

    private final OkHttpClient client = new OkHttpClient();

    private TopicAdapter topicsAdapter;
    private DownloadManager downloadManager;
    private BroadcastReceiver  receiverNotificationClicked, receiverDownloadComplete;
    private DataSnapshot dataSnapshot;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference userRef = database.getReference("users");
    private FirebaseUser user;
    private String userToken;
    private Integer userID;
    private String downloadUri;
    private String moodleInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        ButterKnife.bind(this);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        course = (Course) getIntent().getSerializableExtra("course");
        requestor = new Requestor(new OkHttpClient());
        mHandler = new Handler(Looper.getMainLooper());
//        userToken = (String) getIntent().getStringExtra("token");
//        Toast.makeText(this,"userToken: " + userToken,Toast.LENGTH_SHORT).show();
        userToken = "";
        userID = 0;

//        if (! session.isLoggedIn()) {
//            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }

        if (course == null) {
            Log.i(TAG, "no course passed in extras");
            Toast.makeText(this, "Error loading course", Toast.LENGTH_SHORT).show();
            FirebaseCrash.log("Course failed to load");
            finish();
            return;
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null )
            findUserById(user.getUid());

        setupViews();
//        getCourseDetails();
    }


    private void findUserById(@NonNull String userId) {
        final String userResult ="x";
        userRef.child(userId).child("moodle")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        Log.i("TAG", "userCourses:" + user.toString());
//                        Toast.makeText(ShowCourseActivity.this, " " + user.getUserId(), Toast.LENGTH_SHORT).show();
//                        loadUserCourses(user);
                        userToken = user.getToken();
                        userID = user.getUserId();
                        moodleInstance = user.getMoodleInstance();
//                        Toast.makeText(ShowCourseActivity.this, "Token " + userToken, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(ShowCourseActivity.this, "UserID " + userID, Toast.LENGTH_SHORT).show();
//                        String userResult = user.getToken();
                        getCourseDetails();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        showErrorViews();
                        FirebaseCrash.report(databaseError.toException());
                    }
                });
//        return userResult;
    }


    @SuppressWarnings("ConstantConditions")
    private void setupViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(course.getShortName());
        getSupportActionBar().setSubtitle(course.getFullName());

        topicsAdapter = new TopicAdapter(this);
        topicsRecyclerView.setAdapter(topicsAdapter);
        topicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }




    private void requestCourseDetails() {
//        final Session session = new Session(this);
        final String courseDetailsUrl = MoodleUtil.getCourseContent(userToken, course.getId(),"");

        Observable.create((Observable.OnSubscribe<Response>) subscriber -> {
            try {
                Response response = client.newCall(new Request.Builder().url(courseDetailsUrl).build()).execute();
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        })
        .subscribeOn(Schedulers.io())
        .flatMap(response -> Observable.create(new Observable.OnSubscribe<List<Topic>>() {
            @Override
            public void call(Subscriber<? super List<Topic>> subscriber) {
                if (response.isSuccessful()) {
                    subscriber.onError(new AssertionError("Response unsuccessful"));
                    return;
                }

                try {
                    final String contentsResponse = response.body().string();
                    final JsonArray contentsJsonArray = new JsonParser().parse(contentsResponse).getAsJsonArray();
                    final List<Topic> topics = new ArrayList<>();
                    final Gson gson = new Gson();

                    for (JsonElement topicElement : contentsJsonArray) {
                        final Topic topic = gson.fromJson(topicElement, Topic.class);
                        topics.add(topic);
                    }
                    subscriber.onNext(topics);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<Topic>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "coursesLoaded:onError", e);
                FirebaseCrash.report(e);
            }

            @Override
            public void onNext(List<Topic> topics) {
                topicsAdapter.setEntities(topics);

                if (topics.size() <= 0)
                    Toast.makeText(ShowCourseActivity.this, "No topics found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestCourseDetails2() {
//        final Session session = new Session(this);
        final String courseDetailsUrl = MoodleUtil.getCourseContent(userToken, course.getId(),"");

        Observable.create((Observable.OnSubscribe<Response>) subscriber -> {
            try {
                Response response = client.newCall(new Request.Builder().url(courseDetailsUrl).build()).execute();
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                FirebaseCrash.report(e);
                Log.e(TAG, "courseTopics:error", e);
            }

            @Override
            public void onNext(Response response) {
                if ( ! response.isSuccessful()) {
                    Toast.makeText(ShowCourseActivity.this, "Request unsuccessful", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    final String contentsResponse = response.body().string();
                    final JsonArray contentsJsonArray = new JsonParser().parse(contentsResponse).getAsJsonArray();
                    final List<Topic> topics = new ArrayList<>();
                    final Gson gson = new Gson();

                    if (contentsJsonArray.size() <= 0) {
                        Toast.makeText(ShowCourseActivity.this, "Nothing to see here", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (JsonElement topicElement : contentsJsonArray) {
                        final Topic topic = gson.fromJson(topicElement, Topic.class);
                        topics.add(topic);
                    }

                    topicsAdapter.setEntities(topics);

                } catch (IOException e) {
                    FirebaseCrash.report(e);
                    Log.e(TAG, "courseContent:error", e);
                }
            }
        });
    }

    private void getCourseDetails() {
//        final Session sessionManager = new Session(this);
        final String token = this.userToken;
        final Integer userId = this.userID;

        if (token == null || userId == null) {
            Log.i(TAG, "token=" + token + " userId=" + userId);
            finish();
            return;
        }

        Log.e("Init Token", userToken);
        Log.e("Moodle Instance", moodleInstance);
        Log.e("Moodle Instance Test", "ourvle");
        Log.e("Moodle Instance", Boolean.toString(moodleInstance.equals("ourvle")));
//        Toast.makeText(ShowCourseActivity.this, "Tokenz " + token, Toast.LENGTH_SHORT).show();
        final String courseDetailsUrl = MoodleUtil.getCourseContent(userToken, course.getId(), moodleInstance);

        requestor.get(courseDetailsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onCreate:onFailure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String topicResponse = response.body().string();
                Log.e("JSON",topicResponse);
                Log.e("Token", userToken);
                Log.e("Course Code",course.getId());
                Log.e("User ID",userID.toString());
                Log.e("URL", courseDetailsUrl);
//                Toast.makeText(ShowCourseActivity.this, " " + userToken, Toast.LENGTH_SHORT).show();
                final JsonArray topicJsonArray = new JsonParser().parse(topicResponse).getAsJsonArray();

                if (topicJsonArray.size() <= 0) {
                    Toast.makeText(ShowCourseActivity.this, "Nothing to see here", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Gson gson = new Gson();
                final List<Topic> topics = new ArrayList<>();

                for (JsonElement topicElement : topicJsonArray) {
                    final Topic topic = gson.fromJson(topicElement, Topic.class);
//                    Log.i(TAG, "topicElement=" + topicElement.toString());
                    topics.add(topic);
                }
                mHandler.post(() -> topicsAdapter.setEntities(topics));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(ModuleClickEvent event) {
        final Module module = event.getModule();
        final List<Content> contents = module.getContents();
        if (contents.size() <= 0) return;
        content = contents.get(0);
        Log.e("ShowCourseActivity"," ModuleClickEvent");
        Log.e("ShowCourseActivity"," ModuleClickEvent--Test");
        if (content.getType().equals("url")) {
            final String url = content.getFileUrl();
            openFileInBrowser(url);
        } else if (content.getType().equals("file")) {
//            final Session sessionManager = new Session(this);
            String url = content.getFileUrl();
            url = url.replace("?", "&").replace(".php/", ".php?file=/") + "&token=" + userToken;
            if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
            final String uri = url;
            downloadUri = url;
            Log.e(TAG,url);
            if (!StorageUtil.isExternalStorageWritable()) {
                Toast.makeText(this, "Unable to download file, opening in browser", Toast.LENGTH_SHORT).show();
                openFileInBrowser(uri);
                return;
            }

//            http://ourvle.mona.uwi.edu/webservice/pluginfile.php/134761/mod_resource/content/1/MGMT3019VirtualTextbookV.2.pdf?forcedownload=1
//            http://ourvle.mona.uwi.edu/webservice/pluginfile.php?file=/134761/mod_resource/content/1/MGMT3019VirtualTextbookV.2.pdf&forcedownload=1&token=575273100f8491b02ffeaad3532bb66b

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    snackbarPermissions = Snackbar.make(coordinatorLayout, "Save file?", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Yes", view -> {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST_PERMISSION);
                            })
                            .setAction("No", view -> {
                                Toast.makeText(this, "Opening in browser", Toast.LENGTH_SHORT).show();
                                final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(uri));
                                startActivity(browserIntent);
                                snackbarPermissions.dismiss();
                            });

                    snackbarPermissions.show();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST_PERMISSION);
                }
            } else {
                Log.e(TAG, "Before Init");
                initiateDownload();
                Log.i(TAG, "onRequestPermission:permissionAlreadyGranted");

            }
        }
    }

    private void openFileInBrowser(String url) {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW);

        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        browserIntent.setData(Uri.parse(url));
        startActivity(browserIntent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateDownload();
                    Log.i(TAG, "onRequestPermission:here");
                } else {
                    Log.i(TAG, "onRequestPermission:else");
                    // file already opened using browser intent
                    // since permission was denied
                }
                break;
        }
    }

    private void initiateDownload() {
        Log.e(TAG, "Initiate Download");
        File msbmDirectory = new File(Environment.getExternalStorageDirectory()
                + "/MSBM");
        Log.e(TAG + " Directory", Environment.getExternalStorageDirectory()
                + "/MSBM/" + content.getFilename());

        Log.e(TAG + " New Test", this.getExternalFilesDir(null)
                + "/MSBM/" + content.getFilename());
        boolean isCreated = false;
        if (!msbmDirectory.exists())  {
            isCreated = msbmDirectory.mkdirs();

        }
        Log.e("IsCreated ",Boolean.toString(isCreated));
//        File extStore = Environment.getExternalStorageDirectory();
//        File filePath = new File(extStore.getAbsolutePath()
//                + "/MSBM/" + content.getFilename());

        File filePath = new File(this.getExternalFilesDir(null)
                + "/MSBM/" + content.getFilename());

//        if(filePath.exists()){
//            Log.e(TAG,"File Folder Exists");
//        }else{
//            Log.e(TAG,"File Folder Does Not Exist: " + filePath
//                    + "/MSBM");
//        }

        if(filePath.exists()){
            Log.e(TAG,"File Exists");

            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String extension = filePath.getName().substring(filePath.getName().lastIndexOf(".") + 1);
            String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension);
            Intent fileIntent = new Intent();
            fileIntent.setAction(Intent.ACTION_VIEW);
            fileIntent.setDataAndType(Uri.fromFile(filePath), mimeType);

            try {
                startActivity(fileIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ShowCourseActivity.this, "Unable to open file type", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.e(TAG,"File Does Not Exist");
            // TODO : allow custom folder to save to
//        Log.e(TAG, "Initiate Download");
            final Uri uri = Uri.parse(downloadUri);//Uri.parse(content.getFileUrl());
            Log.e(TAG, "Context URI" + uri );
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(content.getFilename())
                    .setDescription(content.getFileUrl())
                    .setDestinationInExternalFilesDir(this,
                            "/MSBM", content.getFilename())
                    .setVisibleInDownloadsUi(true);

            downloadRef = downloadManager.enqueue(request);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter notificationFilter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        final IntentFilter downloadFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        receiverNotificationClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String extraId = DownloadManager
                        .EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraId);
                for (long reference : references) {
                    if (reference == downloadRef) {

                    }
                }
            }
        };

        receiverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (downloadRef == reference) {
                    final DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    final Cursor cursor = downloadManager.query(query);

                    cursor.moveToFirst();
                    final int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    final int status = cursor.getInt(columnIndex);
                    final int fileUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    final String filePath = Uri.parse(cursor.getString(fileUriIndex)).getPath();
                    final int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    final int reason = cursor.getInt(columnReason);

                    Log.i(TAG, "saved path = " + filePath);

                    switch (status) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            Log.i(TAG, "downloadReceiver:status=successful");

                            File file = new File(filePath);
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                            String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension);
                            Intent fileIntent = new Intent();
                            fileIntent.setAction(Intent.ACTION_VIEW);
                            fileIntent.setDataAndType(Uri.fromFile(file), mimeType);

                            try {
                                startActivity(fileIntent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(ShowCourseActivity.this, "Unable to open file type", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case DownloadManager.STATUS_FAILED:
                            Log.i(TAG, "downloadReceiver:status=failed");
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Log.i(TAG, "downloadReceiver:status=pause");
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Log.i(TAG, "downloadReceiver:status=pending");
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Log.i(TAG, "downloadReceiver:status=running");
                            break;
                    }

                }
            }
        };

        registerReceiver(receiverNotificationClicked, notificationFilter);
        registerReceiver(receiverDownloadComplete, downloadFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverNotificationClicked);
        unregisterReceiver(receiverDownloadComplete);
    }

}