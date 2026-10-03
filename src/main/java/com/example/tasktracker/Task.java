package com.example.tasktracker;

public class Task {
    private String status;
    private String description;

    public Task(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toFileLine() {
        return status + "|" + description;
    }
}
