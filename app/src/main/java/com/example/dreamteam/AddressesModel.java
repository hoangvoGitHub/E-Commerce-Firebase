package com.example.dreamteam;

import java.io.Serializable;

// TODO: 3/5/2024 Create firebase model
public class AddressesModel implements Serializable {

    private String userId;

    private String addressId;

    private String name;

    private String phoneNumber;

    private String line1;

    private String line2;

    private String line3;

    private String line4;
    private String pincode;
    private Boolean selected;

    public AddressesModel(String userId, String addressId, String name, String phoneNumber, String line1, String line2, String line3, String line4, String pincode, Boolean selected) {
        this.userId = userId;
        this.addressId = addressId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.line4 = line4;
        this.pincode = pincode;
        this.selected = selected;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    /**
     * This line is typically used for the street address, including the house number and street name.
     */
    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    /**
     * This line is typically used for the apartment number, suite number, or building name.
     */
    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    /**
     * This line is typically used for the city, state.
     */
    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    /**
     * This line is typically used for additional information, such as the country or province.
     */
    public String getLine4() {
        return line4;
    }

    public void setLine4(String line4) {
        this.line4 = line4;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.getLine1() + ", " +
                this.getLine2() + ", " +
                this.getLine3() + ", " +
                this.getLine4();
    }
}
