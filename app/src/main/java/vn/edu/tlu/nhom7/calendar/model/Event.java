package vn.edu.tlu.nhom7.calendar.model;

public class Event {
    private String date;
    private String description;

    public Event(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
