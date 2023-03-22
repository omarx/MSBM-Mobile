package com.fras.msbm.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fras.msbm.R;
import com.fras.msbm.adapter.recycler.ArticleAdapter;
import com.fras.msbm.events.users.UserLoginEvent;
import com.fras.msbm.models.Article;
import com.fras.msbm.models.directory.Contact;
import com.fras.msbm.models.directory.DirectoryEntry;
import com.fras.msbm.network.parsers.NewsFeedParser;
import com.fras.msbm.utils.AsyncResponse;
import com.fras.msbm.views.recyclers.BottomOffsetDecoration;
import com.fras.msbm.views.recyclers.SimpleItemDivider;
import com.fras.msbm.views.recyclers.TopOffsetDecoration;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.entries;

public class ListArticlesFragment extends BaseFragment {
    public static final String TAG = ListArticlesFragment.class.getSimpleName();
    public static final String NEWS_FEED = "http://www.mona.uwi.edu/msbm/news-feed";
    public static final String RESEARCH_FEED = "http://www.mona.uwi.edu/msbm/research-feed2";

    public static final String namespace = null;
    private ProgressDialog pDialog;
    private List<Article> articles = new ArrayList<>();
    private List<Article> tempArticles = new ArrayList<>();
    private AsyncTask mTask;
    public AsyncTask exTask = new LoadArticles();
    private static final String ARG_URL = "url";

    @BindView(R.id.circular_progress_bar) CircularProgressBar progressBar;
    @BindView(R.id.recycler_articles) RecyclerView articlesRecyclerView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;

    private final OkHttpClient client = new OkHttpClient();
    private ArticleAdapter articleAdapter;
    private String url;

    public ListArticlesFragment() {}

    public static ListArticlesFragment newInstance(String url) {
        ListArticlesFragment fragment = new ListArticlesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle(getString(R.string.title_news));
        setupViews();
        articles = Article.listAll(Article.class);
        Log.e(TAG, "Article Length: " + articles.size());

        if(articles.isEmpty())
            showLoadingStarted();

        articleAdapter.update(articles);
        articleAdapter.notifyDataSetChanged();
//        loadArticles(url);
//        showLoadingCompleted();
        mTask = new LoadArticles().execute();
        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mTask.cancel(true);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mTask.cancel(true);
    }


    private void loadArticles(@NonNull String url) {
        showLoadingStarted();

        generateResponseObservableFromUrl(url)
                .flatMap(this::mapResponseToArticleList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleArticlesLoadSubscription());
    }

    private void showLoadingStarted() {
        progressBar.setVisibility(View.VISIBLE);
        articlesRecyclerView.setVisibility(View.GONE);
    }

    private void showLoadingCompleted() {
        progressBar.setVisibility(View.GONE);
        articlesRecyclerView.setVisibility(View.VISIBLE);
    }

