package com.fras.msbm.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fras.msbm.R;

/**
 * Created by Shane on 8/25/2016.
 */
public class ImageDialog extends AppCompatActivity {

    ImageView imageView;

    boolean isImageFitToScreen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog_layout);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        imageView = (ImageView) findViewById(R.id.your_image);

        Bundle extras = getIntent().getExtras();
        String imageURL = extras.getString("IMAGE_URL");
        //        String otherURL = "http://www.visitbuffaloniagara.com/content/uploads/2014/11/chicken-wings-2-1200x675.jpg";

        Glide.with(ImageDialog.this)
                .load(imageURL)
                //                .placeholder(R.drawable.parallax_travel_small)
                .into(imageView);

        Log.e("Inside Dialog","NAANIII");
        Log.e("Inside Dialog URL",imageURL);
        //        imageView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                if(isImageFitToScreen) {
        //        isImageFitToScreen=false;
        //        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //        imageView.setAdjustViewBounds(true);
        //                }else{
        //                    isImageFitToScreen=true;
        //        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //                }
        //            }
        //        });

    }
}