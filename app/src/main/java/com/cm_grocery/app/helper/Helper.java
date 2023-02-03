package com.cm_grocery.app.helper;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;


public class Helper {

    /**
     * Using this function for checking the internet connectivity in the entire application.
     *
     * @param context : Context of calling fragment or activity
     * @return TRUE for active and FALSE for inactive
     */

    public static boolean internetActive(Context context) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        Network[] networks = cm.getAllNetworks();

        boolean hasInternet = false;
        if (networks.length > 0) {
            for (Network network : networks) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                if (nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                    hasInternet = true;
            }
        }
        return hasInternet;
    }

    //Two Location distance calculation way 2
    public static double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        int radius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)));
        double c = 2 * asin(sqrt(a));
        double valueResult = radius * c;
        double meter = (valueResult % 1000) * 1000;
        return round(meter);
    }

    /**
     * Hide the keyboard on calling of this function
     *
     * @param view    : Respective view
     * @param context : Respective context
     */
    public static void hideKeyboard(View view, Context context) {
        InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String[] getLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        } else
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    }

    public static boolean checkLocationPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }
}
