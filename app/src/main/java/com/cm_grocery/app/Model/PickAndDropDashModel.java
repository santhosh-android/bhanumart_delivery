package com.cm_grocery.app.Model;

public class PickAndDropDashModel {

    private String orderType;
    private String countString;

    public PickAndDropDashModel(String orderType, String countString) {
        this.orderType = orderType;
        this.countString = countString;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getCountString() {
        return countString;
    }
}
