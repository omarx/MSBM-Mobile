package com.fras.msbm.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fras.msbm.R;

import java.util.List;

/**
 * Created by Antonio on 18/09/2016.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ImageEntity> mImageEntities;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public LinearLayout linearLayout;
        public TextView title1;
        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            linearLayout = (LinearLayout) view.findViewById(R.id.layout_top_bottom_1);
            title1  = (TextView) view.findViewById(R.id.list_item_title_1);
            progressBar =  (ProgressBar) view.findViewById(R.id.progress);
        }
    }

    public GalleryAdapter(Context context, List<ImageEntity> imageEntities){
        mContext = context;
        mImageEntities = imageEntities;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    public static class viewHolder{
//        public ImageView image1;
//        public TextView title1;
//        public ImageView image2;
//        public TextView title2;
//        public TextView folder;
//    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageEntity imageEntry = mImageEntities.get(position);
//        Log.e("Gallery Check", "Binded");
        Glide.with(mContext).load(imageEntry.getImageURL())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.thumbnail);
        holder.thumbnail.setTag(position);

        holder.title1.setText(imageEntry.getImageTitle());

        if(imageEntry.getIsFolder() == 0){
//            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_top_bottom_1);
            holder.linearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mImageEntities.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void update(List<ImageEntity> imageEntities){
        this.mImageEntities = imageEntities;
    }

}