package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.Adapters.DriverShiftAdapter;
import com.cm_grocery.app.Adapters.ReportsAdapter;
import com.cm_grocery.app.Model.DriverShiftModel;
import com.cm_grocery.app.Model.ReportsModel;
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

public class ReportsActivity extends AppCompatActivity {
    private ImageView imgBack;
    private ProgressBar pbr;
    private String frmDate = "", toDate = "", db_id = "";
    private SharedPreferences sharedPreferences;
    private TextView tvFrmDate, tvToDate, tvOrders, tvTraveled, tvAmount, tvNoReports;
    private DatePickerDialog datePickerDialog;
    private List<ReportsModel> reportsModel;
    private ReportsAdapter reportsAdapter;
    private RecyclerView rvReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
        castingViews();
        getReports();

        imgBack.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });
        tvFrmDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            final Calendar newCalendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(ReportsActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    newCalendar.set(dayOfMonth, month, year);
                    String dateStr = dayOfMonth + "-" + (month + 1) + "-" + year;
                    tvFrmDate.setText(dateStr);
                    frmDate = dateStr;
                    if (!toDate.isEmpty()) {
                        getReports();
                    }
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();

        });

        tvToDate.setOnClickListener(v -> {
            if (tvFrmDate.getText().toString().isEmpty()) {
                Toast.makeText(ReportsActivity.this, "Please select the start date first", Toast.LENGTH_SHORT).show();
            } else {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                final Calendar newCalendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(ReportsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        newCalendar.set(dayOfMonth, month, year);
                        String dateStr = dayOfMonth + "-" + (month + 1) + "-" + year;
                        tvToDate.setText(dateStr);
                        toDate = dateStr;
                        getReports();
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }

        });
    }

    private void getReports() {
        pbr.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("db_id", db_id);
        map.put("from_date", frmDate);
        map.put("to_date", toDate);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.getReports(map);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        reportsModel = new ArrayList<>();
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject reportObj = responseArray.getJSONObject(i);
                            reportsModel.add(new ReportsModel(
                                    reportObj.getString("name"),
                                    reportObj.getString("value")
                            ));
                        }
                        reportsAdapter = new ReportsAdapter(ReportsActivity.this, reportsModel);
                        rvReports.setAdapter(reportsAdapter);
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
        imgBack = findViewById(R.id.imgBack);
        pbr = findViewById(R.id.pbr);
        tvFrmDate = findViewById(R.id.tvFrmDate);
        tvToDate = findViewById(R.id.tvToDate);
        rvReports = findViewById(R.id.rvReports);
        tvNoReports = findViewById(R.id.tvNoReports);
    }
}