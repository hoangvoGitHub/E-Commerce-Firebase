package com.example.dreamteam;

import java.io.Serializable;

// TODO: 3/5/2024 Create firebase model
public class CategoryModel  implements Serializable {

    private String categoryIconLink, categoryName, id;

    public CategoryModel(String id, String categoryIconink, String categoryName) {
        this.categoryIconLink = categoryIconink;
        this.categoryName = categoryName;
        this.id = id;
    }

    public CategoryModel(String categoryIconink, String categoryName) {
        this.categoryIconLink = categoryIconink;
        this.categoryName = categoryName;
        this.id = categoryName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryIconLink() {
        return categoryIconLink;
    }

    public void setCategoryIconLink(String categoryIconink) {
        categoryIconLink = categoryIconink;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
