package com.fras.msbm.activities.general;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.fras.msbm.R;

public class LocationDetailActivity extends AppCompatActivity {

    private Bundle extras;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        description = (TextView) this.findViewById(R.id.descContent);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        extras = getIntent().getExtras();

        setUpToolBar();
        description.setText(extras.getString("DESCRIPTION"));

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setUpToolBar()
    {
        String name = extras.getString("NAME");
        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
