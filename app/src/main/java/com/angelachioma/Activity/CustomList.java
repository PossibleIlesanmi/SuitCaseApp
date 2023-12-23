package com.angelachioma.Activity;

import java.util.ArrayList;
import java.util.List;

public class CustomList {
    private String listId;
    private String listName;
    private String userId;
    private List<ListItem> items;

    public CustomList() {
        // Default constructor required for Firebase
    }

    public CustomList(String listId, String listName, String userId) {
        this.listId = listId;
        this.listName = listName;
        this.userId = userId;
        this.items = new ArrayList<>(); // Initialize the list of items
    }

    // Getters and setters for the fields
    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ListItem> getItems() {
        return items;
    }

    public void setItems(List<ListItem> items) {
        this.items = items;
    }
}
