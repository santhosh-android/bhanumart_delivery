package com.cm_grocery.app.PickandDrop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class NewPickOrderDetailsActivity extends AppCompatActivity {
    private TextView tv_order_id, tv_name, tv_number,
            tv_paymentRemarks, tv_address_pickup, tv_address, txt_description;
    private TextView tv_pick_order_pick;
    private String db_id, orderId;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pick_order_details);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait....");

        orderId = getIntent().getStringExtra("orderId");

        castingViews();
        orderDetailsApiCall();
        tv_pick_order_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDropPickUpApiCall();
            }
        });
    }

    private void pickDropPickUpApiCall() {
        progressDialog.show();
        Map<String, String> confirmPickUp = new HashMap<>();
        confirmPickUp.put("db_id", db_id);
        confirmPickUp.put("order_id", orderId);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.pickdropPickUpApi(confirmPickUp);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
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

                            String message = jsonObject.getString("message");
                            Toast.makeText(NewPickOrderDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewPickOrderDetailsActivity.this, PickAndDropPickUpLIstActivity.class);
                            startActivity(intent);
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

    private void castingViews() {
        tv_order_id = findViewById(R.id.tv_order_id);
        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_paymentRemarks = findViewById(R.id.tv_paymentStatus);
        tv_address_pickup = findViewById(R.id.tv_address_pickup);
        tv_address = findViewById(R.id.tv_address);
        tv_pick_order_pick = findViewById(R.id.tv_pick_order_pick);
        txt_description = findViewById(R.id.txt_description);
    }

    private void orderDetailsApiCall() {
        progressDialog.show();
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getOrderDetailsApi(orderId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.hide();
                if (response.body() != null) {
                    progressDialog.hide();
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray respnseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = respnseArray.getJSONObject(0);

                        String order = jsonObject.getString("order_id");
                        String cus_name = jsonObject.getString("customer_name");
                        String mobile = jsonObject.getString("mobile");
                        String pickup_address = jsonObject.getString("pickup_address");
                        String delivery_address = jsonObject.getString("delivery_address");
                        String description = jsonObject.getString("description");
                        String payment_remarks = jsonObject.getString("payment_remarks");

                        tv_order_id.setText(order);
                        tv_name.setText(cus_name);
                        tv_number.setText(mobile);
                        tv_address_pickup.setText(pickup_address);
                        tv_address.setText(delivery_address);
                        tv_paymentRemarks.setText(payment_remarks);
                        txt_description.setText(description);

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