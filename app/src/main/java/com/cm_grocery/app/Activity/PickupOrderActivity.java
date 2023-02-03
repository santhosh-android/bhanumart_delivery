package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm_grocery.app.Adapters.NewOrderRecyclerAdapter;
import com.cm_grocery.app.Model.NewOrdersRecyclerModel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupOrderActivity extends AppCompatActivity implements NewOrderRecyclerAdapter.OrderDetailsInterface {

    private NewOrderRecyclerAdapter pickupOrderAdapter;
    private RecyclerView rv_pickupOrderList;
    private ImageView img_back_pickup;
    private SharedPreferences sharedPreferences;
    private String db_id, orderId;
    private List<NewOrdersRecyclerModel> pickModelList;
    private TextView tv_no_pickorders;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_order);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        castingViews();
        img_back_pickup.setOnClickListener(v -> {
                    overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                    onBackPressed();
                }
        );

        rv_pickupOrderList.setHasFixedSize(true);
        rv_pickupOrderList.setLayoutManager(new LinearLayoutManager(this));

        pickUpOrderApiCall();
    }

    private void pickUpOrderApiCall() {
        Map<String, String> map = new HashMap<>();
        map.put("db_id", db_id);
        map.put("start", "0");
        map.put("limit", "100");
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getPickUpOrders(map);
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
                            String orders = jsonObject.getString("orders");
                            pickModelList = new ArrayList<>();
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray itemsArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject itemsObject = itemsArray.getJSONObject(i);
                                    orderId = itemsObject.getString("order_id");
                                    String customerName = itemsObject.getString("customer_name");
                                    String mobile = itemsObject.getString("mobile");
                                    String address = itemsObject.getString("address");
                                    String date_time = itemsObject.getString("date_time");
                                    String payment_option = itemsObject.getString("payment_option");
                                    String payment_status = itemsObject.getString("payment_status");
                                    pickModelList.add(new NewOrdersRecyclerModel(orderId, customerName, mobile,
                                            address, date_time, payment_option, payment_status));
                                }

                            } else {
                                tv_no_pickorders.setVisibility(View.VISIBLE);
                                progressDialog.hide();
                            }
                            pickupOrderAdapter = new NewOrderRecyclerAdapter(PickupOrderActivity.this, pickModelList,"");
                            pickupOrderAdapter.setOrderDetailsInterface(PickupOrderActivity.this);
                            rv_pickupOrderList.setAdapter(pickupOrderAdapter);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        tv_no_pickorders.setVisibility(View.VISIBLE);
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
        rv_pickupOrderList = findViewById(R.id.rv_pickupOrderList);
        img_back_pickup = findViewById(R.id.img_back_pickup);
        tv_no_pickorders = findViewById(R.id.tv_no_pickorders);
    }

    @Override
    public void onOrderdetailsListener(int position) {
        Intent intent = new Intent(PickupOrderActivity.this, PickUpOrderDetailsActivity.class);
        intent.putExtra("order_id", pickModelList.get(position).getOrderId());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }
}