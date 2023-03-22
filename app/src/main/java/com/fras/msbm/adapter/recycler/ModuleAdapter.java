package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.events.clicks.ModuleClickEvent;
import com.fras.msbm.models.courses.Content;
import com.fras.msbm.models.courses.Module;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 7/3/2016.
 */
public class ModuleAdapter extends BaseAdapter<Module, ModuleAdapter.ModuleViewHolder> {
    public static final String TAG = ModuleAdapter.class.getSimpleName();

    public ModuleAdapter(Context context, List<Module> entities) {
        super(context, entities);
    }

    @Override
    public ModuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_course_contents, parent, false);
        return new ModuleViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ModuleViewHolder holder, int position) {
        final Module module = getEntity(position);
        holder.nameTextView.setText(module.getName());

        final List<Content> contents = module.getContents();

//        for (int i = 0; i < contents.size(); i++)
//            Log.i(TAG, "contents[" + i + "] = " + contents.get(i));

        if (contents == null || contents.size() < 1) return;

        final Content content = contents.get(0);

        Log.i(TAG, "content=" + content.toString());

        if (content.getType().equals("url"))
            holder.nameTextView.setTextColor(Color.BLUE);

        if (content.getType().equals("file"))
            holder.nameTextView.setTextColor(Color.RED);

        if (content.getType().equals("url") || content.getType().equals("file")) {
            holder.containerLinearLayout.setOnClickListener(view -> bus.post(new ModuleClickEvent(module)));
        }
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.linear_layout_container) LinearLayout containerLinearLayout;
        @BindView(R.id.text_name) TextView nameTextView;

        public ModuleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
