package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.cm_grocery.app.UserSessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private ProgressBar pbr;
    private SharedPreferences sharedPreferences;
    private String db_id = "";
    private TextView tvLogout, tvTip;
    private ImageView imgBack;
    private CardView cardTip, cardShift, cardReports, cardTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        castingViews();

        getTipBalance();
        getSettingsAPI();

        tvLogout.setOnClickListener(v -> showLogoutAlert());

        imgBack.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });
        cardReports.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ReportsActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        cardShift.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ShiftsActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        cardTransfer.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, WalletAmontTransferActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
    }

    private void showLogoutAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure want to exit")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent logOut = new Intent(SettingsActivity.this, LoginActivity.class);
                        logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        UserSessionManagement.getUserSessionManagement(SettingsActivity.this).logOut();
                        startActivity(logOut);
                        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void castingViews() {
        pbr = findViewById(R.id.pbr);
        tvTip = findViewById(R.id.tvTipBalance);
        cardTip = findViewById(R.id.cardTip);
        cardShift = findViewById(R.id.cardShift);
        cardReports = findViewById(R.id.cardReports);
        cardTransfer = findViewById(R.id.cardTransfer);
        tvLogout = findViewById(R.id.tvLogout);
        imgBack = findViewById(R.id.imgBack);
    }

    private void getSettingsAPI() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.getSettings();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject arrayObj = responseArray.getJSONObject(0);
                        if (arrayObj.getString("status").equalsIgnoreCase("Valid")) {
                            String delivery_boy_tip_wallet = arrayObj.getString("delivery_boy_tip_wallet");
                            if (delivery_boy_tip_wallet.equalsIgnoreCase("Yes")) {
                                cardTip.setVisibility(View.VISIBLE);
                            } else {
                                cardTip.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.GONE);
            }
        });
    }

    private void getTipBalance() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getTip(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject object = responseArray.getJSONObject(0);
                        if (object.getString("status").equalsIgnoreCase("Valid")) {
                            String balance = object.getString("balance");
                            tvTip.setText("\u20B9" + " " + balance);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.GONE);
            }
        });
    }
}