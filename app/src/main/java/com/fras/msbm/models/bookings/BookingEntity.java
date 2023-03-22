package com.fras.msbm.models.bookings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Antonio on 05/07/2016.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    protected String name;
    protected String cohort;
    protected String time;
    protected String duration;
    protected String location;
    protected String eventID;
    protected String eventStart;
    protected String eventEnd;
    protected String success;
    protected String request_type;
    protected String confirmation;
    protected Integer confirmValue;
    protected String moodleID;
    protected String eventDate;


    protected String email;

    public BookingEntity(String name, String cohort, String time, String duration, String location) {
        this.name = name;
        this.cohort = cohort;
        this.time = time;
        this.duration = duration;
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCohort(String cohort) {
        this.cohort = cohort;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setEventID(String eventID) { this.eventID = eventID; }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRequest_type(String request_type) { this.request_type = request_type; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getName() {
        return name;
    }

    public String getCohort() {
        return cohort;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public String getLocation() {
        return location;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventStart() {
        return eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public String getSuccess() {
        return success;
    }

    public String getRequest_type() {
        return request_type;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public Integer getConfirmValue() {
        return confirmValue;
    }
}
