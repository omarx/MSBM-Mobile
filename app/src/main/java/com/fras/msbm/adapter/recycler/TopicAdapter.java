package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.models.courses.Module;
import com.fras.msbm.models.courses.Topic;
import com.fras.msbm.views.recyclers.BottomOffsetDecoration;
import com.fras.msbm.views.recyclers.SimpleItemDivider;
import com.fras.msbm.views.recyclers.TopOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 7/2/2016.
 */
public class TopicAdapter extends BaseAdapter<Topic, TopicAdapter.TopicViewHolder> {
    public static final String TAG = TopicAdapter.class.getSimpleName();

    public TopicAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public TopicAdapter(Context context, List<Topic> topics) {
        super(context, topics);
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_topic, parent, false);
        return new TopicViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        final Topic topic = getEntity(position);
        final List<Module> modules = topic.getModules();

//        for (int i = 0; i < modules.size(); i++)
//            Log.i(TAG, "module[" + i + "] = " + modules.get(i));

        final ModuleAdapter moduleAdapter = new ModuleAdapter(context, modules);

        holder.textViewName.setText(topic.getName()) ;
        holder.recyclerViewContents.setAdapter(moduleAdapter);
        holder.recyclerViewContents.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewContents.addItemDecoration(new SimpleItemDivider(context.getResources().getDrawable(R.drawable.divider_space)));
        holder.recyclerViewContents.addItemDecoration(new TopOffsetDecoration(8));
        holder.recyclerViewContents.addItemDecoration(new BottomOffsetDecoration(8));
        holder.recyclerViewContents.setHasFixedSize(true);

        final boolean isContentLoaded = modules.size() > 0;
        final int isVisible = (isContentLoaded) ? View.GONE : View.VISIBLE;

        holder.textViewEmpty.setVisibility(isVisible);
    }

    final class TopicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view_container) CardView cardViewContainer;
        @BindView(R.id.recycler_contents) RecyclerView recyclerViewContents;
        @BindView(R.id.text_name) TextView textViewName;
        @BindView(R.id.text_empty) TextView textViewEmpty;

        TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
