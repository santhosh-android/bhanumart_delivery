package com.cm_grocery.app.Model;

public class NewOrdersRecyclerModel {
    private String orderId;
    private String customerName;
    private String customerMobile;
    private String deliveryAddress;
    private String dateTime;
    private String payMode;
    private String paymentstatus;

    public NewOrdersRecyclerModel(String orderId, String customerName, String customerMobile, String deliveryAddress, String dateTime, String payMode, String paymentstatus) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerMobile = customerMobile;
        this.deliveryAddress = deliveryAddress;
        this.dateTime = dateTime;
        this.payMode = payMode;
        this.paymentstatus = paymentstatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }
}
