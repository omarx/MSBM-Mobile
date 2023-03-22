package com.fras.msbm.models.courses;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Shane on 7/3/2016.
 */
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private String type;
    private String filename;
    private String filepath;
    @SerializedName("filesize")
    private int fileSize;
    @SerializedName("fileurl")
    private String fileUrl;
    @SerializedName("timecreated")
    private int timeCreated;
    @SerializedName("timemodified")
    private int timeModified;
    @SerializedName("sortorder")
    private int sortOrder;
    @SerializedName("userid")
    private int userId;
    private String author;
    private String license;
}
