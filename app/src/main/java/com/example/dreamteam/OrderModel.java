package com.example.dreamteam;

import java.util.List;

public class OrderModel {

    private String id;
    private String userId;

    private String addressId;
    private List<ItemDetailModel> items;
    private List<DeliveryStage> deliveryStages;






    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ItemDetailModel> getItems() {
        return items;
    }

    public void setItems(List<ItemDetailModel> items) {
        this.items = items;
    }

    public List<DeliveryStage> getDeliveryStages() {
        return deliveryStages;
    }

    public void setDeliveryStages(List<DeliveryStage> deliveryStages) {
        this.deliveryStages = deliveryStages;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}

