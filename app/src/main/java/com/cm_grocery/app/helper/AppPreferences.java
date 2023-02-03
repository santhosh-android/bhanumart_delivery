package com.cm_grocery.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.cm_grocery.app.locationServices.LocationModel;
import com.google.gson.Gson;


public class AppPreferences {
    public void storeLatLong(LocationModel locationModel, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IConstants.Pref.shared_preference, Context.MODE_PRIVATE).edit();
        String latLong = new Gson().toJson(locationModel);
        editor.putString(IConstants.Pref.LOCATION, latLong);
        editor.apply();
    }

    public LocationModel getLatLong(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(IConstants.Pref.shared_preference, Context.MODE_PRIVATE);
        String latLan = preferences.getString(IConstants.Pref.LOCATION, "");
        return latLan.isEmpty() ? null : new Gson().fromJson(latLan, LocationModel.class);

    }








}
