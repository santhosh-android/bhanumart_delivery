package com.cm_grocery.app.Model;

public class NewPickAndDropModel {

    private String orderPickId;
    private String customerPickName;
    private String customerPickMobile;
    private String pickAddress;
    private String deliveryAddress;

    public NewPickAndDropModel(String orderPickId, String customerPickName, String customerPickMobile, String pickAddress, String deliveryAddress) {
        this.orderPickId = orderPickId;
        this.customerPickName = customerPickName;
        this.customerPickMobile = customerPickMobile;
        this.pickAddress = pickAddress;
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderPickId() {
        return orderPickId;
    }

    public String getCustomerPickName() {
        return customerPickName;
    }

    public String getCustomerPickMobile() {
        return customerPickMobile;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

}
