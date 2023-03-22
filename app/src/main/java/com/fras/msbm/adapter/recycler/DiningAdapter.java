package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.events.clicks.DiningLocationSelectedEvent;
import com.fras.msbm.models.locations.Location;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 7/27/2016.
 */
public class DiningAdapter extends BaseAdapter<Location, DiningAdapter.DiningViewHolder>{

    public DiningAdapter(Context context, List<Location> entities) {
        super(context, entities);
    }

    @Override
    public DiningViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_dining, parent, false);
        return new DiningViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(DiningViewHolder holder, int position) {
        final Location location = getEntity(position);
        holder.nameTextView.setText(location.getDisplayName());
        holder.containerLayoutView.setOnClickListener(view ->
            bus.post(new DiningLocationSelectedEvent(location)));
    }

    class DiningViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_name) TextView nameTextView;
        @BindView(R.id.layout_container)
        LinearLayout containerLayoutView;

        public DiningViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}