package com.uasmobile.todolist_app.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Todo extends RealmObject {

    @Required
    private String title;
    private String description;
    private String date;

    private String time;

    private String totalDate;
    private int status;

    private String prio;

    @PrimaryKey
    private String id;

    public Todo() {
    }

    public Todo(String title, String description, String date, String time, String totalDate, int status, String id, String prio) {
        this.title= title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.totalDate = totalDate;
        this.status = status;
        this.id = id;
        this.prio = prio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrio() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio = prio;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalDate() {
        return totalDate;
    }

    public void setTotalDate(String totalDate) {
        this.totalDate = totalDate;
    }
}
