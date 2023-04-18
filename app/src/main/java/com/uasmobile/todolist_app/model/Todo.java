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
    private int status;
    @PrimaryKey
    private String id;

    public Todo() {
    }

    public Todo(String title, String description, String date, int status, String id) {
        this.title= title;
        this.description = description;
        this.date = date;
        this.status = status;
        this.id = id;
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
}
