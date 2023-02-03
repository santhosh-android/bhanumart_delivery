package com.cm_grocery.app.Services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseCloudMessagingService extends FirebaseMessagingService {
    private SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.d("Push Notification Body", Objects.requireNonNull(remoteMessage.getNotification().getBody()));
        }
        if (remoteMessage.getData().size() > 0) {
            Log.d("PushNotificationBody", Objects.requireNonNull(remoteMessage.getData().toString()));
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleNotification(remoteMessage.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
                /*JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", remoteMessage.getData().get("title"));
                jsonObject.put("body", remoteMessage.getData().get("body"));
                Log.d("PushNotifydata", jsonObject.toString());
                NotificationHelper.getInstance(getApplicationContext()).createJobNotification(jsonObject);*/
        }
        super.onMessageReceived(remoteMessage);
    }

    private void handleNotification(Map<String, String> remoteMessage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", remoteMessage.get("title"));
            jsonObject.put("body", remoteMessage.get("body"));
            if (remoteMessage.containsKey("payload")) {
                jsonObject.put("payload", new JSONObject(Objects.requireNonNull(remoteMessage.get("payload"))));
            }
            NotificationHelper.getInstance(getApplicationContext()).createJobNotification(jsonObject);

            Log.d("PushNotifyData", remoteMessage.toString());

            if (remoteMessage.containsKey("payload")) {
                JSONObject payload;
                try {
                    payload = new JSONObject(Objects.requireNonNull(remoteMessage.get("payload")));
                    if (payload.getString("type").equals("new_order")) {
                        String orderId = payload.getString("order_id");
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                        launchIntent.putExtra("type", "new_order");
                        if (!orderId.isEmpty()) {
                            launchIntent.putExtra("order_id", orderId);
                        }
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(launchIntent);
                        if (NotificationHelper.getInstance(getApplicationContext()) != null) {
                            //  NotificationHelper.getInstance(getApplicationContext()).playNotificationSound();
                        } else {
                            NotificationHelper helper = NotificationHelper.getInstance(getApplicationContext());
                            //  helper.playNotificationSound();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TAG", "onNewToken: " + token);
        sharedPreferences = getSharedPreferences("zaroorath", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("device_id", token);
        editor.apply();
        sendRegistrationTokenToServer(token);
        super.onNewToken(token);
    }

    private void sendRegistrationTokenToServer(String token) {

    }
}
