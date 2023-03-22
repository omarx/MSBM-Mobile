package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fras.msbm.R;
import com.fras.msbm.activities.articles.ShowArticleActivity;
import com.fras.msbm.models.Article;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 * Created by Shane on 7/10/2016.
 */
public class ArticleAdapter extends BaseAdapter<Article, ArticleAdapter.ArticleViewHolder> {
    public static final String TAG = ArticleAdapter.class.getSimpleName();
    private List<Article> mArticles;

    private FirebaseAnalytics firebaseAnalytics;

    public ArticleAdapter(Context context, List<Article> entities) {
        super(context, entities);
        this.mArticles = entities;
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public ArticleAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_article, parent, false);
        return new ArticleViewHolder(rootView);
    }

    @Override
    public int getItemCount() {
        try{
            return mArticles.size();
        }catch(Exception e){
            return 0;
        }

    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
//        final Article article = getEntities().get(position);
        final Article article = mArticles.get(position);
        holder.textViewTitle.setText(article.getTitle());
        holder.textViewPreview.setText(article.getDescription());
        holder.textViewPublishDate.setText(article.getPublishedDate());
        Log.e(TAG + " TEST",article.toString());
        final String image = article.getImage();

        if (image != null && ! image.isEmpty()) {
            Glide.with(context)
                    .load(image)
//                    .override(5000, 1200000000)
//                    .fitCenter()
//                    .centerCrop()
                    .bitmapTransform(new CropTransformation(context, holder.imageViewHeadline.getWidth(), holder.imageViewHeadline.getHeight(), CropTransformation.CropType.TOP))
                    .crossFade()
                    .error(R.mipmap.ic_launcher)
//                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.imageViewHeadline);
        }

        holder.cardView.setOnClickListener(view -> {
            trackArticleAnalytics(article, FirebaseAnalytics.Event.SELECT_CONTENT);
            openArticle(article);
        });

        holder.imageViewShare.setOnClickListener(view -> {
            trackArticleAnalytics(article, FirebaseAnalytics.Event.SHARE);
            shareArticle(article);
        });
    }

    private void openArticle(Article article) {
        final Intent showArticleIntent = new Intent(context, ShowArticleActivity.class);
        showArticleIntent.putExtra("url", article.getLink());
        showArticleIntent.putExtra("title", article.getLink());
        showArticleIntent.putExtra("published_date", article.getPublishedDate());
        context.startActivity(showArticleIntent);
    }

    private void shareArticle(Article article) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_SUBJECT, article.getTitle())
                .putExtra(Intent.EXTRA_TEXT, article.getLink())
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share this article..."));
    }

    private void trackArticleAnalytics(Article article, String eventType) {
        final Bundle shareBundle = new Bundle();
        shareBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, article.getTitle());
        shareBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "article");
        shareBundle.putString("published_date", article.getPublishedDate());
        firebaseAnalytics.logEvent(eventType, shareBundle);
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view) CardView cardView;
        @BindView(R.id.linear_layout_summary) LinearLayout linearLayoutSummary;
        @BindView(R.id.text_title) TextView textViewTitle;
        @BindView(R.id.image_article) ImageView imageViewHeadline;
        @BindView(R.id.text_publish_date) TextView textViewPublishDate;
        @BindView(R.id.image_share) ImageView imageViewShare;
        @BindView(R.id.text_preview) TextView textViewPreview;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void update(List<Article> bookingEntries){
        this.mArticles = bookingEntries;
    }
}