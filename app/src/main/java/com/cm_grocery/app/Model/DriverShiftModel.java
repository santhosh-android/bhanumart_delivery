package com.cm_grocery.app.Model;

public class DriverShiftModel {
    private String status;
    private String shift_id;
    private String shift;

    public DriverShiftModel(String status, String shift_id, String shift) {
        this.status = status;
        this.shift_id = shift_id;
        this.shift = shift;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShift_id() {
        return shift_id;
    }

    public void setShift_id(String shift_id) {
        this.shift_id = shift_id;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
