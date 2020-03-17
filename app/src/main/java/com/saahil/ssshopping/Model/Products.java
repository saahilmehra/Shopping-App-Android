package com.saahil.ssshopping.Model;

public class Products {
    private String name, price, description, image, category, id, date, timme;
    public Products(){

    }

    public Products(String name, String price, String description, String image, String category, String id, String date, String timme) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.category = category;
        this.id = id;
        this.date = date;
        this.timme = timme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimme() {
        return timme;
    }

    public void setTimme(String timme) {
        this.timme = timme;
    }
}
