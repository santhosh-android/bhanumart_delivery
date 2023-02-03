package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.cm_grocery.app.PickandDrop.PickAndDropDashBoardActivity;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseDeliveryActivity extends AppCompatActivity {

    private TextView tv_normal, tv_pick, tv_wallet_amount, tv_goto_wallet;
    private SharedPreferences sharedPreferences;
    private String db_id;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_delivery);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        tv_normal = findViewById(R.id.tv_normal_order);
        tv_pick = findViewById(R.id.tv_pick_order);
        tv_wallet_amount = findViewById(R.id.tv_wallet_amount);
        tv_goto_wallet = findViewById(R.id.tv_goto_wallet);

        tv_normal.setOnClickListener(v ->
                startActivity(new Intent(ChooseDeliveryActivity.this, MainActivity.class)));

        tv_pick.setOnClickListener(v ->
                startActivity(new Intent(ChooseDeliveryActivity.this, PickAndDropDashBoardActivity.class)));

        tv_goto_wallet.setOnClickListener(v ->
                startActivity(new Intent(ChooseDeliveryActivity.this, WalletAmontTransferActivity.class)));
        walletbalanceApiCall();
    }


    private void walletbalanceApiCall() {
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getWalletbalance(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    progressDialog.hide();
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String balance = jsonObject.getString("balance");
                            tv_wallet_amount.setText("\u20B9" + " " + balance);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        progressDialog.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progressDialog.hide();
            }
        });
    }
}