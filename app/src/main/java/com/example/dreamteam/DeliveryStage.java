package com.example.dreamteam;

import java.util.Date;

public class DeliveryStage {
    private String id;
    private Stage stage;
    private String description;
    private Date date;

    public DeliveryStage(String id, Stage stage, String description, Date date) {
        this.id = id;
        this.stage = stage;
        this.description = description;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public enum Stage {
        Ordered, Packed, Shipping, Delivered
    }
}
