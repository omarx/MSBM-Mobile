package com.fras.msbm.gallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.fras.msbm.R;
import com.fras.msbm.constants.MSBMConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Shane on 8/25/2016.
 */
public class ImageGalleryActivity extends AppCompatActivity {
    private ListView mListView;
    List<ImageEntity> images = new ArrayList<>();
    ImageGalleryAdapter mGalleryAdapter;
    ImageEntity[] imageEntities;
    private RecyclerView recyclerView;
    private GalleryAdapter mGridAdapter;

    private ProgressDialog pDialog;

    private static String POST_API =  MSBMConstants.PHOTOS_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.gallery_list_view);
        setContentView(R.layout.recycler_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mListView = (ListView) findViewById(R.id.list_view);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //        images.add(new ImageEntity("Test 1", "http://g01.a.alicdn.com/kf/HTB1TI5ZJVXXXXahaXXXq6xXFXXXY/Hotapei-font-b-Sexy-b-font-font-b-Kitty-b-font-Costume-LC8775-new-2016-wonder.jpg","Tester",1));
        //        images.add(new ImageEntity("Test 2", "http://i.imgur.com/DLXgQOL.jpg","Tester 2", 1));
        //        images.add(new ImageEntity("Test 3", "http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=96366694","Tester 3",1));
        //        images.add(new ImageEntity("Test 4", "https://dorkarama.files.wordpress.com/2012/10/resident-evil-retribution-resident-evil-retribution-32177903-1024-7682.jpg","Tester 4",1));
        //        images.add(new ImageEntity("Test 5", "http://m.flikie.com/wallpaper/download?paperId=33565874","Tester 5",1));
        //        images.add(new ImageEntity("Test 6", "http://images.akamai.steamusercontent.com/ugc/53248336537319992/CEA1F1ABDCEFF1082B68DE137AC9EEBD77854AB0/","Tester 6",1));
        //        images.add(new ImageEntity("Test 7", "https://s-media-cache-ak0.pinimg.com/236x/f2/48/9e/f2489e566a7765c4d7e45df7f0641adc.jpg","Tester 7",1));
        //        images.add(new ImageEntity("Test y", "http://i.imgur.com/DLXgQOL.jpg","Tester 2"));
        //        images.add(new ImageEntity("Test z", "http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=96366694","Tester 3"));

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                int tag = (int) view.getTag();
                int pos = Arrays.asList(imageEntities).get(position).getFolderID();
//                Toast.makeText(view.getContext(), "Folder id: " + pos, Toast.LENGTH_SHORT)
//                        .show();
                Intent intent = new Intent(ImageGalleryActivity.this, ImageGalleryPhotoActivity.class);
                intent.putExtra("FOLDER_ID",Integer.toString(pos));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        setAdapter(images);
        setRecyclerAdapter(images);
        //        return rootView;
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

    public void setAdapter(List<ImageEntity> images){
        mGalleryAdapter = new ImageGalleryAdapter(this, images);
        mListView.setAdapter(mGalleryAdapter);
        Log.e("Adapter", "Set");
        new getFolders().execute();
    }

    public void setRecyclerAdapter(List<ImageEntity> images){
        mGridAdapter = new GalleryAdapter(getApplicationContext(), images);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mGridAdapter);
        Log.e("Recycler Adapter", "Set");
        new getFolders().execute();
    }

    class getFolders extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ImageGalleryActivity.this);
            pDialog.setMessage("Loading Images. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            ImageEntity imageEntity = new ImageEntity();
            Gson gson = new GsonBuilder().create();
            try {
                //                directoryResponse = new DirectoryResponse();
                imageEntity.setIsFolder(1);

                String folderData = gson.toJson(imageEntity);

                URL url = new URL(POST_API);
                Log.e("BEFORE CONNECTION","XXXXXX");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Log.e("After CONNECTION","XXXXXX");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                urlConnection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
                dStream.writeBytes(folderData); //Writes out the string to the underlying output stream as a sequence of bytes
                dStream.flush(); // Flushes the data output stream.
                dStream.close();

                //              int responseCode = urlConnection.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
//                Log.e("Printed Response: ", sb.toString());

                imageEntities = gson.fromJson(sb.toString(), ImageEntity[].class);
                //
                Log.e("Size", "Threr");
            }catch(Exception e){
                Log.e("InputStream=", e.getLocalizedMessage());
                Log.e("Bookings","Download Exception");
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
//                    mGalleryAdapter.update(Arrays.asList(imageEntities));
//                    mGalleryAdapter.notifyDataSetChanged();
                    mGridAdapter.update(Arrays.asList(imageEntities));
                    mGridAdapter.notifyDataSetChanged();
                }

            });
            pDialog.dismiss();
        }

    }
}
