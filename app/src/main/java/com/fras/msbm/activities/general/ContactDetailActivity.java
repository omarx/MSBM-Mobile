package com.fras.msbm.activities.general;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fras.msbm.R;

public class ContactDetailActivity extends AppCompatActivity {

    private TextView contactTile;
    private TextView contactName;
    private TextView contactNumber;
    private TextView contactUnit;
    private TextView contactEmail;
    private TextView contactOffice;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        extras = getIntent().getExtras();

        setValues();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void setupViews(){
        contactTile = (TextView) findViewById(R.id.contact_title_name);
        contactName = (TextView) findViewById(R.id.contact_name);
        contactUnit = (TextView) findViewById(R.id.contact_unit_text);
        contactOffice = (TextView) findViewById(R.id.contact_office_text);
        contactEmail = (TextView) findViewById(R.id.contact_email_text);
        contactNumber = (TextView) findViewById(R.id.contact_tele_text);
    }

    public void setValues(){
        setupViews();

        contactTile.setText(extras.getString("TITLE"));
        contactName.setText(extras.getString("NAME"));
        contactUnit.setText(extras.getString("UNIT"));
        contactOffice.setText(extras.getString("OFFICE"));
        contactEmail.setText(extras.getString("EMAIL"));
        contactNumber.setText(extras.getString("NUMBER"));


    }

}
