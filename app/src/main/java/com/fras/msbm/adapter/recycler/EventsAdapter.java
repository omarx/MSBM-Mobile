package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.models.Lecture;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 8/10/2016.
 */
public class EventsAdapter extends BaseAdapter<Lecture, EventsAdapter.LectureViewHolder> {
    public static final String TAG = EventsAdapter.class.getName();

    public EventsAdapter(Context context) {
        super(context);
    }

    @Override
    public LectureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_events, parent, false);
        return new LectureViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(LectureViewHolder holder, int position) {
        final Lecture lecture = getEntity(position);
        Log.e(TAG, lecture.toString());
        String time;
        if(lecture.getStartAt().equals("empty")){
            time = lecture.getDate() + ", All-Day Event";
        }else{
            Log.e(TAG, lecture.getStartAt() + " " + lecture.getEndAt());
            time = lecture.getDate() + ", " + lecture.getStartAt() + " - " + lecture.getEndAt();
        }
//        time = lecture.getDate() + ", " + lecture.getStartAt() + " - " + lecture.getEndAt();

//        holder.lecturerTextView.setText((! lecture.getLecturer().isEmpty()) ? lecture.getLecturer() : "");
        holder.locationTextView.setText(lecture.getLocation());
        holder.nameTextView.setText(lecture.getName());
        holder.timeTextView.setText(time);
        holder.dateTextView.setText(lecture.getDateString());

//        TextView blankText = (TextView) convertView.findViewById(R.id.blankView);
//        LinearLayout listView = (LinearLayout) convertView.findViewById(R.id.linear_layout_events);

//        if((Integer.parseInt(lecture.getRequest_type()) == 0)){
//            Log.e(TAG,"Empty Results");
//            try{
//                holder.listView.setVisibility(View.GONE);
//                holder.blankText.setVisibility(View.VISIBLE);
//            }catch(Exception e){
//                Log.e("Exceptions Thrown",e.toString());
//            }
//
//        }
    }

    public class LectureViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.text_lecturer) TextView lecturerTextView;
        @BindView(R.id.text_location) TextView locationTextView;
        @BindView(R.id.text_name) TextView nameTextView;
        @BindView(R.id.text_time) TextView timeTextView;
        @BindView(R.id.text_date) TextView dateTextView;

        @BindView(R.id.blankView) TextView blankText;
        @BindView(R.id.linear_layout_events) LinearLayout listView;

        public LectureViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
