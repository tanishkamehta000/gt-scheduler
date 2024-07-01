package com.example.a2340collegescheduler;

import java.io.Serializable;

public class ClassDetails implements Serializable {
    private String courseName;
    private String time;
    private String instructor;
    private boolean[] days;

    public ClassDetails(String courseName, String time, String instructor, boolean[] days) {
        this.courseName = courseName;
        this.time = time;
        this.instructor = instructor;
        this.days = days;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }
}
