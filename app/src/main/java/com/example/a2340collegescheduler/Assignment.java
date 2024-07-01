package com.example.a2340collegescheduler;


public class Assignment {

    public String title;
    public String dueDate;
    public String dueTime;
    public String amPm;
    public String associatedClass;

    public Assignment(String title, String dueDate, String dueTime, String amPm, String associatedClass) {
        this.title = title;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.amPm = amPm;
        this.associatedClass = associatedClass;
    }

    public String getTitle() {
        return title;
    }

    public String getAssociatedClass() {
        return associatedClass;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public String getAmPm() {
        return amPm;
    }


}
