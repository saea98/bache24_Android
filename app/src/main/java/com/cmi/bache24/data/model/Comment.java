package com.cmi.bache24.data.model;

/**
 * Created by omar on 1/31/16.
 */
public class Comment {

    private Report report;
    private String message;
    private User user;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
