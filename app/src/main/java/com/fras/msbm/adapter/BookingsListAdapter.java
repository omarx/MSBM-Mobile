package com.fras.msbm.adapter;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.models.bookings.BookingEntity;

import java.util.List;

/**
 * Created by Antonio on 10/07/2016.
 */
public class BookingsListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<BookingEntity> mBookingEntries;

    public BookingsListAdapter(Context context, List<BookingEntity> bookingEntities){
        this.mContext = context;
        this.mBookingEntries = bookingEntities;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class viewHolder{
        public ImageView iconView;
        public TextView nameView;
        public TextView startTime;
        public TextView startEnd;
        public TextView cohort;
        public TextView location;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return mBookingEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return mBookingEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 111;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final viewHolder mHolder;
        BookingEntity bookingEntity = mBookingEntries.get(position);

        if(convertView == null){
            convertView = mInflater.from(mContext).inflate(R.layout.list_item_bookings, parent, false);
            mHolder = new viewHolder();


            convertView.setTag(mHolder);
        }else{
            mHolder = (viewHolder) convertView.getTag();
        }

        mHolder.nameView = (TextView) convertView.findViewById(R.id.list_item_bookings_name);
        mHolder.iconView = (ImageView) convertView.findViewById(R.id.list_item_bookings_image);
        mHolder.startTime = (TextView) convertView.findViewById(R.id.list_item_bookings_starttime);
        mHolder.startEnd = (TextView) convertView.findViewById(R.id.list_item_bookings_endtime);
        mHolder.location = (TextView) convertView.findViewById(R.id.list_item_bookings_location);

        mHolder.nameView.setText(bookingEntity.getName());
        mHolder.startTime.setText(bookingEntity.getEventStart());
        mHolder.startEnd.setText(bookingEntity.getEventEnd());
        mHolder.location.setText(bookingEntity.getLocation());
        mHolder.iconView.setBackgroundColor(Color.rgb(100, 100, 50));
        mHolder.iconView.setImageResource(R.drawable.parallax_travel_small);
        TextView blankText = (TextView) convertView.findViewById(R.id.blankView);
        LinearLayout listView = (LinearLayout) convertView.findViewById(R.id.linear_layout_booking);
        // RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.gallery_relative_layout2);
//        Log.e("-----xxxxxx----","I Try");
//        Log.e("-----TYPE----",bookingEntity.getRequest_type());
//        Log.e("-----SIZE----",Integer.toString(mBookingEntries.size()));
//        Log.e("-----LOGIC----",String.valueOf(bookingEntity.getRequest_type().toString() == "empty"));
        if((Integer.parseInt(bookingEntity.getRequest_type()) == 0)){
//            Log.e("BookingAdapter","I LIIIIIVEE");
            try{
                listView.setVisibility(View.GONE);
                blankText.setVisibility(View.VISIBLE);
            }catch(Exception e){
                Log.e("Exceptions Thrown",e.toString());
            }

        }


        return convertView;
    }

    public void update(List<BookingEntity> bookingEntries){
        this.mBookingEntries = bookingEntries;
    }

}
