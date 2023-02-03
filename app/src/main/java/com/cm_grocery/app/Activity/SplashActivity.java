package com.cm_grocery.app.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cm_grocery.app.BuildConfig;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.cm_grocery.app.Services.NotificationHelper;
import com.cm_grocery.app.UserSessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar pbr;
    private String orderId = "", db_id = "";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = getSharedPreferences("fashionMaa", MODE_PRIVATE);
        db_id = preferences.getString("UserId", "");

        pbr = findViewById(R.id.pbr);

       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                VersionCheck();
            }
        }, 2000);*/

        if (getIntent().hasExtra("order_id")) {
            orderId = getIntent().getStringExtra("order_id");
        }
        VersionCheck();
    }

    private void VersionCheck() {
        pbr.setVisibility(View.VISIBLE);
        int version = BuildConfig.VERSION_CODE;
        String sVersion = String.valueOf(version);
        CM_GroceryDeliveryInterface anInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> bodyCall = anInterface.versionCheck(sVersion);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pbr.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject resposneObj = new JSONObject(responseStr);
                        JSONArray responseArray = resposneObj.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            validateOpenNotification();
                           /* if (db_id.isEmpty()) {
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            } else {
                                validateOpenNotification();
                            }*/
                        } else {
                            openAlertForUpdate(resposneObj.getString("message"));
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pbr.setVisibility(View.GONE);
            }
        });
    }

    private void validateOpenNotification() {
        if (orderId != null && !orderId.isEmpty()) {
            NotificationHelper.getInstance(getApplicationContext()).playNotificationSound();
            Intent intent = new Intent(SplashActivity.this, OrderDetailsActivity.class);
            intent.putExtra("id", orderId);
            intent.putExtra("from", "splash");
            startActivity(intent);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void run() {
                    boolean isLoggedIn = UserSessionManagement.getUserSessionManagement(SplashActivity.this).isUserLoggedIn();
                    Intent intent;
                    if (isLoggedIn) {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                    }
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
    }


    private void openAlertForUpdate(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                    startActivity(updateIntent);
                    finish();
                })
                .show();
        alertDialog.show();
    }
}
