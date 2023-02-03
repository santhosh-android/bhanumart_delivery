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

public class NewOrdersActivity extends AppCompatActivity implements NewOrderRecyclerAdapter.OrderDetailsInterface {

    private RecyclerView rv_newOrders;
    private ImageView img_back_new;
    private String userId, orderId;
    private SharedPreferences sharedPreferences;
    private TextView tv_no_orders;
    private ProgressDialog progressDialog;
    private List<NewOrdersRecyclerModel> newOrderModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_orders);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        userId = sharedPreferences.getString("UserId", "");

        tv_no_orders = findViewById(R.id.tv_no_orders);
        img_back_new = findViewById(R.id.img_back_new);
        img_back_new.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait Getting Orders");
        rv_newOrders = findViewById(R.id.rv_newOrders);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newOrdersAllApiCall();
    }

    private void newOrdersAllApiCall() {
        Map<String, String> map = new HashMap<>();
        map.put("db_id", userId);
        map.put("start", "0");
        map.put("limit", "100");
        progressDialog.show();
        rv_newOrders.setLayoutManager(new LinearLayoutManager(this));
        rv_newOrders.setHasFixedSize(true);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getOrderApi(map);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.hide();
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("valid")) {
                            String orders = jsonObject.getString("orders");
                            newOrderModelList = new ArrayList<>();
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject ordersObject = jsonArray.getJSONObject(i);
                                    orderId = ordersObject.getString("order_id");
                                    String customerName = ordersObject.getString("customer_name");
                                    String mobile = ordersObject.getString("mobile");
                                    String address = ordersObject.getString("address");

                                    String date_time = ordersObject.getString("date_time");
                                    String payment_option = ordersObject.getString("payment_option");
                                    String payment_status = ordersObject.getString("payment_status");
                                    newOrderModelList.add(new NewOrdersRecyclerModel(orderId, customerName, mobile, address, date_time, payment_option, payment_status));
                                }
                            } else {
                                progressDialog.hide();
                                rv_newOrders.setVisibility(View.GONE);
                                tv_no_orders.setVisibility(View.VISIBLE);
                            }
                            NewOrderRecyclerAdapter adapter = new NewOrderRecyclerAdapter(NewOrdersActivity.this, newOrderModelList,"");
                            rv_newOrders.setAdapter(adapter);
                            adapter.setOrderDetailsInterface(NewOrdersActivity.this);
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        tv_no_orders.setVisibility(View.VISIBLE);
                        rv_newOrders.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onOrderdetailsListener(int position) {
        Intent intent = new Intent(NewOrdersActivity.this, OrderDetailsActivity.class);
        intent.putExtra("orderId", newOrderModelList.get(position).getOrderId());
        startActivity(intent);
    }
}
