package com.fras.msbm.events;

import com.fras.msbm.models.courses.Course;

/**
 * Created by Shane on 6/18/2016.
 */
public class CourseClickEvent {
    private Course course;

    public CourseClickEvent(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }
}
