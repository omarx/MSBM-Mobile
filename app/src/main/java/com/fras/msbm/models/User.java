package com.fras.msbm.models;

import com.fras.msbm.models.courses.Course;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Shane on 6/18/2016.
 */
@Getter @Setter
@ToString @Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class User {
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("firstname")
    private String firstName;
    @SerializedName("lastname")
    private String lastName;
    @SerializedName("token")
    private String token;
    @SerializedName("id")
    private int userId;
    @SerializedName("profileimageurl")
    private String profileUrl;
    @SerializedName("department")
    private String department;
    @SerializedName("moodleLocation")
    private String moodleLocation;

    private String moodleInstance;

    @Exclude
    private List<Course> courses;

    public String getUserToken(){
        return this.token;
    }

    public String getFullName() {
        return firstName + lastName;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("userId", userId);
        map.put("department", department);
        map.put("token", token);
        map.put("profileUrl", profileUrl);
        return map;
    }
}
