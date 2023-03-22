package com.fras.msbm.network.parsers;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;

import com.fras.msbm.models.Article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shane on 7/12/2016.
 */

public class NewsFeedParser {
    public static final String TAG = NewsFeedParser.class.getSimpleName();

    public static final String namespace = null;

    public List<Article> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Article> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "rss");
//        int count = 0;
        int eventType = parser.getEventType();

        Article article = null;
        List<Article> articles = new ArrayList<>();

        for(int checkz=0; checkz < 140; checkz++){ //while (eventType != XmlPullParser.END_DOCUMENT) {
            Log.e(TAG," Check: " + Integer.toString(checkz));
            String tagName;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    articles = new ArrayList<>();
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
                                Log.e(TAG,"Title Here");
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
                                Log.e(TAG,htmlDescription.body().select(".field-item").toString() );
                                desc = htmlDescription.body().select(".field-item").get(0).text();
                                if(desc != null && !desc.isEmpty()){
                                    Log.e(TAG, "ONE --" + htmlDescription.body().select(".field-item").get(0).text());
                                    article.setDescription(desc);
                                }else{
                                    desc = htmlDescription.body().select(".field-item").get(1).text();
                                    Log.e(TAG,"TWO --" + htmlDescription.body().select(".field-item").get(1).text());
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
                        articles.add(article);
//                        Log.i(TAG, "item=" + article.toString());
                    }
                    break;
            }
            eventType = parser.next();
        }
        return articles;
    }

    private String parseHeaderImageFromHtml(@NonNull Document document) {
        Element contentElement = document.select("div#content").first();
//        Element imageElement = contentElement.select("img[typeof=foaf:Image]").first();
        Element imageElement = contentElement.select("img").first();
        return imageElement.absUrl("src");
    }
}
