package com.cm_grocery.app.Model;

public class HomeCategoryMoadel {

    private String tittleName;
    private String count;

    public HomeCategoryMoadel(String tittleName, String count) {
        this.tittleName = tittleName;
        this.count = count;
    }

    public String getTittleName() {
        return tittleName;
    }

    public void setTittleName(String tittleName) {
        this.tittleName = tittleName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
