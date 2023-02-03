package com.cm_grocery.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.Adapters.DriverShiftAdapter;
import com.cm_grocery.app.Adapters.NewOrderRecyclerAdapter;
import com.cm_grocery.app.Model.DriverShiftModel;
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

public class ShiftsActivity extends AppCompatActivity {
    private RecyclerView rvShifts;
    private TextView btnUpdate;
    private ProgressBar pbr;
    private SharedPreferences sharedPreferences;
    private String db_id = "", currentShift = "", updatedShift = "";
    private List<DriverShiftModel> shiftModelList;
    private DriverShiftAdapter shiftAdapter;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts);
        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        castingViews();

        getShifts();

        btnUpdate.setOnClickListener(v -> {
            if (updatedShift.isEmpty()) {
                Toast.makeText(ShiftsActivity.this, "Please select updated shift", Toast.LENGTH_SHORT).show();
            } else {
                updateShiftAPI();
            }
        });

        imgBack.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        });

    }

    private void updateShiftAPI() {
        pbr.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("db_id", db_id);
        map.put("shift_id", updatedShift);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.updateShift(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            openAlert(jsonObject.getString("message"));
                        } else {
                            Toast.makeText(ShiftsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
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

    private void openAlert(String message) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_logout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                    }
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void getShifts() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.getShifts(db_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            currentShift = jsonObject.getString("current_shift_id");
                            String shifts = jsonObject.getString("shifts");
                            shiftModelList = new ArrayList<>();
                            if (!shifts.equalsIgnoreCase("null")) {
                                JSONArray shiftArray = jsonObject.getJSONArray("shifts");
                                for (int i = 0; i < shiftArray.length(); i++) {
                                    JSONObject shiftObj = shiftArray.getJSONObject(i);
                                    String shift = shiftObj.getString("shift");
                                    String status = shiftObj.getString("status");
                                    String shift_id = shiftObj.getString("shift_id");
                                    shiftModelList.add(new DriverShiftModel(status, shift_id, shift));
                                }
                            }
                            shiftAdapter = new DriverShiftAdapter(ShiftsActivity.this, shiftModelList, currentShift);
                            shiftAdapter.setCheckBoxListener(new DriverShiftAdapter.CheckBoxListener() {
                                @Override
                                public void onChecked(int pos) {
                                    updatedShift = shiftModelList.get(pos).getShift_id();
                                }
                            });
                            rvShifts.setAdapter(shiftAdapter);
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
        rvShifts = findViewById(R.id.rvShifts);
        btnUpdate = findViewById(R.id.btnUpdate);
        pbr = findViewById(R.id.pbr);
        imgBack = findViewById(R.id.imgBack);
    }
}