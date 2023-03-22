package com.fras.msbm.models;

import com.google.firebase.database.Exclude;
import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Shane on 5/28/2016.
 */
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Article extends SugarRecord {
    @Getter(AccessLevel.NONE)
    public static final SimpleDateFormat publishDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    @Exclude
    private String uid;
    private String title;
    private String link;
    private String description;
    private String image;
    private String publishedDate;
    private String category;




}
