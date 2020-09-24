package com.richa.myspace.model;

public class Note {
    private String Title;
    private String DateTime;
    private String Content;

    public Note(String Title, String DateTime, String Content) {
        this.Title = Title;
        this.DateTime = DateTime;
        this.Content = Content;
    }

    public Note() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String DateTime) {
        this.DateTime = DateTime;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }
}
