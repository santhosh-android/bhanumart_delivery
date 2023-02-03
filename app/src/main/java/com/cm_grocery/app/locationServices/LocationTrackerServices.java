package com.cm_grocery.app.locationServices;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cm_grocery.app.Activity.SplashActivity;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.cm_grocery.app.helper.AppPreferences;
import com.cm_grocery.app.helper.Helper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationTrackerServices extends Service {
    private static final String DRIVER_TRACK_CH01 = "DRIVER_TRACK_CH01";
    private static final String DRIVER_TRACK_CHANNEL = "DRIVER_TRACK_CHANNEL";
    private static final int TRACK_NOTIFICATION_ID = 123456;
    private String LOG_TAG = null;
    private String db_id = "";
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        Log.i(LOG_TAG, "service created");
        sharedPreferences = this.getSharedPreferences("fashionMaa", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "onStartCommand: ");
        observerLocationChange();
        return START_STICKY;
    }

    private void observerLocationChange() {
        new LocationLiveData(this, locationCallback);
        Log.d("observerLocation", "observerLocationChange: " + "StartLocation Update");
    }

    private final ILocationCallback locationCallback = locationModel -> {
        LocationModel model = new AppPreferences().getLatLong(getApplicationContext());
        if (model == null) {
            new AppPreferences().storeLatLong(locationModel, getApplicationContext());
        } else {
            LatLng currentLatLan = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
            LatLng lastLng = new LatLng(model.getLatitude(), model.getLongitude());
            double distance = Helper.calculateDistance(currentLatLan.latitude, currentLatLan.longitude, lastLng.latitude, lastLng.longitude);

            if (distance > 100) {
                Log.d("location update", "LocationChange" + locationModel.getLatitude());
                updateDriverLocation(locationModel);
            }
            Log.d("location update", "observerLocationChange: " + distance);
        }
    };

    private void updateDriverLocation(LocationModel locationModel) {
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", db_id);
        map.put("latitude", locationModel.getLatitude().toString());
        map.put("longitude", locationModel.getLongitude().toString());
        CM_GroceryDeliveryInterface velInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = velInterface.updateLocation(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        if (responseObj.getString("status").equalsIgnoreCase("true")) {

                        } else {
                            Toast.makeText(LocationTrackerServices.this, responseObj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LocationTrackerServices.this, "No response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "In onDestroyed");
        super.onDestroy();
    }

    private void buildNotification() {
        PendingIntent broadCastIntent = PendingIntent
                .getActivity(this, 0,
                        new Intent(this, SplashActivity.class),
                        PendingIntent.FLAG_IMMUTABLE);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DRIVER_TRACK_CH01)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Tracking your location to find near by orders.")
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(broadCastIntent)
                .setSmallIcon(R.drawable.bhanu_mart_delivery_boy_app_logo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(DRIVER_TRACK_CH01); // Channel ID
        }
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(TRACK_NOTIFICATION_ID, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(DRIVER_TRACK_CH01,
                    DRIVER_TRACK_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("To track cab / driver location.");
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "In onBind");
        return null;
    }
}
