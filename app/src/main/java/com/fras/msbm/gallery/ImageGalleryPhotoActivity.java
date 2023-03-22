package com.fras.msbm.gallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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
public class ImageGalleryPhotoActivity extends AppCompatActivity {

    private ListView mListView;
    List<ImageEntity> images = new ArrayList<>();
    ImageGalleryPhotoAdapter mGalleryAdapter;
    ArrayList<String> urls = new ArrayList<String>();

    ImageEntity[] imageEntities;
    Integer folderID;
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private GalleryAdapter mGridAdapter;


    private static String POST_API = MSBMConstants.PHOTOS_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.photo_list_view);
        setContentView(R.layout.recycler_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mListView = (ListView) findViewById(R.id.list_view);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Bundle extras = getIntent().getExtras();
        folderID = Integer.parseInt(extras.getString("FOLDER_ID"));
        Log.e("FolderID:= ",folderID.toString());



        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Bundle bundle = new Bundle();
////                bundle.putSerializable("images", images);
//                bundle.putStringArrayList("urls",urls);
//                bundle.putInt("position", position);
////                bundle.put
//
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
//                newFragment.setArguments(bundle);
//                newFragment.show(ft,"Slide");
                String imageURL = Arrays.asList(imageEntities).get(position).getImageURL();
                Intent intent = new Intent(ImageGalleryPhotoActivity.this, ImageDialog.class);
                intent.putExtra("IMAGE_URL", imageURL);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


//        setAdapter(images);
        setRecyclerAdapter(images);

    }

    public void setAdapter(List<ImageEntity> images){
        mGalleryAdapter = new ImageGalleryPhotoAdapter(this, images);

        mListView.setAdapter(mGalleryAdapter);
        new getPhotos().execute();
    }

    public void setRecyclerAdapter(List<ImageEntity> images){
        mGridAdapter = new GalleryAdapter(getApplicationContext(), images);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mGridAdapter);
        Log.e("Recycler Adapter", "Set");
        new getPhotos().execute();
    }

    class getPhotos extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ImageGalleryPhotoActivity.this);
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
                imageEntity.setIsFolder(0);
                imageEntity.setFolderID(folderID);

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

                for(ImageEntity entity: imageEntities){
                    urls.add(entity.getImageURL());
                }
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
