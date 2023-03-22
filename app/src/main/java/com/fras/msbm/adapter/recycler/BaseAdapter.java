package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shane on 6/18/2016.
 */

//
public abstract class BaseAdapter<E, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    public final EventBus bus = EventBus.getDefault();
    public final Context context;
    public List<E> entities;

    public BaseAdapter(Context context, List<E> entities) {
        this.context = context;
        this.entities = entities;
    }

    public BaseAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public void setEntities(List<E> entities) {
        this.entities = entities;
        notifyDataSetChanged();
    }

    public void addEntities(List<E> newEntities) {
        entities.addAll(newEntities);
        notifyDataSetChanged();
    }

    public void addEntity(E entity) {
        entities.add(entity);
        notifyDataSetChanged();
    }

    public List<E> getEntities() {
        return entities;
    }

    public E getEntity(int position) {
        return getEntities().get(position);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }
}
