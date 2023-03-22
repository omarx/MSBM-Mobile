package com.fras.msbm.activities.bookings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fras.msbm.R;
import com.fras.msbm.adapter.BookingsListAdapter;
import com.fras.msbm.models.bookings.BookingEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookingsListActivityFragment extends Fragment {


    private BookingsListAdapter mBookingAdapter;

    private ProgressDialog pDialog;

    private final String USER_AGENT = "Mozilla/5.0";
//    private static String POST_API = "http://10.32.0.175/api/v1/bookings.php";
    private static String POST_API = "http://mobile.antoniofearon.com/api/v1/bookings.php";
//    private static String POST_API = "http://10.32.0.87/api/v1/bookings.php"; //
//    private static String POST_API = "http://172.16.116.205/api/v1/bookings.php";
    BookingEntity[] bookArray;
    List<BookingEntity> bookings = new ArrayList<>();

    public BookingsListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new LoadBookings().execute();

//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));
//        bookings.add(new BookingEntity("uno","buff","ruff","tuff","huff"));


//        myListAdapter = new MyListAdapter(getActivity());
        mBookingAdapter = new BookingsListAdapter(getActivity(), bookings); //listview_bookings

        View rootView = inflater.inflate(R.layout.fragment_bookings_list, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_bookings);

//        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mBookingAdapter);
        SwingBottomInAnimationAdapter animationAdapter =  new SwingBottomInAnimationAdapter(new SwingRightInAnimationAdapter(mBookingAdapter));
        //mAnimAdapter = new SwingBottomInAnimationAdapter(new SwingRightInAnimationAdapter(mAdapter));
        animationAdapter.setAbsListView(listView);

        listView.setAdapter(animationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Object o = listView.getItemAtPosition(position);
//                String str=(String)o;//As you are using Default String Adapter
//                String selectedValue = (String) getListAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), BookingDetailsActivity.class);

                BookingEntity obj = (BookingEntity)listView.getAdapter().getItem(position);
                String eventID = obj.getEventID();

                intent.putExtra("EVENT_ID",eventID);
                startActivity(intent);
                Toast.makeText(getActivity(),eventID, Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadBookings extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Checking Bookings. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            BookingEntity booking;
            InputStream inputStream = null;
            String result = "";
            try{
                Gson gson = new GsonBuilder().create();
                booking = new BookingEntity();
                booking.setRequest_type("list");
                String bookingData = gson.toJson(booking);

                URL url = new URL(POST_API);
                Log.e("BEFORE CONNECTION","XXXXXX");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Log.e("After CONNECTION","XXXXXX");
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

                bookArray = gson.fromJson(sb.toString(), BookingEntity[].class);
//                Log.e("Size", Integer.toString(bookArray.length));
                if(bookArray.length == 1){
                    Log.e("Success",bookArray[0].getSuccess());
                    Log.e("Size",Integer.toString(bookArray.length));
                    Log.e("Request Type ",bookArray[0].getRequest_type());
                }

            }catch(Exception e){
                Log.e("InputStream=", e.getLocalizedMessage());
                Log.e("Bookings","Download Exception");
            }

            return null;
        }


        protected void onPostExecute(String args){
            Log.e("Excecute", "------");
            Log.e("Cehck", "-----");
//            Arrays.asList(bookArray);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mBookingAdapter.update(Arrays.asList(bookArray));
                    mBookingAdapter.notifyDataSetChanged();
                }
            });

            pDialog.dismiss();

        }
    }

}