    private Subscriber<List<Article>> handleArticlesLoadSubscription() {
        return new Subscriber<List<Article>>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "articles:completed");
                showLoadingCompleted();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "articles:error", e);
                // change this to show loading error eventually
                showLoadingCompleted();
                FirebaseCrash.report(e);
            }

            @Override
            public void onNext(List<Article> articles) {
                Log.i(TAG, "articles:next");
                showLoadingCompleted();
                articleAdapter.setEntities(articles);
            }
        };
    }

    private Observable<List<Article>> mapResponseToArticleList(Response response) {
        return Observable.create(subscriber -> {
            try {
                if (! response.isSuccessful()) throw new IOException();
                final List<Article> articles = extractArticlesFromResponse(response);
                for (Article article : articles)
                    Log.i(TAG, "article -> " + article.toString());
                Log.i(TAG, "here in articles");
                subscriber.onNext(articles);
                subscriber.onCompleted();
            } catch (IOException | XmlPullParserException e) {
                subscriber.onError(e);
            }
        });
    }

    private List<Article> extractArticlesFromResponse(Response response) throws IOException, XmlPullParserException {
        final InputStream inputStream = response.body().byteStream();
        return new NewsFeedParser().parse(inputStream);
    }

    private Observable<Response> generateResponseObservableFromUrl(@NonNull String url) {
        return Observable.create(subscriber -> {
            try {
                final Request request = new Request.Builder().url(url).build();
                final Response response = client.newCall(request).execute();
                subscriber.onNext(response);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private void setupViews() {
        articleAdapter = new ArticleAdapter(getContext());
        articlesRecyclerView.setHasFixedSize(true);
        articlesRecyclerView.setAdapter(articleAdapter);
        articlesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        articlesRecyclerView.addItemDecoration(new SimpleItemDivider(getActivity().getResources().getDrawable(R.drawable.divider_space)));
        articlesRecyclerView.addItemDecoration(new BottomOffsetDecoration(16));
        articlesRecyclerView.addItemDecoration(new TopOffsetDecoration(16));
    }

    @Subscribe
    public void onEvent(UserLoginEvent event) {}


    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    public class LoadArticles extends AsyncTask<String, String, String> {
        private AsyncResponse response;
        private int newItems = 0;

        LoadArticles(){
        }

        LoadArticles(AsyncResponse newResponse){
            response = newResponse;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showLoadingStarted();
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Loading contacts. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
//            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
            Log.e("Start  ","Start Background");

//            try{
//                Log.e(TAG, "Given Argument: =" + args[0]);
//            }catch (Exception e){
//                Log.e(TAG, e.getLocalizedMessage());
//            }


            try {
                final Request request = new Request.Builder().url(NEWS_FEED).build();
                final Response response = client.newCall(request).execute();
                final InputStream inputStream = response.body().byteStream();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inputStream, null);
                parser.nextTag();
                readFeed(parser);

                response.body().close();
                Log.e(TAG,response.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {

            Log.e("XYXYX","iN POST Exce");
            try{

                List<Article> savedArticles = Article.listAll(Article.class);
                Log.e(TAG, "TITLE Saved: " + savedArticles.get(0).getTitle() + " NEW: " + tempArticles.get(0).getTitle());
                Log.e(TAG, " BOOLEAN " + Boolean.toString(!tempArticles.get(0).getTitle().equals(savedArticles.get(0).getTitle())));
                if(!savedArticles.isEmpty() && !tempArticles.get(0).getTitle().equals(savedArticles.get(0).getTitle()) ||
                        !savedArticles.isEmpty() && savedArticles.size() < tempArticles.size()){
                    Article.deleteAll(Article.class);
                    Log.e(TAG, "Articles Cleansed: ");
                }

                savedArticles = Article.listAll(Article.class);
                if(savedArticles.isEmpty()){
                    for(Article newArticle: tempArticles){
                        newArticle.save();
                        Log.e(TAG, "Article Saved: " + newArticle.getTitle());
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            articleAdapter.update(tempArticles);
                            articleAdapter.notifyDataSetChanged();

                            showLoadingCompleted();
                            Log.e("Excecute", "After Article Run");
                        }
                    });
                }

                showLoadingCompleted();
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//                        articleAdapter.update(tempArticles);
//                        articleAdapter.notifyDataSetChanged();
//
//                        showLoadingCompleted();
//                        Log.e("Excecute", "After Article Run");
//                    }
//                });
//                response.processFinish(newItems);
            }catch(Exception e){
                Log.e(TAG, e.toString());
            }

        }

    }

    private List<Article> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        tempArticles.clear();

        parser.require(XmlPullParser.START_TAG, namespace, "rss");
//        int count = 0;
        int eventType = parser.getEventType();

        Article article = null;
//        articles = new ArrayList<>();

        while (eventType != XmlPullParser.END_DOCUMENT) {
//            Log.e(TAG," Check: " + Integer.toString(checkz));
            String tagName;

            if(mTask.isCancelled())
                break;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
//                    articles = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName().toLowerCase();

                    Log.i(TAG, "START_TAG:tagName=" + tagName);

                    if (tagName.equals("item"))
                        article = new Article();
                    else if (article != null) {
                        parser.next();
                        final String data = parser.getText();

                        Log.i(TAG, "data = " + data);

                        switch (tagName) {
                            case "title":
                                Log.e(TAG,"Title Here " + data);
                                article.setTitle(data);
                                break;
                            case "link":
                                article.setLink(data);
                                Document htmlDocument = Jsoup.connect(data).get();
                                String imageOfArticle = parseHeaderImageFromHtml(htmlDocument);
                                article.setImage(imageOfArticle);
                                break;
                            case "description":
                                String desc;
//                                Log.e(TAG, "Description" + data + "This is the End tho");
                                Document htmlDescription = Jsoup.parse(data);
//                                Log.e(TAG,htmlDescription.body().select(".field-item").toString() );
                                desc = htmlDescription.body().select(".field-item").get(0).text();
                                if(desc != null && !desc.isEmpty()){
//                                    Log.e(TAG, "ONE --" + htmlDescription.body().select(".field-item").get(0).text());
                                    article.setDescription(desc);
                                }else{
                                    desc = htmlDescription.body().select(".field-item").get(1).text();
//                                    Log.e(TAG,"TWO --" + htmlDescription.body().select(".field-item").get(1).text());
                                    article.setDescription(desc);
                                }
//                                article.setDescription(data);
                                break;
                            case "category":
                                article.setCategory(data);
                                break;
                            case "pubdate":
                                article.setPublishedDate(data);
                                break;
                            case "guid": break; // skipping this for now
                            case "dc:creator": break; // skipping this as well
                            default:
                                Log.i(TAG, "Missing Tag=" + tagName);
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();

                    if (tagName.equalsIgnoreCase("item") && article != null && article.getTitle() != null) {
//                        article.save();
                        tempArticles.add(article);
                        try{
//                            article.save();
//                            Log.e(TAG, "Article Saved: " + article.toString());

//                            getActivity().runOnUiThread(new Runnable() {
//                                public void run() {
//                                    showLoadingCompleted();
//                                    articleAdapter.update(articles);
//                                    articleAdapter.notifyDataSetChanged();
//                                }
//                            });
                        }catch(Exception e){
                            mTask.cancel(true);
                            Log.e(TAG,e.toString());
                        }
//                        articleAdapter.update(articles);
//                        articleAdapter.notifyDataSetChanged();
                        Log.i(TAG, "Article Item Added=" + article.toString());
                    }
                    break;
            }
            eventType = parser.next();
            if(mTask.isCancelled())
                break;
        }
//        try{
//
//            if(!Article.listAll(Article.class).isEmpty()){
//                Article.deleteAll(Article.class);
//                Log.e(TAG, "Articles Cleansed: ");
//            }
//
//            for(Article newArticle: tempArticles){
//                newArticle.save();
//                Log.e(TAG, "Article Saved: " + newArticle.getTitle());
//            }
//
//        }catch(Exception e){
//            Log.e(TAG, "Weird Error: " +  e.getLocalizedMessage());
//        }

        return tempArticles;
    }

    private String parseHeaderImageFromHtml(@NonNull Document document) {
        Element contentElement = document.select("div#content").first();
//        Element imageElement = contentElement.select("img[typeof=foaf:Image]").first();
        Element imageElement = contentElement.select("img").first();
        return imageElement.absUrl("src");
    }

}
