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

import com.cm_grocery.app.Activity.ChooseDeliveryActivity;
import com.cm_grocery.app.Activity.MainActivity;
import com.cm_grocery.app.Adapters.PickUpCompletedOrderAdapter;
import com.cm_grocery.app.Model.PickUpCompletedModel;
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

public class PickUpCompletedOrderActivity extends AppCompatActivity {
    private RecyclerView rv_completedOrders_pickUp;
    private PickUpCompletedOrderAdapter completedAdapter;
    private List<PickUpCompletedModel> completedModelList;
    private SharedPreferences sharedPreferences;
    private String db_id;
    private ImageView img_back_completed_pick;
    private ProgressDialog progressDialog;
    private TextView tv_no_completed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_completed_order);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");


        tv_no_completed = findViewById(R.id.tv_no_completed);
        img_back_completed_pick = findViewById(R.id.img_back_cmp_pick);

        img_back_completed_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rv_completedOrders_pickUp = findViewById(R.id.rv_completedOrders_pickUp);
        rv_completedOrders_pickUp.setHasFixedSize(true);
        rv_completedOrders_pickUp.setLayoutManager(new LinearLayoutManager(this));

        completedPickUpOrders();
        progressDialog.show();
    }

    private void completedPickUpOrders() {
        CM_GroceryDeliveryInterface CMGroceryDeliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = CMGroceryDeliveryInterface.getCompletedPickup(db_id);
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
                            String ordersString = jsonObject.getString("orders");
                            completedModelList = new ArrayList<>();
                            if (!ordersString.equalsIgnoreCase(null)) {
                                JSONArray ordersArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < ordersArray.length(); i++) {
                                    JSONObject ordersObject = ordersArray.getJSONObject(i);
                                    completedModelList.add(new PickUpCompletedModel(
                                            ordersObject.getString("order_id"),
                                            ordersObject.getString("customer_name"),
                                            ordersObject.getString("mobile"),
                                            ordersObject.getString("pickup_address"),
                                            ordersObject.getString("delivery_address")));
                                }
                                completedAdapter = new PickUpCompletedOrderAdapter(PickUpCompletedOrderActivity.this, completedModelList);
                                rv_completedOrders_pickUp.setAdapter(completedAdapter);
                                tv_no_completed.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        progressDialog.hide();
                        tv_no_completed.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progressDialog.hide();
                tv_no_completed.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}