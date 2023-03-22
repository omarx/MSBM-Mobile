package com.fras.msbm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.fras.msbm.R;
import com.fras.msbm.models.directory.DirectoryEntry;

import java.util.List;

/**
 * Created by Antonio on 07/08/2016.
 */
public class DirectoryAdapter extends android.widget.BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<DirectoryEntry> mDirectoryEntries;

    public DirectoryAdapter(Context context, List<DirectoryEntry> directoryEntries){
        this.mContext = context;
        this.mDirectoryEntries = directoryEntries;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public static class ViewHolder{
        public ImageView iconView;
        public TextView nameView;
        public  TextView numberView;

//        public ViewHolder(View view){
//            iconView = (ImageView) view.findViewById(R.id.list_directory_icon);
//            nameView = (TextView) view.findViewById(R.id.list_name_directory_textview);
//            numberView = (TextView) view.findViewById(R.id.list_number_directory_textview);
//
//        }
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return mDirectoryEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return mDirectoryEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        DirectoryEntry entry = mDirectoryEntries.get(position);
        String dir_name;
        dir_name = entry.firstname + " " + entry.lastname;
        final ViewHolder mHolder;
        if(convertView == null){
            convertView = mInflater.from(mContext).inflate(R.layout.list_item_directory, parent, false);
            mHolder = new ViewHolder();

            convertView.setTag(mHolder);
        }else{
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.nameView = (TextView) convertView.findViewById(R.id.list_name_directory_textview);
        mHolder.iconView = (ImageView) convertView.findViewById(R.id.list_directory_icon);
        mHolder.nameView.setText(dir_name);
        mHolder.iconView.setBackgroundColor(Color.rgb(100, 100, 50));
        mHolder.iconView.setImageResource(R.drawable.ic_beer);

        return convertView;
    }

    public void update(List<DirectoryEntry> directoryEntries){
        this.mDirectoryEntries = directoryEntries;
    }


}