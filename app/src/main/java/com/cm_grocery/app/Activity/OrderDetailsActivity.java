package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.Adapters.OrderDetailsListAdapter;
import com.cm_grocery.app.Model.OrderDetailsModelList;
import com.cm_grocery.app.Model.VendorDetailsModel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.cm_grocery.app.Services.NotificationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    private OrderDetailsListAdapter detailsListAdapter;
    private List<OrderDetailsModelList> detailsList;
    private RecyclerView rv_orItems;
    private LinearLayout layoutAccept;
    private ImageView img_back;
    private TextView tv_pick_order, tvReject, tvAccept, tvDirections, tvTip, tv_order_id, tv_name, tv_number, tv_paymentType, tv_paymentStatus, tv_address, tv_ttlAmount, tv_date;
    private String orderId, name, building_name, street, location, landmark, db_id, flat_no;
    String order_id, customer_name, mobile, date_time, address_type = "",
            payment_option, payment_status, sub_total, shipping, vat, total, latitude = "", longitude = "";
    private String newOrderId, from = "", newID = "";
    private SharedPreferences sharedPreferences;
    private ProgressBar pb_bar;
    private VendorDetailsModel vendorDeailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        orderId = getIntent().getStringExtra("orderId");
        castingViews();
        onClick();

        if (getIntent().hasExtra("id")) {
            newOrderId = getIntent().getStringExtra("orderId");
            orderId = getIntent().getStringExtra("id");
            from = getIntent().getStringExtra("from");
        }

        newOrderDetails();

        tvDirections.setOnClickListener(view -> {
            if (latitude.isEmpty() || latitude.equals(null) || longitude.isEmpty() || longitude.equals(null)) {
                Toast.makeText(OrderDetailsActivity.this, "No directions found", Toast.LENGTH_SHORT).show();

            } else {
                if (latitude != null && longitude != null) {
                    float lat = Float.parseFloat(latitude);
                    float longi = Float.parseFloat(longitude);
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", lat, longi);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        });

        if (from.equalsIgnoreCase("new")) {
            tv_pick_order.setVisibility(View.VISIBLE);
            layoutAccept.setVisibility(View.GONE);
        } else {
            tv_pick_order.setVisibility(View.GONE);
            layoutAccept.setVisibility(View.VISIBLE);
        }

        tvAccept.setOnClickListener(v -> acceptOrderApiCall());
        tvReject.setOnClickListener(v -> cancelOrder());
    }

    private void cancelOrder() {
        pb_bar.setVisibility(View.VISIBLE);
        NotificationHelper.getInstance(getApplicationContext()).stopNotificationSound();
        HashMap<String, String> map = new HashMap<>();
        map.put("action", "cancel_order");
        map.put("db_id", db_id);
        map.put("order_id", orderId);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.cancelOrder(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pb_bar.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                startActivity(new Intent(OrderDetailsActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        } else if (jsonObject.getString("status").equalsIgnoreCase("Invalid")) {
                            Toast.makeText(OrderDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pb_bar.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pb_bar.setVisibility(View.GONE);

            }
        });
    }

    private void acceptOrderApiCall() {
        pb_bar.setVisibility(View.VISIBLE);
        NotificationHelper.getInstance(getApplicationContext()).stopNotificationSound();
        HashMap<String, String> map = new HashMap<>();
        map.put("action", "accept_order");
        map.put("db_id", db_id);
        map.put("order_id", orderId);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.acceptOrder(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pb_bar.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray jsonArray = responseObj.getJSONArray("response");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            Toast.makeText(OrderDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            finishAffinity();
                            startActivity(new Intent(OrderDetailsActivity.this, PickupOrderActivity.class));
                        } else if (jsonObject.getString("status").equalsIgnoreCase("Invalid")) {
                            String messgae = jsonObject.getString("message");
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(OrderDetailsActivity.this);
                            builder.setMessage(messgae)
                                    .setCancelable(false)
                                    .setIcon(R.drawable.ic_logout)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            NotificationHelper.getInstance(getApplicationContext()).stopNotificationSound();
                                            onBackPressed();
                                            finish();
                                        }
                                    });

                            androidx.appcompat.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pb_bar.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pb_bar.setVisibility(View.GONE);

            }
        });

    }

    private void newOrderDetails() {
        pb_bar.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface pheebsDeliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = pheebsDeliveryInterface.getOrderDetails(orderId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb_bar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray respnseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = respnseArray.getJSONObject(0);
                        JSONArray itemsArray = jsonObject.getJSONArray("items");
                        detailsList = new ArrayList<>();
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);
                            String id = itemObject.getString("id");
                            String product = itemObject.getString("product");
                            String price = itemObject.getString("price");
                            String qty = itemObject.getString("qty");
                            String sub_total = itemObject.getString("sub_total");
                            String image = itemObject.getString("image");
                            detailsList.add(new OrderDetailsModelList(id, product, "\u20b9 " + price,
                                    "Qty :" + qty, sub_total, image));
                        }
                        recyclerViewAttach();

                        JSONArray addresArray = jsonObject.getJSONArray("address_array");
                        JSONObject addressObject = addresArray.getJSONObject(0);
                        name = addressObject.getString("name");
                        building_name = addressObject.getString("address");
                        String city = addressObject.getString("city");
                        String pincode = addressObject.getString("pincode");
                        location = addressObject.getString("location");
                        landmark = addressObject.getString("landmark");
                        latitude = addressObject.getString("latitude");
                        longitude = addressObject.getString("longitude");

                        if (latitude.isEmpty() || latitude.equals(null)
                                || longitude.isEmpty() || longitude.equals(null) || latitude.equalsIgnoreCase("undefined") || longitude.equalsIgnoreCase("undefined")) {
                            tvDirections.setVisibility(View.GONE);

                        }

                        order_id = jsonObject.getString("order_id");
                        customer_name = jsonObject.getString("customer_name");
                        mobile = jsonObject.getString("mobile");
                        date_time = jsonObject.getString("date_time");
                        payment_option = jsonObject.getString("payment_option");
                        payment_status = jsonObject.getString("payment_status");
                        sub_total = jsonObject.getString("sub_total");
                        shipping = jsonObject.getString("shipping");
                        vat = jsonObject.getString("gst");
                        String tip = jsonObject.getString("tip");
                        total = jsonObject.getString("total");

                        tv_order_id.setText(order_id);
                        tv_name.setText(customer_name);
                        tv_number.setText(mobile);
                        tv_date.setText(date_time);
                        tv_paymentType.setText(payment_option);
                        tv_paymentStatus.setText(payment_status);
                        tv_ttlAmount.setText("\u20b9" + total);
                        tv_address.setText(building_name + "," + location + "," + city + "," + pincode);
                        tvTip.setText(tip);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void recyclerViewAttach() {
        rv_orItems.setHasFixedSize(true);
        rv_orItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        detailsListAdapter = new OrderDetailsListAdapter(this, detailsList);
        rv_orItems.setAdapter(detailsListAdapter);
    }

    private void onClick() {
        img_back.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });
        tv_pick_order.setOnClickListener(v -> orderPickedApiCall(pickUpApiMap()));
    }

    private Map<String, String> pickUpApiMap() {
        Map<String, String> createPickUpMap = new HashMap<>();
        createPickUpMap.put("order_id", order_id);
        createPickUpMap.put("db_id", db_id);
        return createPickUpMap;
    }

    private void orderPickedApiCall(Map<String, String> pickUpMap) {
        pb_bar.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.pickUpOrderApi(pickUpMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb_bar.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String message = jsonObject.getString("message");
                            Toast.makeText(OrderDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            finishAffinity();
                            startActivity(new Intent(OrderDetailsActivity.this, PickupOrderActivity.class));
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void castingViews() {
        rv_orItems = findViewById(R.id.rv_orItems);
        img_back = findViewById(R.id.img_back);
        tv_pick_order = findViewById(R.id.tv_pick_order);
        tvTip = findViewById(R.id.tvTip);
        pb_bar = findViewById(R.id.pb_pbar);
        tv_order_id = findViewById(R.id.tv_order_id);
        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_paymentType = findViewById(R.id.tv_paymentType);
        tv_paymentStatus = findViewById(R.id.tv_paymentStatus);
        tv_address = findViewById(R.id.tv_address);
        tv_ttlAmount = findViewById(R.id.tv_ttlAmount);
        tv_date = findViewById(R.id.tv_date);
        tvDirections = findViewById(R.id.tvDirections);
        layoutAccept = findViewById(R.id.layoutAccept);
        tvReject = findViewById(R.id.tvReject);
        tvAccept = findViewById(R.id.tvAccept);
    }
}
