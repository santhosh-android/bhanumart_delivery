package com.cm_grocery.app.locationServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocationLiveData {

    private final FusedLocationProviderClient fusedLocationProviderClient;
    public static LocationRequest locationRequest;
    private final ILocationCallback localLocationCallback;

    public LocationLiveData(Context mContext, ILocationCallback callback) {
        this.localLocationCallback = callback;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d("LiveLocation", "LocationLiveData: " + "LiveLocation Update");

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        setLocationData(location);
                    }
                });

        startLocationUpdates();

    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                setLocationData(location);
            }
        }
    };


    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void setLocationData(Location location) {
        if (localLocationCallback != null) {
            localLocationCallback.locationObserver(new LocationModel(location.getLatitude(), location.getLongitude()));

        }
    }
}
