package com.fras.msbm.models.courses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Shane on 7/2/2016.
 */
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    private int id;
    private String name;
    private String description;
    private int visible;
    @SerializedName("modicon")
    private String icon;
    @SerializedName("modname")
    private String type;
    private int indent;
    public List<Content> contents;
}
