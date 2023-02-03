package com.cm_grocery.app.Model;

public class VendorDetailsModel {

    private String shopName;
    private String contactName;
    private String mobile;
    private String alternateMobile;
    private String address;

    public VendorDetailsModel(String shopName, String contactName, String mobile, String alternateMobile, String address) {
        this.shopName = shopName;
        this.contactName = contactName;
        this.mobile = mobile;
        this.alternateMobile = alternateMobile;
        this.address = address;
    }

    public String getShopName() {
        return shopName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAlternateMobile() {
        return alternateMobile;
    }

    public String getAddress() {
        return address;
    }
}
