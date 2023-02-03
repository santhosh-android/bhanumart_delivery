package com.cm_grocery.app.Model;

public class PickDropPickUpListModel {
    private String oreder_pd;
    private String customer_pd;
    private String mobile_pd;
    private String pickUp_address_pd;
    private String delivery_pd;

    public PickDropPickUpListModel(String oreder_pd, String customer_pd, String mobile_pd, String pickUp_address_pd, String delivery_pd) {
        this.oreder_pd = oreder_pd;
        this.customer_pd = customer_pd;
        this.mobile_pd = mobile_pd;
        this.pickUp_address_pd = pickUp_address_pd;
        this.delivery_pd = delivery_pd;
    }

    public String getOreder_pd() {
        return oreder_pd;
    }

    public String getCustomer_pd() {
        return customer_pd;
    }

    public String getMobile_pd() {
        return mobile_pd;
    }

    public String getPickUp_address_pd() {
        return pickUp_address_pd;
    }

    public String getDelivery_pd() {
        return delivery_pd;
    }
}
