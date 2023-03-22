package com.fras.msbm.gallery;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fras.msbm.R;

import java.util.List;

/**
 * Created by Shane on 8/25/2016.
 */
public class ImageGalleryPhotoAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ImageEntity> mImageEntities;

    private static final int TYPE_ONE_COLUMN = 0;
    private static final int TYPE_TWO_COLUMNS = 1;

    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_FOLDER = 1;

    public ImageGalleryPhotoAdapter(Context context, List<ImageEntity> imageEntities){
        mContext = context;
        mImageEntities = imageEntities;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class viewHolder{
        public ImageView image1;
        public TextView title1;
        public ImageView image2;
        public TextView title2;
        public TextView folder;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == mImageEntities.size() / 2)
                && (mImageEntities.size() % 2 == 1)) {
            return TYPE_ONE_COLUMN;
        } else {
            return TYPE_TWO_COLUMNS;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return mImageEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final viewHolder mHolder;
        Log.e("List Index:= ", Integer.toString(position));
        int type = getItemViewType(position);
        if (type == TYPE_ONE_COLUMN) {
            Log.e("TYPE", "One Column");
            if(convertView == null){
                convertView = mInflater.from(mContext).inflate(R.layout.list_item_image_gallery_one_column, parent, false);
                mHolder = new viewHolder();

                convertView.setTag(mHolder);
            }else{
                mHolder = (viewHolder) convertView.getTag();
            }

            mHolder.image1 = (ImageView) convertView.findViewById(R.id.list_item_image_1);
            mHolder.title1  = (TextView) convertView.findViewById(R.id.list_item_title_1);

            try{
                ImageEntity imageEntry1 = mImageEntities.get(position * 2);
                //                mHolder.title1.setText(imageEntry1.getImageTitle());
                //            mHolder.image1.setTag(position * 2);
                Glide.with(mContext)
                        .load(imageEntry1.getImageURL())
                        .into(mHolder.image1);
                mHolder.image1.setTag(R.id.list_item_image_1,imageEntry1.getImageURL());
                mHolder.image1.setOnClickListener(this);
                //                if(imageEntry1.getIsFolder() == TYPE_PHOTO){
                //                    mHolder.title1.setVisibility(View.GONE);
                //                }
                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_top_bottom_1);
                linearLayout.setVisibility(View.GONE);
            }catch(Exception e){
                Log.e("Exception:=", e.toString());
                RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.gallery_relative_layout);
                try{
                    relativeLayout.setVisibility(View.GONE);
                }catch(Exception ex){
                    Log.e("Exception:=",  ex.toString());
                }
            }


        }else if (type == TYPE_TWO_COLUMNS){
            Log.e("TYPE", "Two Columns");
            if(convertView == null){
                convertView = mInflater.from(mContext).inflate(R.layout.list_item_image_gallery_two_columns, parent, false);
                mHolder = new viewHolder();

                convertView.setTag(mHolder);
            }else{
                mHolder = (viewHolder) convertView.getTag();
            }

            mHolder.image1 = (ImageView) convertView.findViewById(R.id.list_item_image_1);
            mHolder.title1  = (TextView) convertView.findViewById(R.id.list_item_title_1);
            mHolder.image2 = (ImageView) convertView.findViewById(R.id.list_item_image_2);
            mHolder.title2  = (TextView) convertView.findViewById(R.id.list_item_title_2);

            try{
                ImageEntity imageEntry1 = mImageEntities.get(position * 2);
                //                mHolder.title1.setText(imageEntry1.getImageTitle());
                //            mHolder.image1.setTag(position * 2);
                Glide.with(mContext)
                        .load(imageEntry1.getImageURL())
                        .into(mHolder.image1);
                mHolder.image1.setTag(R.id.list_item_image_1,imageEntry1.getImageURL());
                mHolder.image1.setOnClickListener(this);
                //                if(imageEntry1.getIsFolder() == TYPE_PHOTO){
                //                    mHolder.title1.setVisibility(View.GONE);
                //                }
                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_top_bottom_1);
                linearLayout.setVisibility(View.GONE);
            }catch(Exception e){
                Log.e("Exception:=", e.toString());
                RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.gallery_relative_layout1);
                try{
                    relativeLayout.setVisibility(View.GONE);
                }catch(Exception ex){
                    Log.e("Exception:=",  ex.toString());
                }
            }

            try{
                ImageEntity imageEntry2 = mImageEntities.get(position * 2 + 1);
                //                mHolder.title2.setText(imageEntry2.getImageTitle());
                Glide.with(mContext)
                        .load(imageEntry2.getImageURL())
                        .into(mHolder.image2);
                mHolder.image2.setTag(R.id.list_item_image_2,imageEntry2.getImageURL());
                mHolder.image2.setOnClickListener(this);
                //                if(imageEntry2.getIsFolder() == TYPE_PHOTO){
                //                    mHolder.title1.setVisibility(View.GONE);
                //                }
                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_top_bottom_2);
                linearLayout.setVisibility(View.GONE);
            }catch (Exception e){
                Log.e("Exception:=", e.toString());
                RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.gallery_relative_layout2);
                try{
                    relativeLayout.setVisibility(View.GONE);
                }catch(Exception ex){
                    Log.e("Exception:=",  ex.toString());
                }
            }
        }

        return convertView;
    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        String imageURL = "";

        imageURL = (String) view.getTag(R.id.list_item_image_1);
        if (imageURL == null){
            imageURL = (String) view.getTag(R.id.list_item_image_2);
        }

//        String imageURL = (String) view.getTag();
        //        Toast.makeText(view.getContext(), "Folder id: " + imageURL, Toast.LENGTH_SHORT)
        //                .show();
        //        view.getContext
        //        Log.e("Starting:=","Photos");
        Intent intent = new Intent(mContext, ImageDialog.class);
        intent.putExtra("IMAGE_URL", imageURL);
        mContext.startActivity(intent);
        Log.e("Image Should be up --"," Adapter OnClick");
        Log.e("Adapter URL --",imageURL);
    }

    public void update(List<ImageEntity> imageEntities){
        this.mImageEntities = imageEntities;
    }}
