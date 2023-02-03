package com.cm_grocery.app.Model;

public class PickUpCompletedModel {
    private String orderId;
    private String cusName;
    private String mobileCus;
    private String pickAddress;
    private String DelAddress;

    public PickUpCompletedModel(String orderId, String cusName, String mobileCus, String pickAddress, String delAddress) {
        this.orderId = orderId;
        this.cusName = cusName;
        this.mobileCus = mobileCus;
        this.pickAddress = pickAddress;
        DelAddress = delAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCusName() {
        return cusName;
    }

    public String getMobileCus() {
        return mobileCus;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public String getDelAddress() {
        return DelAddress;
    }
}
