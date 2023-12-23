package com.angelachioma.Activity;

public class ListItem {
    private String id;
    private String name;
    private String description;
    private String price;
    private String location;
    private boolean isPurchased;
    private String imageUrl;

    // Constructors
    public ListItem() {
    }

    public ListItem(String name, String description, String price, String location, boolean isPurchased, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.isPurchased = isPurchased;
        this.imageUrl = imageUrl;
    }

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", location='" + location + '\'' +
                ", isPurchased=" + isPurchased +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
