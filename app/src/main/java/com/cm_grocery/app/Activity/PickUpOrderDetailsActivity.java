package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.Adapters.OrderDetailsListAdapter;
import com.cm_grocery.app.Model.VendorDetailsModel;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.cm_grocery.app.fragments.BottomSheetFragment;
import com.cm_grocery.app.Model.OrderDetailsModelList;
import com.cm_grocery.app.R;
import com.cm_grocery.app.fragments.CodBottomSheetFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickUpOrderDetailsActivity extends AppCompatActivity {

    private TextView tv_delivery_order;
    private OrderDetailsListAdapter orderDetailsListAdapter;
    private List<OrderDetailsModelList> detailsModelList;
    private RecyclerView rv_orDetails_pick;
    private ImageView bck_image;
    private SharedPreferences sharedPreferences;
    private TextView tv_order_id, tvDirections, tvTip, tv_name, tv_number, tv_paymentType, tv_paymentStatus, tv_address, tv_ttlAmount, tv_date;
    private String orderId;
    private String order_Id, name, building_name, street, location, landmark, db_id, flat_no;
    String order_id, customer_name, mobile, date_time,
            payment_option, payment_status, sub_total, shipping, vat, total, latitude = "", longitude = "";
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_order_details);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        orderId = getIntent().getStringExtra("order_id");
        castingViews();
        onClick();
        pickUpOrderDetailsApiCall();

        tvDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude.isEmpty() || latitude.equals(null) || longitude.isEmpty() || longitude.equals(null)) {
                    Toast.makeText(PickUpOrderDetailsActivity.this, "No directions found", Toast.LENGTH_SHORT).show();
                } else {
                    if (latitude != null && longitude != null) {
                        float lat = Float.parseFloat(latitude);
                        float longi = Float.parseFloat(longitude);
                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", lat, longi);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void pickUpOrderDetailsApiCall() {
        progress.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface pheebsDeliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = pheebsDeliveryInterface.getOrderDetails(orderId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {
                    progress.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray respnseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = respnseArray.getJSONObject(0);
                        JSONArray itemsArray = jsonObject.getJSONArray("items");
                        detailsModelList = new ArrayList<>();
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);
                            String id = itemObject.getString("id");
                            String product = itemObject.getString("product");
                            String price = itemObject.getString("price");
                            String qty = itemObject.getString("qty");
                            String sub_total = itemObject.getString("sub_total");
                            String image = itemObject.getString("image");

                            detailsModelList.add(new OrderDetailsModelList(id, product, "\u20b9 " + price, "Qty :" + qty, sub_total, image));
                        }
                        orderPickupItemsRv();
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

                        if (latitude.isEmpty() || latitude == null
                                || longitude.isEmpty() || longitude == null || latitude.equalsIgnoreCase("undefined") || longitude.equalsIgnoreCase("undefined")) {
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

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        progress.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void castingViews() {
        tv_delivery_order = findViewById(R.id.tv_delivery_order);
        rv_orDetails_pick = findViewById(R.id.rv_orDetails_pick);
        bck_image = findViewById(R.id.img_back_details);
        tvTip = findViewById(R.id.tvTip);
        tv_order_id = findViewById(R.id.tv_order_id);
        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_paymentType = findViewById(R.id.tv_paymentType);
        tv_paymentStatus = findViewById(R.id.tv_paymentStatus);
        tv_address = findViewById(R.id.tv_address);
        tv_ttlAmount = findViewById(R.id.tv_ttlAmount);
        tv_date = findViewById(R.id.tv_date);
        tvDirections = findViewById(R.id.tvDirections);
        progress = findViewById(R.id.progress);
    }


    private void onClick() {
        tv_delivery_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payment_option.equalsIgnoreCase("COD")) {
                    openCODAlert();
                } else {
                    orderDeliveryApiCall();
                }
            }
        });

        bck_image.setOnClickListener(v -> {
                    onBackPressed();
                    overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                }
        );
    }

    private void openCODAlert() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.fragment_cod_bottom_sheet, null);

        EditText etAmount, et_payment_via, et_comments;
        Button btn_submit;
        TextView tv_collect;
        btn_submit = view.findViewById(R.id.btn_submit);
        etAmount = view.findViewById(R.id.etAmount);
        et_payment_via = view.findViewById(R.id.et_payment_via);
        et_comments = view.findViewById(R.id.et_comments);
        tv_collect = view.findViewById(R.id.tv_collect);
        tv_collect.setText("Please Collect Cash : " + total);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etAmount.getText().toString().equals(total)) {
                    Toast.makeText(PickUpOrderDetailsActivity.this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                } else if (et_payment_via.getText().toString().isEmpty()) {
                    Toast.makeText(PickUpOrderDetailsActivity.this, "Please enter payment via", Toast.LENGTH_SHORT).show();
                } else {
                    progress.setVisibility(View.VISIBLE);
                    Map<String, String> submitMap = new HashMap<>();
                    submitMap.put("order_id", order_id);
                    submitMap.put("db_id", db_id);
                    submitMap.put("amount", etAmount.getText().toString());
                    submitMap.put("payment_via", et_payment_via.getText().toString());
                    submitMap.put("payment_remarks", et_comments.getText().toString());
                    CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
                    Call<ResponseBody> responseBodyCall = deliveryInterface.orderDeliveryApiCall(submitMap);
                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.body() != null) {
                                progress.setVisibility(View.GONE);
                                try {
                                    String responseString = new String(response.body().bytes());
                                    JSONObject responseObject = new JSONObject(responseString);
                                    JSONArray responseArray = responseObject.getJSONArray("response");
                                    JSONObject jsonObject = responseArray.getJSONObject(0);
                                    if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(PickUpOrderDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(PickUpOrderDetailsActivity.this, MainActivity.class));
                                        alertDialog.dismiss();
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    progress.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            progress.setVisibility(View.GONE);
                            alertDialog.dismiss();
                        }
                    });

                }
            }
        });

        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    private void orderDeliveryApiCall() {
        progress.setVisibility(View.VISIBLE);
        Map<String, String> deliveryMap = new HashMap<>();
        deliveryMap.put("db_id", db_id);
        deliveryMap.put("order_id", order_id);

        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.orderDeliveryApiCall(deliveryMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    progress.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);

                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String message = jsonObject.getString("message");
                            Toast.makeText(PickUpOrderDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PickUpOrderDetailsActivity.this, MainActivity.class));
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        progress.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progress.setVisibility(View.GONE);

            }
        });
    }

    private void orderPickupItemsRv() {
        rv_orDetails_pick.setHasFixedSize(true);
        rv_orDetails_pick.setLayoutManager(new LinearLayoutManager(this));
        orderDetailsListAdapter = new OrderDetailsListAdapter(PickUpOrderDetailsActivity.this, detailsModelList);
        rv_orDetails_pick.setAdapter(orderDetailsListAdapter);
    }
}
