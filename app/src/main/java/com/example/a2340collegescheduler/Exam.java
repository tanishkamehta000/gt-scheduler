package com.example.a2340collegescheduler;

public class Exam {

    private String name;
    private String location;
    private String date;
    private String time;
    private String amPm;

    public Exam(String name, String location, String date, String time, String amPm) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.amPm = amPm;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAmPm() {
        return amPm;
    }
}
