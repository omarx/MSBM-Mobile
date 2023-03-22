package com.fras.msbm.activities.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fras.msbm.R;

public class BookingsOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public void createBooking(final View view){
        Intent intent = new Intent(this, BookingsActivity.class);
        startActivity(intent);
    }

    public void bookingsList(final View view){
        Intent intent = new Intent(this, BookingsListActivity.class);
        startActivity(intent);
    }

    public void userBookings(final View view){
        Intent intent = new Intent(this, BookingUserList.class);
        startActivity(intent);
    }
}
