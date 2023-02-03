package com.cm_grocery.app.locationServices;

public class LocationModel {

    private Double latitude;
    private Double longitude;

    public LocationModel(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
