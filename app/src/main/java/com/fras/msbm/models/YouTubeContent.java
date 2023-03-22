package com.fras.msbm.models;

/**
 * Created by Antonio on 10/10/2016.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Helper class for providing mock data to the app.
 * In a real world scenario you would either hard code the video ID's in the strings file or
 * retrieve them from a web service.
 */
public class YouTubeContent {

    /**
     * An array of YouTube videos
     */
    public static List<YouTubeVideo> ITEMS = new ArrayList<>();

    /**
     * A map of YouTube videos, by ID.
     */
    public static Map<String, YouTubeVideo> ITEM_MAP = new HashMap<>();

    static {
        addItem(new YouTubeVideo("y7x3RnDAJOc", "Pursue Your Passion in Business"));
        addItem(new YouTubeVideo("zh2SkEiSKng", "MSBM Post Graduate Registration Video"));
        addItem(new YouTubeVideo("NufrszUJJf4", "MSBM Undergraduate Registration Video"));
        addItem(new YouTubeVideo("0-H95tL-J7k", "Logistics Symposium 1"));
        addItem(new YouTubeVideo("opIkhCIBl88", "MSBM 2016 TV Commercial"));
        addItem(new YouTubeVideo("T6-XVnJYLok", "Orientation 2015"));
        addItem(new YouTubeVideo("TAe6ye9E-H0", "Dr Maurice McNaughton, Director of the Centre of Excellence describes what is OPENDATA"));
        addItem(new YouTubeVideo("JdXh9HMFVww", "Jackie Sharpe , MSBM alumnus and CEO of Scotia Group Jamaica"));
        addItem(new YouTubeVideo("n10VwcKg-hU", "Harry Smith Addressing Students at Mona School of Business and Management - Part 4"));
        addItem(new YouTubeVideo("ZlgL-lx_ujU", "Harry Smith Addressing Students at Mona School of Business and Management - Part 3"));
        addItem(new YouTubeVideo("a1hv8jqgX4I", "Harry Smith Addressing Students at Mona School of Business and Management - Part 2"));
        addItem(new YouTubeVideo("oyYUqzCI_pU", "Harry Smith Addressing Students at Mona School of Business and Management - Part 1"));
        addItem(new YouTubeVideo("bK84aGxqjrQ", "Alton Thewell, MSc Accounting, VP of Systems and Internal Controls, Island Entertainment Brands"));
        addItem(new YouTubeVideo("MRYMaXxxp9M", "Lana Forbes, EMBA, Director, Sales & Service, Scotia Jamaica Life Insurance Company Limited"));
        addItem(new YouTubeVideo("1EIa03fd9Jc", "Austin Briscoe, MBA, E-Gov Jamaica"));
        addItem(new YouTubeVideo("4MnHcunh5Ws", "Alumni Spotlight: Claudette Crooks, EMBA"));
        addItem(new YouTubeVideo("_IwKC3rXzAo", "Mr. Aubyn Hill Chief Executive Office of Corporate Strategies Ltd, Lecture on Change Management 2"));
        addItem(new YouTubeVideo("fLAhFLVK52o", "Mr. Aubyn Hill Chief Executive Office Of Corporate Strategies Ltd, Lecture On Change Management."));
        addItem(new YouTubeVideo("WytMGWcjGZ4", "The Mona School Of Business and Management Advantage"));


    }

    private static void addItem(final YouTubeVideo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A POJO representing a YouTube video
     */
    public static class YouTubeVideo {
        public String id;
        public String title;

        public YouTubeVideo(String id, String content) {
            this.id = id;
            this.title = content;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}