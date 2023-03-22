package com.fras.msbm.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fras.msbm.R;
import com.fras.msbm.adapter.DirectoryAdapter;
import com.fras.msbm.models.directory.DirectoryEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DirectoryActivityFragment extends Fragment {

    private DirectoryAdapter mDirectoryAdapter;


    private ProgressDialog pDialog;

    ArrayList<DirectoryEntry> directoryArray;
    private List<DirectoryEntry> sortDirList;

    private static String POST_API = "http://mobile.antoniofearon.com/api/v1/directory.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private final String USER_AGENT = "Mozilla/5.0";

    List<DirectoryEntry> entries = new ArrayList<>();

    DirectoryEntry[] dirArray;

    public DirectoryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new LoadDirectory().execute();

        mDirectoryAdapter = new DirectoryAdapter(getActivity(), entries);

        View rootView =  inflater.inflate(R.layout.fragment_directory, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_directory);

        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mDirectoryAdapter);
        animationAdapter.setAbsListView(listView);

        listView.setAdapter(animationAdapter);
        Log.e("XXX","---Fragment---");

        return rootView;
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadDirectory extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading contacts. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
//            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
            Log.e("Start  ","Start Background");
//            try{
//                sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
//                if(!sortDirList.isEmpty())
//                    DirectoryEntry.deleteAll(DirectoryEntry.class);
//                Log.e("XXXXX  ","Works");
//            }catch(Exception e){
//                Log.e("XXXXX","Exception thrown");
//            }
//            sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
            try {
//                directoryResponse = new DirectoryResponse();
                Log.e("Start  ","Test 1 ---------------------");
                URL url = new URL(POST_API);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                Log.e("Start  ","Test 2 ---------------------");
                //add request header
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
                Log.e("Start  ","Test 3 ---------------------");
                Integer responseCode = urlConnection.getResponseCode();
                Log.e("XYXYX",responseCode.toString());
                Log.e("Start  ","Test 4 ---------------");
//                Log.e("Start  ","Test 5 ---------------");
                Reader reader = new InputStreamReader(urlConnection.getInputStream());
//
                Gson gson = new GsonBuilder().create();
//
                Log.e("Size", "Threr");
                dirArray = gson.fromJson(reader, DirectoryEntry[].class);
//
                Log.e("Size", Integer.toString(dirArray.length));

                Collections.sort(Arrays.asList(dirArray), DirectoryEntry.NameComparator);

                Log.e("Test ", "After assign");
//
                if(responseCode == 200) {
                    Log.e("Data Returned","Get");

                } else {
                    Log.e("YYY  ",responseCode.toString());

                }

            }catch(MalformedURLException e) {
                e.printStackTrace();
                Log.e("XXXX","Mal");
            }catch(Exception e) {
                Log.e("XXXX","Except");
                e.printStackTrace();
            }

            try{ //check if directory has been updated
                sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);

//                Log.e("Saved List:= ",Integer.toString(sortDirList.size()));
//                Log.e("New List:= ",Integer.toString(dirArray.length));
                if((dirArray != null) && (sortDirList.size() != dirArray.length) ){
                    if(!sortDirList.isEmpty()){
                        DirectoryEntry.deleteAll(DirectoryEntry.class);
                    }
                    for(DirectoryEntry entry: dirArray){
//                    Log.e("Check", entry.toString());
                        entry.save();
//                    Log.e("Saved", entry.lastname);
                    }
                }

            }catch (Exception e){
                Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

//          Log.e("After", dirArray[1].lastname);
//          entries = Arrays.asList(dirArray);
            Log.e("XYXYX","iN POST Exce");
            sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
            Log.e("Excecute", Integer.toString(sortDirList.size()));
            Log.e("Cehck", sortDirList.get(0).toString());
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mDirectoryAdapter.update(sortDirList);
                    mDirectoryAdapter.notifyDataSetChanged();
                }
            });


            pDialog.dismiss();

        }

    }

}
