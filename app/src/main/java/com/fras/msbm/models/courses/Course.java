package com.fras.msbm.models.courses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Shane on 6/1/2016.
 */
@Setter @Getter
@ToString @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("shortname")
    private String shortName;
    @SerializedName("fullname")
    private String fullName;
    @SerializedName("enrolledusercount")
    private int enrolledUserCount;
    @SerializedName("visible")
    private int visible;
}
