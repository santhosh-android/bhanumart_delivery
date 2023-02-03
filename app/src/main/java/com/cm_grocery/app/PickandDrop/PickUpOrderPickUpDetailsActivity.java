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
import com.cm_grocery.app.fragments.PickDropPayBottomSheetFragment;

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

public class PickUpOrderPickUpDetailsActivity extends AppCompatActivity {
    private TextView tv_order_id_pickDetails, tv_name_pickDetails, tv_number_pickDetails, tv_paymentStatus_pickDetails,
            tv_address_pickup_pickDetails, tv_address_pickDetails, tv_orderdel,txt_description;
    private String db_id, orderId;
    private String order;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_order_pick_up_details);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait....");

        orderId = getIntent().getStringExtra("order_id");
        castingViews();
        pickUpPickDetails();
        tv_orderdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickDropPayBottomSheetFragment bottomSheet = PickDropPayBottomSheetFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("order_id",order);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(),"pickandDropBottomFrag");
                //orderDeliveryApiCall();
            }
        });
    }

    private void pickUpPickDetails() {
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

                        order = jsonObject.getString("order_id");
                        String cus_name = jsonObject.getString("customer_name");
                        String mobile = jsonObject.getString("mobile");
                        String pickup_address = jsonObject.getString("pickup_address");
                        String delivery_address = jsonObject.getString("delivery_address");
                        String description = jsonObject.getString("description");
                        String payment_remarks = jsonObject.getString("payment_remarks");

                        tv_order_id_pickDetails.setText(order);
                        tv_name_pickDetails.setText(cus_name);
                        tv_number_pickDetails.setText(mobile);
                        tv_address_pickup_pickDetails.setText(pickup_address);
                        tv_address_pickDetails.setText(delivery_address);
                        tv_paymentStatus_pickDetails.setText(payment_remarks);
                        txt_description.setText(description);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
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

    private void orderDeliveryApiCall() {
        progressDialog.show();
        Map<String, String> deliveryMap = new HashMap<>();
        deliveryMap.put("db_id", db_id);
        deliveryMap.put("order_id", order);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.confirmDeliviryApi(deliveryMap);
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
                            Toast.makeText(PickUpOrderPickUpDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PickUpOrderPickUpDetailsActivity.this,PickAndDropDashBoardActivity.class));
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
        tv_order_id_pickDetails = findViewById(R.id.tv_order_id_pickDetails);
        tv_name_pickDetails = findViewById(R.id.tv_name_pickDetails);
        tv_number_pickDetails = findViewById(R.id.tv_number_pickDetails);
        tv_paymentStatus_pickDetails = findViewById(R.id.tv_paymentStatus_pickDetails);
        tv_address_pickup_pickDetails = findViewById(R.id.tv_address_pickup_pickDetails);
        tv_address_pickDetails = findViewById(R.id.tv_address_pickDetails);
        tv_orderdel = findViewById(R.id.tv_orderdel);
        txt_description = findViewById(R.id.txt_description);
    }
}