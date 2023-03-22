package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.models.locations.OpeningHours;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 7/30/2016.
 */
public class OpeningHoursAdapter extends BaseAdapter<OpeningHours, OpeningHoursAdapter.OpeningHoursViewHolder> {

    public OpeningHoursAdapter(Context context, List<OpeningHours> entities) {
        super(context, entities);
    }

    @Override
    public OpeningHoursViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_opening_hours, parent, false);
        return new OpeningHoursViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(OpeningHoursViewHolder holder, int position) {
        final OpeningHours hours = getEntity(position);
        final String time = hours.getStart() + " - " + hours.getEnd();
        holder.nameTextView.setText(hours.getTitle());
        holder.timeTextView.setText(time);
    }

    public static class OpeningHoursViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_name) TextView nameTextView;
        @BindView(R.id.text_time) TextView timeTextView;

        public OpeningHoursViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}