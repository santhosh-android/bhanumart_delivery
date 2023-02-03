package com.cm_grocery.app.Model;

public class OrderDetailsModelList {

    private String id;
    private String itemName;
    private String itemPrice;
    private String itemQty;
    private String subTotal;
    private String itemImg;
    //private VendorDetailsModel vendorDeailsModel;

    public OrderDetailsModelList(String id, String itemName, String itemPrice, String itemQty, String subTotal, String itemImg) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQty = itemQty;
        this.subTotal = subTotal;
        this.itemImg = itemImg;
        // this.vendorDeailsModel = vendorDeailsModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQty() {
        return itemQty;
    }

    public void setItemQty(String itemQty) {
        this.itemQty = itemQty;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    /*public VendorDetailsModel getVendorDeailsModel() {
        return vendorDeailsModel;
    }*/
}
