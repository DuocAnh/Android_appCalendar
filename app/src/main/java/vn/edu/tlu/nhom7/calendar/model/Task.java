package vn.edu.tlu.nhom7.calendar.model;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String taskName;
    private String taskDescription;
    private String date;
    private String startTime;
    private String endTime;
    private String alarmTime;
    private String color;
    private String location;
    private String idCurrentUser;

    public Task() {
    }

    public Task(int id, String taskName, String taskDescription, String date, String startTime, String endTime, String alarmTime, String color, String location, String idCurrentUser) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.alarmTime = alarmTime;
        this.color = color;
        this.location = location;
        this.idCurrentUser = idCurrentUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIdCurrentUser() {
        return idCurrentUser;
    }
    public void setIdCurrentUser(String idCurrentUser) {
        this.idCurrentUser = idCurrentUser;
    }
}