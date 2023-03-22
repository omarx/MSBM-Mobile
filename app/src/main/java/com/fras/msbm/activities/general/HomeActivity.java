package com.fras.msbm.activities.general;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fras.msbm.R;
import com.fras.msbm.VideoListDemoActivity;
import com.fras.msbm.activities.bookings.BookingsOptionActivity;
import com.fras.msbm.events.users.UserLoginEvent;
import com.fras.msbm.events.users.UserLogoutEvent;
import com.fras.msbm.fragments.ClassesFragment;
import com.fras.msbm.fragments.ListArticlesFragment;
import com.fras.msbm.fragments.ListContactsFragment;
import com.fras.msbm.fragments.ListCoursesFragment;
import com.fras.msbm.fragments.ListEventsFragment;
import com.fras.msbm.fragments.LocationFragment;
import com.fras.msbm.fragments.SocialFragment;
import com.fras.msbm.fragments.VideoFragment;
import com.fras.msbm.gallery.ImageGalleryActivity;
import com.fras.msbm.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Bundle extras;
    public static final String TAG = HomeActivity.class.getName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.container_navigation_fragment) FrameLayout containerFrameLayout;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference userRef = database.getReference("users");

    MenuItem loginMenuItem;
    MenuItem logoutMenuItem;

    TextView nameTextView;
    CircularImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        extras = getIntent().getExtras();

        if(extras != null){
            openLocationFragment(extras);
        }else{
            openInitialFragment(savedInstanceState);
        }

//        test();
        setupToolbar();
        setupDrawerHeader();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null ) {
            try {
                loginMenuItem.setVisible(false);
            } catch (Exception e) {
                Log.e(TAG, "onResume " + e.toString());
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null )
            try{
                loginMenuItem.setVisible(false);
            }catch(Exception e){
                Log.e(TAG,"onResume " + e.toString());
            }

    }
    private void setupDrawerHeader() {
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        profileImage = (CircularImageView) headerView.findViewById(R.id.image_profile);
        nameTextView = (TextView) headerView.findViewById(R.id.text_name);
    }

    private void loadUserData(@NonNull User user) {
        nameTextView.setText(user.getFirstName());
        Glide.with(this)
                .load(user.getProfileUrl()).centerCrop().crossFade()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(profileImage);
    }

    private void resetUserData() {
        nameTextView.setText(getString(R.string.not_logged_in));
        profileImage.setImageResource(R.mipmap.ic_launcher);
    }

    private void openInitialFragment(Bundle savedInstanceState) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragmentTransaction.isEmpty() && savedInstanceState == null) {
            final Fragment fragment = ListArticlesFragment.newInstance(ListArticlesFragment.NEWS_FEED);
            openNavigationFragment(fragment);
        }
    }

    private void openLocationFragment(Bundle extra) {
//        final FragmentManager fragmentManager = getSupportFragmentManager();
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        final Fragment fragment = LocationFragment.newInstance();
        fragment.setArguments(extra);
        openNavigationFragment(fragment);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_profile:
                openActivity(VideoListDemoActivity.class);
                break;
            case R.id.nav_classes:
                openNavigationFragment(ClassesFragment.newInstance());
                break;
            case R.id.nav_news:
//                getFragmentManager().findFragmentById(fragmentId);
                openNavigationFragment(ListArticlesFragment.newInstance(ListArticlesFragment.NEWS_FEED));
                break;
            case R.id.nav_locations:
                openNavigationFragment(LocationFragment.newInstance());
                break;
            case R.id.nav_directory:
                openNavigationFragment(ListContactsFragment.newInstance());
                break;
            case R.id.nav_social:
                openNavigationFragment(SocialFragment.newInstance());
                break;
            case R.id.nav_images:
                openActivity(ImageGalleryActivity.class);
                break;
            case R.id.nav_courses:
                openNavigationFragment(ListCoursesFragment.newInstance());
                break;
            case R.id.nav_feedback:
                sendFeedback();
                break;
            case R.id.nav_about:
                openActivity(AboutActivity.class);
                break;
            case R.id.nav_setting:
                openSettingsActivity();
                break;
            case R.id.nav_video:
                openNavigationFragment(VideoFragment.newInstance());
                break;
            case R.id.nav_bookings:
                openActivity(BookingsOptionActivity.class);
                break;
            case R.id.nav_events:
                openNavigationFragment(ListEventsFragment.newInstance());
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        loginMenuItem = menu.findItem(R.id.action_login);
        logoutMenuItem = menu.findItem(R.id.action_logout);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You have logged out", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_login:
                openActivity(LoginActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void test() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url("https://www.googleapis.com/youtube/v3/search" +
                        "?key=AIzaSyAryJekXeJKack3QNGgduz01OgRoNT-7uM" +
                        "&channelId=UCIC723TRvp9y22K87gkjnpA" +
                        "&part=snippet,id&order=date&maxResults=20")
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "yt:fail", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (! response.isSuccessful()) Log.i(TAG, "request failed");

                        Log.i(TAG, "yt:success=" + response.body().string());
                    }
                });
    }

    private void sendFeedback() {
        final Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "msbm.mobile@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My feedback about MSBM Mobile App");
        startActivity(Intent.createChooser(emailIntent, "Send feedback..."));
    }

    private void openNavigationFragment(@NonNull Fragment fragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
//        Fragment oldFrag = fragmentManager.findFragmentById(fragment.getId());
//        if(oldFrag != null) {
//            Toast.makeText(this, "Old Fragment Found!", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, oldFrag.toString());
//        }
//        Log.e(TAG,"After Instance check");
        transaction.replace(containerFrameLayout.getId(), fragment, fragment.getTag());
        transaction.commit();
    }



    @Subscribe
    public void onEventMainThread(UserLoginEvent event) {
        try{
            loginMenuItem.setVisible(false);
            logoutMenuItem.setVisible(true);
        }catch(Exception e){
            Log.e("HomeActivity:=","onEventMainThread LoginMenuError");
        }
//        loginMenuItem.setVisible(false);
//        logoutMenuItem.setVisible(true);

        final FirebaseUser firebaseUser = event.getUser();
        final String id = firebaseUser.getUid();

        requestUserById(id);
    }

    private void requestUserById(@NonNull String id) {
        userRef.child(id)
                .child("moodle")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Log.i(TAG, "failed to load user");
                            return;
                        }
                        Log.i(TAG, "snapshot:" + dataSnapshot.toString());
                        Log.i(TAG, "user:" + user.toString());
                        loadUserData(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        FirebaseCrash.report(databaseError.toException());
                    }
                });
    }

    @Subscribe
    public void onEventMainThread(UserLogoutEvent event) {
        try{
            loginMenuItem.setVisible(true);
            logoutMenuItem.setVisible(false);
        }catch(Exception e){
            Log.e(TAG,e.toString());
        }

        try{
            LocationFragment.moodleLocation = "";
        }catch(Exception e ){
            Log.e(TAG, e.toString());
        }
//        loginMenuItem.setVisible(true);
//        logoutMenuItem.setVisible(false);

        resetUserData();
    }
}
