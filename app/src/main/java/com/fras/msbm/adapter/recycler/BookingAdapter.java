package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.models.bookings.Booking;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 8/27/2016.
 */
public class BookingAdapter extends BaseAdapter<Booking, BookingAdapter.BookingViewHolder> {


    public BookingAdapter(Context context) {
        super(context);
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.item_row_booking, parent);
        return new BookingViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        final Booking booking = getEntity(position);
        holder.textLocation.setText(booking.getLocation());
        holder.textTime.setText(booking.getEventStart());
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_location) TextView textLocation;
        @BindView(R.id.text_time) TextView textTime;

        public BookingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
