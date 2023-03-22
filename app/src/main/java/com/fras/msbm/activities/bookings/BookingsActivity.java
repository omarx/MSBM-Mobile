package com.fras.msbm.activities.bookings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fras.msbm.R;

import com.fras.msbm.activities.courses.ShowCourseActivity;
import com.fras.msbm.activities.general.BaseActivity;
import com.fras.msbm.dialogs.CustomTimePickerDialog;
import com.fras.msbm.events.network.NetworkDisconnectEvent;
import com.fras.msbm.models.User;
import com.fras.msbm.models.bookings.Booking;
import com.fras.msbm.models.bookings.BookingEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class BookingsActivity extends BaseActivity implements View.OnClickListener {
    //BOOKINGS CREATION
    private EditText startTime;
    private EditText bookingsName;
    private EditText bookingsCohort;
    private Spinner bookingsDuration;
    private SwipeSelector bookingsLocation;
    private String usarID;
    private DataSnapshot dataSnapshot;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference userRef = database.getReference("users");
    private FirebaseUser user;
    private ProgressDialog pDialog;
    private BookingEntity bookingSuccess;
    private DialogFragment  bFailDialog;
    private Intent intent;
    private String moodleID;

    private final static int TIME_PICKER_INTERVAL = 5;

    private static String POST_API = "http://mobile.antoniofearon.com/api/v1/bookings.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewsById();
        setStartTime();
        setGazebos();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null ) findUserById(user.getUid());

    }

    private void findUserById(@NonNull String userId) {
        userRef.child(userId).child("moodle")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        Log.i("TAG", "userCourses:" + user.toString());
                        moodleID = user.getUsername();
                        Log.i("moodleID", "userCourses:" + moodleID);
//                        Toast.makeText(BookingsActivity.this, "Token: " + user.getToken(), Toast.LENGTH_SHORT).show();
//                        loadUserCourses(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        showErrorViews();
                        FirebaseCrash.report(databaseError.toException());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
//        getMenuInflater().inflate(R.menu.menu_bookings, menu);
        tb.inflateMenu(R.menu.menu_bookings);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sendBooking:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                findUserById(user.getUid());
                if (user == null) {
                    Toast.makeText(this, "Must be logged in to make booking", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if( bookingsName.getText().toString().trim().equals("")){
                    Toast.makeText(this, "Name of Booking is Required.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if( bookingsCohort.getText().toString().trim().equals("")){
                    Toast.makeText(this, "Your Cohort is Required.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                String email = user.getEmail(); // TODO : pulls user's email
//                findUserById(user.getUid());
                String name = bookingsName.getText().toString();
                String cohort = bookingsCohort.getText().toString();
                String time = startTime.getText().toString();
                String duration = bookingsDuration.getSelectedItem().toString();
                SwipeItem location = bookingsLocation.getSelectedItem();
                String gazeboLocation = getSelectedGazebo(location);
                new POST(this).execute(name, cohort, time, duration, gazeboLocation, email, moodleID);
//                Toast.makeText(this, "Sending", Toast.LENGTH_LONG)
//                        .show();
                break;
            case R.id.action_settings:

                break;

            default:
                break;
        }

        return true;
    }

    private void findViewsById(){
        startTime = (EditText) findViewById(R.id.bookingsStartTime);
        startTime.setInputType(InputType.TYPE_NULL);

        bookingsName = (EditText) findViewById(R.id.bookingsName);
        bookingsCohort = (EditText) findViewById(R.id.bookingsCohort);
        bookingsLocation = (SwipeSelector) findViewById(R.id.bookingsLocation);
        bookingsDuration = (Spinner) findViewById(R.id.bookingsDuration);

    }

    private void setStartTime(){
        startTime.setOnClickListener(this);
        //to be place in appropriate place

    }


    private void setGazebos(){
        bookingsLocation = (SwipeSelector) findViewById(R.id.bookingsLocation);
        bookingsLocation.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Gazebo one", ""),
                new SwipeItem(1, "Gazebo two", ""),
                new SwipeItem(2, "Gazebo three", ""),
                new SwipeItem(3, "Gazebo four", ""),
                new SwipeItem(4, "Gazebo five", ""),
                new SwipeItem(5, "MSBM Finance Lab", ""),
                new SwipeItem(6, "MSBM Media Lab (Doc Centre)", "")
        );
    }

    private String getSelectedGazebo(SwipeItem swipeItem){
        String location = "";
        Integer selected = (Integer) swipeItem.value;
        if(selected == 0){
            location = "Gazebo One";
        }else if(selected == 1){
            location = "Gazebo Two";
        }else if(selected == 2){
            location = "Gazebo Three";
        }else if(selected == 3){
            location = "Gazebo Four";
        }else if(selected == 4){
            location = "Gazebo Five";
        }else if(selected == 5){
            location = "MSBM Finance Lab";
        }else if(selected == 6){
            location = "MSBM Media Lab (Doc Centre)";
        }
        return location;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.bookingsStartTime:

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
//                TimePickerDialog mTimePicker;
                CustomTimePickerDialog mTimePicker;
                mTimePicker = new CustomTimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.setText(new StringBuilder().append(pad(selectedHour)).append(":").append(pad(selectedMinute)));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

                break;
//            case R.id.swipeSelector:

//                break;

        }

    }

    //    tvDisplayTime.setText(new StringBuilder().append(pad(hour))
//    					.append(":").append(pad(minute)));
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    class POST extends AsyncTask<String, String, String> {
        private Context context;

        public POST(@NonNull Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BookingsActivity.this);
            pDialog.setMessage("Creating Bookings");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            BookingEntity booking;
//            ResponseAPI responseAPI;
            InputStream inputStream = null;
            String result = "";
            try {


//                String email = user.getEmail();
//                String token = user.get
//                String token = user.getToken(true);
                Gson gson = new GsonBuilder().create();
                booking = new BookingEntity();
                booking.setName(args[0]);
                booking.setCohort(args[1]);
                booking.setTime(args[2]);
                booking.setDuration(args[3]);
                booking.setLocation(args[4]);
                booking.setEmail(args[5]);
                booking.setMoodleID(args[6]);
                booking.setRequest_type("create");
                Log.e("MOODLE_ID", "got: " + args[6]);
                String bookingData = gson.toJson(booking);

                Log.e("Duration",args[3]);

                URL url = new URL(POST_API);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                urlConnection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);


                DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
                dStream.writeBytes(bookingData); //Writes out the string to the underlying output stream as a sequence of bytes
                dStream.flush(); // Flushes the data output stream.
                dStream.close(); // Closing the output stream.

                Log.e("POST ----->>", bookingData);

                int responseCode = urlConnection.getResponseCode();


                BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
//                while ((output = br.readLine()) != null) {
//                    sb.append(output);
//                }
//                Log.e("Printed Response: ", sb.toString());
                bookingSuccess = gson.fromJson(br, BookingEntity.class);
                Log.e("Current Success: ", booking.getSuccess());


            } catch (Exception e) {
                Log.e("InputStream=", e.getLocalizedMessage());
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            runOnUiThread(() -> {
                //update ui here
                // display toast here
                if(Integer.parseInt(bookingSuccess.getSuccess()) == 1){
                    intent = new Intent(BookingsActivity.this, BookingDetailsActivity.class);
                    intent.putExtra("EVENT_ID",bookingSuccess.getEventID());
                    Toast.makeText(BookingsActivity.this, "Bookings Created", Toast.LENGTH_SHORT)
                            .show();
                    startActivity(intent);
                }else if(Integer.parseInt(bookingSuccess.getSuccess()) == 0){
                    bFailDialog = new BookingsFailDialog();
                    bFailDialog.show(getSupportFragmentManager(),"Test");
                    //newFragment.show(getSupportFragmentManager(), "missiles");
                }

            });

            pDialog.dismiss();

        }

    }

    @Subscribe
    public void onEvent(NetworkDisconnectEvent event) {

    }

    public static class BookingsFailDialog extends DialogFragment {

        public BookingsFailDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Booking Creation Failed. Please Review Submission and try Again.")
//                    .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // FIRE ZE MISSILES!
//                        }
//                    })
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
