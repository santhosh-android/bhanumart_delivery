package com.cm_grocery.app.PickandDrop;

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

import com.cm_grocery.app.Adapters.NewPickAndDropAdapter;
import com.cm_grocery.app.Model.NewPickAndDropModel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPickAndDropOrders extends AppCompatActivity implements NewPickAndDropAdapter.NewPickUpOrderListener {
    private RecyclerView rv_newPick_orders;
    private String userId, orderId;
    private SharedPreferences sharedPreferences;
    private List<NewPickAndDropModel> newPickDropModelList;
    private ProgressDialog progressDialog;
    private TextView noOrders;
    private ImageView img_back_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pick_and_drop_orders);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        userId = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        noOrders=findViewById(R.id.noOrders_pickDrop);
        img_back_new=findViewById(R.id.img_back_new);
        img_back_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rv_newPick_orders = findViewById(R.id.rv_newPick_orders);
        progressDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        newPickOrderApiCall();
    }

    private void newPickOrderApiCall() {
        CM_GroceryDeliveryInterface newOrderInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = newOrderInterface.getPickNewOrders(userId);
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
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            newPickDropModelList = new ArrayList<>();
                            if (!orders.equalsIgnoreCase(null)) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject orderObject = jsonArray.getJSONObject(i);
                                    orderId = orderObject.getString("order_id");
                                    String customerName = orderObject.getString("customer_name");
                                    String mobile = orderObject.getString("mobile");
                                    String pickAddress = orderObject.getString("pickup_address");
                                    String address = orderObject.getString("delivery_address");

                                    newPickDropModelList.add(new NewPickAndDropModel(orderId, customerName, mobile, pickAddress, address));
                                    newPickUpRvInitilization();
                                    noOrders.setVisibility(View.GONE);
                                }
                            } else {
                                noOrders.setVisibility(View.VISIBLE);
                                progressDialog.hide();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        progressDialog.hide();
                        noOrders.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progressDialog.hide();
                noOrders.setVisibility(View.VISIBLE);
            }
        });
    }

    private void newPickUpRvInitilization() {
        rv_newPick_orders.setHasFixedSize(true);
        rv_newPick_orders.setLayoutManager(new LinearLayoutManager(this));
        NewPickAndDropAdapter pickAndDropAdapter = new NewPickAndDropAdapter(this, newPickDropModelList);
        pickAndDropAdapter.setNewPickUpOrderListener(this);
        rv_newPick_orders.setAdapter(pickAndDropAdapter);
    }

    @Override
    public void onNewOrderViewlistener(int position) {
        Intent intent = new Intent(NewPickAndDropOrders.this, NewPickOrderDetailsActivity.class);
        intent.putExtra("orderId", newPickDropModelList.get(position).getOrderPickId());
        startActivity(intent);
    }
}