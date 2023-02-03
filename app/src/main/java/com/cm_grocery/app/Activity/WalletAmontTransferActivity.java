package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

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

public class WalletAmontTransferActivity extends AppCompatActivity {
    private TextView tv_wallet_amnt;
    private Button btn_send_money;
    private EditText et_amount;
    private SharedPreferences sharedPreferences;
    private String db_id, amount = "", balance = "";
    private ImageView img_back_wallet;
    private ProgressBar pbr;
    private double number, amountInt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_amont_transfer);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        castingViews();
        walletAmountShowApi();
        onClick();

    }

    private void onClick() {
        btn_send_money.setOnClickListener(v -> {
            if (!et_amount.getText().toString().isEmpty()) {
                amount = et_amount.getText().toString().trim();
                number = Double.parseDouble(balance);
                amountInt = Double.parseDouble(amount);
                if (number < amountInt) {
                    Toast.makeText(this, "Transfer amount not greater than balance", Toast.LENGTH_SHORT).show();
                } else {
                    transferAmountApiCall();
                }
            } else {
                Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            }

        });
        img_back_wallet.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }

    private void castingViews() {
        tv_wallet_amnt = findViewById(R.id.tv_wallet_amnt);
        btn_send_money = findViewById(R.id.btn_send_money);
        et_amount = findViewById(R.id.et_amount);
        img_back_wallet = findViewById(R.id.img_back_wallet);
        pbr = findViewById(R.id.pbr);
    }


    private void walletAmountShowApi() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getWalletbalance(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            balance = jsonObject.getString("balance");
                            tv_wallet_amnt.setText("\u20B9" + " " + balance);
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

    private void transferAmountApiCall() {
        pbr.setVisibility(View.VISIBLE);
        amount = et_amount.getText().toString().trim();
        Map<String, String> sendMoneyMap = new HashMap<>();
        sendMoneyMap.put("db_id", db_id);
        sendMoneyMap.put("amount", amount);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.sendWalletMoney(sendMoneyMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray resposneArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = resposneArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String message = jsonObject.getString("message");
                            Toast.makeText(WalletAmontTransferActivity.this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(WalletAmontTransferActivity.this, MainActivity.class));
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