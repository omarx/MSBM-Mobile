package com.fras.msbm.activities.bookings;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.fras.msbm.R;
import com.fras.msbm.models.bookings.BookingEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookingDetailsActivity extends AppCompatActivity {

    ImageView iv;
    String eventID;
    TextView bookingName;
    TextView bookingDate;
    TextView bookingCohort;
    TextView bookingStart;
    TextView bookingEnd;
    TextView bookingConfirmation;
    TextView bookingLocation;
    BookingEntity[] bookArray;
    BookingEntity bookingEntity;
    private ProgressDialog pDialog;
    private PullToZoomScrollViewEx scrollView;

    private final String USER_AGENT = "Mozilla/5.0";
    //    private static String POST_API = "http://10.32.0.175/api/v1/bookings.php";
    private static String POST_API = "http://mobile.antoniofearon.com/api/v1/bookings.php";
//    private static String POST_API = "http://10.32.0.87/api/v1/bookings.php"; // http://mobile.antoniofearon.com/api/v1/directory.php
//    private static String POST_API = "http://172.16.116.205/api/v1/bookings.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        eventID = extras.getString("EVENT_ID");
        loadViews();

        new LoadBooking().execute();

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);


//        Toast.makeText(this,eventID, Toast.LENGTH_SHORT).show();
    }

    public void loadViews(){
        iv = (ImageView) findViewById(R.id.header_parallax_travel_image);
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);

        View headView = LayoutInflater.from(this).inflate(R.layout.header_booking_details, null, false);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.booking_detail_zoom_view, null, false);
        View contentView = LayoutInflater.from(this).inflate(R.layout.booking_details_content_view, null, false);

//        bookingName = (TextView) findViewById(R.id.bookings_detail_name);

        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        bookingName = (TextView) findViewById(R.id.bookings_detail_name);
//        bookingCohort = (TextView) findViewById(R.id.bookings_detail_cohort);
        bookingDate = (TextView) findViewById(R.id.bookings_date_name);

        bookingStart = (TextView) findViewById(R.id.bookings_detail_starttime);
        bookingEnd = (TextView) findViewById(R.id.bookings_detail_endtime);
        bookingConfirmation  = (TextView) findViewById(R.id.bookings_detail_confirmation);
        bookingLocation = (TextView) findViewById(R.id.bookings_detail_location);
    }

//    private void loadViewForCode() {
//        PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
//        View headView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null, false);
//        View zoomView = LayoutInflater.from(this).inflate(R.layout.profile_zoom_view, null, false);
//        View contentView = LayoutInflater.from(this).inflate(R.layout.profile_content_view, null, false);
//        scrollView.setHeaderView(headView);
//        scrollView.setZoomView(zoomView);
//        scrollView.setScrollContentView(contentView);
//    }

    class LoadBooking extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BookingDetailsActivity.this);
//            pDialog.setMessage("Loading contacts. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            Log.e("XYXYX","Getting Booking");

            BookingEntity booking;
            InputStream inputStream = null;
            String result = "";
            try{
                Gson gson = new GsonBuilder().create();
                booking = new BookingEntity();
                booking.setRequest_type("details");
                booking.setEventID(eventID);
                String bookingData = gson.toJson(booking);

                URL url = new URL(POST_API);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
//              urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                Log.e("Printed Response: ", sb.toString());

//                bookArray = gson.fromJson(sb.toString(), BookingEntity[].class);
                bookingEntity = gson.fromJson(sb.toString(), BookingEntity.class);
                Log.e("SEEE", bookingEntity.getName());
//                if(bookArray.length == 1){
//                    Log.e("Success",bookArray[0].getSuccess());
//                }

            }catch(Exception e){
                Log.e("InputStream=", e.getLocalizedMessage());
                Log.e("Bookings","Download Exception");
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            Log.e("XYXYX","Details postExecute");
//            sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
//            Log.e("Excecute", Integer.toString(sortDirList.size()));
//            Log.e("Cehck", sortDirList.get(0).toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bookingName.setText(bookingEntity.getName());
//                    bookingCohort.setText(bookingEntity.getCohort());
                    bookingDate.setText(bookingEntity.getEventDate());
                    bookingStart.setText(bookingEntity.getEventStart());
                    bookingEnd.setText(bookingEntity.getEventEnd());
                    Integer confirmVal = bookingEntity.getConfirmValue();
                    bookingConfirmation.setText(bookingEntity.getConfirmation());
                    bookingLocation.setText(bookingEntity.getLocation());
                    if(confirmVal == 0){
                        Log.e("DETAILS IF","PENDING");
                        bookingConfirmation.setBackgroundColor(ContextCompat.getColor(BookingDetailsActivity.this, R.color.booking_pending)); //getResources().getColor(R.color.colorAccent)
                    }else if(confirmVal == 1){
                        Log.e("DEATILS ELSE IF","CONFIRMED");
                        bookingConfirmation.setBackgroundColor(ContextCompat.getColor(BookingDetailsActivity.this, R.color.booking_confirm));
                    }else{
                       Log.e("CONFIRM",bookingEntity.getConfirmation());
                    }
                }
            });

            pDialog.dismiss();

        }

    }
}
