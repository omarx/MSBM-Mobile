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
@Builder @Getter
@Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    protected String name;
    protected String email;
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

    public Booking(String name, String cohort, String time, String duration, String location) {
        this.name = name;
        this.cohort = cohort;
        this.time = time;
        this.duration = duration;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
