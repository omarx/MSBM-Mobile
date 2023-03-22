package com.fras.msbm.models;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Shane on 8/10/2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class Lecture {
    @Getter(AccessLevel.NONE)
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @SerializedName("eventID")
    private String id;
    private String name;
    private String notes;
    private String lecturer;
    private String confirmation;
    @SerializedName("eventStart")
    private String startAt;
    @SerializedName("eventEnd")
    private String endAt;
    @SerializedName("eventDate")
    private String date;
    private String location;
    private String subtitle;
    private String dateString;
    private String request_type;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if ( ! (obj instanceof Lecture)) return false;

        Lecture lecture = (Lecture) obj;
        return this.name.equals(lecture.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
