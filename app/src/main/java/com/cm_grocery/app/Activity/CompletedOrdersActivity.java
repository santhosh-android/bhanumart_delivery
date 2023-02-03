package com.cm_grocery.app.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cm_grocery.app.Adapters.NewOrderRecyclerAdapter;
import com.cm_grocery.app.Model.NewOrdersRecyclerModel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedOrdersActivity extends AppCompatActivity {

    private NewOrderRecyclerAdapter completedAdapter;
    private RecyclerView rv_completedOrders;
    private ImageView img_back_cmp;
    private SharedPreferences sharedPreferences;
    private String db_id;
    private ProgressBar pb_bar;
    private TextView no_completed;
    private List<NewOrdersRecyclerModel> newOrderModelList;
    private ProgressDialog progressDialog;
    private TextView tvFrmDate, tvToDate;
    private DatePickerDialog datePickerDialog;
    private String frmDate = "", toDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_orders);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        img_back_cmp = findViewById(R.id.img_back_cmp);
        tvFrmDate = findViewById(R.id.tvFrmDate);
        tvToDate = findViewById(R.id.tvToDate);

        pb_bar = findViewById(R.id.pb_pbar);
        img_back_cmp.setOnClickListener(v -> onBackPressed());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");


        no_completed = findViewById(R.id.tv_no_cmorders);

        rv_completedOrders = findViewById(R.id.rv_completedOrders);
        rv_completedOrders.setHasFixedSize(true);
        rv_completedOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        completedApiCall();

        tvFrmDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            final Calendar newCalendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(CompletedOrdersActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    newCalendar.set(dayOfMonth, month, year);
                    String dateStr = dayOfMonth + "-" + (month + 1) + "-" + year;
                    tvFrmDate.setText(dateStr);
                    frmDate = dateStr;
                    if (!toDate.isEmpty()) {
                        completedApiCall();
                    }
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();

        });

        tvToDate.setOnClickListener(v -> {
            if (tvFrmDate.getText().toString().isEmpty()) {
                Toast.makeText(CompletedOrdersActivity.this, "Please select the start date first", Toast.LENGTH_SHORT).show();
            } else {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                final Calendar newCalendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(CompletedOrdersActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        newCalendar.set(dayOfMonth, month, year);
                        String dateStr = dayOfMonth + "-" + (month + 1) + "-" + year;
                        tvToDate.setText(dateStr);
                        toDate = dateStr;
                        completedApiCall();
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }

        });

    }

    private void completedApiCall() {
        progressDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("db_id", db_id);
        map.put("start", "0");
        map.put("limit", "5000");
        map.put("start_date", tvFrmDate.getText().toString());
        map.put("end_date", tvToDate.getText().toString());
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getCompletedOrders(map);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.hide();
                if (response.body() != null) {
                    try {
                        String respnseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(respnseString);
                        JSONArray respnseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = respnseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            newOrderModelList = new ArrayList<>();
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject ordersObject = jsonArray.getJSONObject(i);
                                    String orderId = ordersObject.getString("order_id");
                                    String customerName = ordersObject.getString("customer_name");
                                    String mobile = ordersObject.getString("mobile");
                                    String address = ordersObject.getString("address");
                                    String date_time = ordersObject.getString("date_time");
                                    String payment_option = ordersObject.getString("payment_option");
                                    String payment_status = ordersObject.getString("payment_status");
                                    newOrderModelList.add(new NewOrdersRecyclerModel(orderId, customerName,
                                            mobile, address, date_time, payment_option, payment_status));
                                }
                            } else {
                                no_completed.setVisibility(View.VISIBLE);
                                progressDialog.hide();
                            }
                            completedAdapter = new NewOrderRecyclerAdapter(CompletedOrdersActivity.this, newOrderModelList, "complete");
                            rv_completedOrders.setAdapter(completedAdapter);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        no_completed.setVisibility(View.VISIBLE);
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
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }
}
