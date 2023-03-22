package com.fras.msbm.activities.articles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fras.msbm.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowArticleActivity extends AppCompatActivity {
    public static final String TAG = ShowArticleActivity.class.getName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.webview) WebView webView;

    private String title;
    private String url;
    private String publishDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);
        ButterKnife.bind(this);
        getIntentData();
        setupLayouts();
    }

    private void setupLayouts() {
        setupToolbar();
        setupWebView();
    }

    private void setupWebView() {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webView.getSettings();
        configureWebViewSettings(webSettings);
        webView.loadUrl(url);
    }

    @SuppressWarnings("ConstantConditions")
    private void configureWebViewSettings(WebSettings webSettings) {
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
        actionBar.setSubtitle(publishDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getIntentData() {
        final Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        publishDate = intent.getStringExtra("published_date");
    }
}
