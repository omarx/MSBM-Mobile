package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.events.CourseClickEvent;
import com.fras.msbm.models.courses.Course;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 6/18/2016.
 */
public class CourseAdapter extends BaseAdapter<Course, CourseAdapter.CourseViewHolder> {
    public static final String TAG = CourseAdapter.class.getSimpleName();

    private EventBus bus = EventBus.getDefault();

    public CourseAdapter(Context context) {
        super(context, new ArrayList<>());
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_course, parent, false);
        return new CourseViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        final Course course = getEntities().get(position);
        holder.nameTextView.setText(course.getShortName());
        holder.fullNameTextView.setText(course.getFullName());
        holder.cardViewContainer.setOnClickListener(v -> bus.post(new CourseClickEvent(course)));
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view_container) CardView cardViewContainer;
        @BindView(R.id.text_name) TextView nameTextView;
        @BindView(R.id.text_full_name) TextView fullNameTextView;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
