package com.fras.msbm.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;

import com.fras.msbm.activities.general.BaseActivity;
import com.fras.msbm.activities.general.HomeActivity;
import com.fras.msbm.events.users.UserLoginEvent;
import com.fras.msbm.events.users.UserLogoutEvent;
import com.fras.msbm.fragments.ListArticlesFragment;
import com.fras.msbm.models.Article;
import com.fras.msbm.utils.AsyncResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Shane on 5/20/2016.
 */
public class SplashActivity extends BaseActivity implements AsyncResponse {
    public static final String TAG = SplashActivity.class.getSimpleName();
    public static final String namespace = null;
    public static final String NEWS_FEED = "http://www.mona.uwi.edu/msbm/news-feed";
    private final OkHttpClient client = new OkHttpClient();
    private List<Article> tempArticles = new ArrayList<>();
    private List<Article> articles = new ArrayList<>();
    SharedPreferences prefs = null;
//    ListArticlesFragment test = new ListArticlesFragment(ListArticlesFragment.NEWS_FEED);

//    Fragment fragment = ListArticlesFragment.newInstance(ListArticlesFragment.NEWS_FEED);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.fras.msbm", MODE_PRIVATE);

        Log.e(TAG, "Before Splash Run");
        articles = Article.listAll(Article.class);
        Log.e(TAG, "Stored Articles: " + articles.size());
        if(articles.isEmpty()) {
            Log.e(TAG, "No Articles Found");
            new LoadArticles().execute();
        }else{
            openActivity(HomeActivity.class);
            finish();
        }
//        openActivity(HomeActivity.class);

//        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onMainThreadEvent(UserLoginEvent event) {
        openActivity(HomeActivity.class);
//        finish();
    }

    @Subscribe
    public void onMainThreadEvent(UserLogoutEvent event) {
//        Log.e(TAG, "onMainThreadEvent UserLogoutEvent Triggered.");
//        openLoginActivity();
//        finish();
    }

    @Override
    public void processFinish(int newItem){
        Log.e(TAG, " Process Finished: "  + Integer.toString(newItem));

    }

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

        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {

            Log.e("Start  ","Start Background");

            try{
                Log.e(TAG, "Given Argument: =" + args[0]);
            }catch (Exception e){
                Log.e(TAG, e.getLocalizedMessage());
            }


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

            Log.e(TAG,"iN POST Exce");
            try{
                List<Article> savedArticles = Article.listAll(Article.class);
//                Log.e(TAG, "TITLE: " + savedArticles.get(0).getTitle());
                if(!savedArticles.isEmpty() && savedArticles.size() < tempArticles.size()){
                    Article.deleteAll(Article.class);
                    Log.e(TAG, "Articles Cleansed: ");
                }

                savedArticles = Article.listAll(Article.class);

                if(savedArticles.isEmpty()){
                    for(Article newArticle: tempArticles){
                        newArticle.save();
                        Log.e(TAG, "Article Saved: " + newArticle.getTitle());
                    }
                }
//                for(Article newArticle: tempArticles){
//                    newArticle.save();
//                    Log.e(TAG, "Article Saved: " + newArticle.getTitle());
//                }

            }catch(Exception e){
                Log.e(TAG, "Weird Error: " +  e.getLocalizedMessage());
            }
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            if (user == null ){
//                Log.e(TAG, "No User Found.");
//                openLoginActivity();
//            }else{
//                Log.e(TAG, "User Found.");
//                openActivity(HomeActivity.class);
//            }

            if (prefs.getBoolean("InitialStartUp", true)){
                // Do first run stuff here then set 'firstrun' as false
                Log.e(TAG,"First Start");
                openLoginActivity();
                Log.e(TAG,"Changing Preferences");
                prefs.edit().putBoolean("InitialStartUp", false).apply();
                //.commit();
            }else{
                Log.e(TAG,"Not First Start");
                openActivity(HomeActivity.class);
            }
//            openActivity(HomeActivity.class);
            finish();
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

//            if(mTask.isCancelled())
//                break;

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
//                            mTask.cancel(true);
                            Log.e(TAG,e.toString());
                        }
//                        articleAdapter.update(articles);
//                        articleAdapter.notifyDataSetChanged();
                        Log.i(TAG, "Article Item Added=" + article.toString());
                    }
                    break;
            }
            eventType = parser.next();

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
