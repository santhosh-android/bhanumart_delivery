package com.cm_grocery.app.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.Adapters.HomeCategoriesAdapter;
import com.cm_grocery.app.Model.HomeCategoryMoadel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.cm_grocery.app.UserSessionManagement;
import com.cm_grocery.app.helper.AppPreferences;
import com.cm_grocery.app.helper.GpsTracker;
import com.cm_grocery.app.locationServices.LocationModel;
import com.cm_grocery.app.locationServices.LocationTrackerServices;

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

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements HomeCategoriesAdapter.CategeoryListenerInterface {
    private HomeCategoriesAdapter categoriesAdapter;
    private List<HomeCategoryMoadel> categoryList;
    private RecyclerView rv_home_ctgs;
    private ImageView img_logout, ivSettings;
    private SharedPreferences sharedPreferences;
    private String db_id = "", new_orders = "", pickup_orders = "",
            completed_orders = "", name = "", mobile = "", email = "", status = "", avaliability = "", shift = "";
    private ProgressBar pbr;
    private TextView tv_wallet_amount, tv_goto_wallet, tvName, tvMobile, tvEmail, tvTip, tvReports, tvShift;
    private SwipeRefreshLayout swipe_layout;
    private CardView cardTip, cardShift;
    private SwitchCompat statusSwitch;
    private Intent serviceIntent = null;
    public GpsTracker gpsTracker;
    public String lati = "";
    public String Logitude = "";

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                for (Map.Entry<String, Boolean> set : permissions.entrySet()) {
                    if (!set.getValue()) {
                        return;
                    }
                }

            });

    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    checkForDrawOnOtherAppSettings();
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForDrawOnOtherAppSettings() {
        if (!Settings.canDrawOverlays(this)) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage("Please give permission to app overlay")
                    .setCancelable(false)
                    .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.Q)
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                                mStartForResult.launch(new Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getOpPackageName())));
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.show();
        } else {
            statrForegroundService();
        }
    }

    private void statrForegroundService() {
        serviceIntent = new Intent(this, LocationTrackerServices.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        castingViews();

        statrForegroundService();
        getDeviceLocation();
        checkForDrawOnOtherAppSettings();


        tv_goto_wallet.setOnClickListener(v -> {
                    startActivity(new Intent(MainActivity.this, WalletAmontTransferActivity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
        );
        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = "Online";
                    statusAPICall();
                    statusSwitch.setText("Online");
                } else {
                    status = "Offline";
                    statusSwitch.setText("Offline");
                    statusAPICall();
                }
            }
        });

        ivSettings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        cardShift.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ShiftsActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        tvReports.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReportsActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProfile();
                getDashBoardCount();
                walletbalanceApiCall();
                getTipBalance();
                setAdapter();
                swipe_layout.setRefreshing(false);
            }
        });
        img_logout.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Are you sure want to exit")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent logOut = new Intent(MainActivity.this, LoginActivity.class);
                            logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();
                            UserSessionManagement.getUserSessionManagement(MainActivity.this).logOut();
                            startActivity(logOut);
                            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        });
    }

    private void getDeviceLocation() {
        gpsTracker = new GpsTracker(this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            lati = String.valueOf(latitude);
            Logitude = String.valueOf(longitude);
            LocationModel locationModel = new LocationModel(latitude, longitude);
            new AppPreferences().storeLatLong(locationModel, this);
            updateLocation(locationModel);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void updateLocation(LocationModel locationModel) {
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", db_id);
        map.put("latitude", locationModel.getLatitude().toString());
        map.put("longitude", locationModel.getLongitude().toString());
        CM_GroceryDeliveryInterface velInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = velInterface.updateLocation(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {

                        } else {
                            Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void statusAPICall() {
        pbr.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", db_id);
        map.put("status", status);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.updateStatus(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject object = responseArray.getJSONObject(0);
                        if (object.getString("status").equalsIgnoreCase("Valid")) {
                            Toast.makeText(MainActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pbr.setVisibility(View.GONE);
            }
        });
    }

    private void getSettingsAPI() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> call = deliveryInterface.getSettings();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject arrayObj = responseArray.getJSONObject(0);
                        if (arrayObj.getString("status").equalsIgnoreCase("Valid")) {
                            String delivery_boy_tip_wallet = arrayObj.getString("delivery_boy_tip_wallet");
                            if (delivery_boy_tip_wallet.equalsIgnoreCase("Yes")) {
                                cardTip.setVisibility(View.VISIBLE);
                            } else {
                                cardTip.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.GONE);
            }
        });
    }

    private void getTipBalance() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getTip(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject object = responseArray.getJSONObject(0);
                        if (object.getString("status").equalsIgnoreCase("Valid")) {
                            String balance = object.getString("balance");
                            tvTip.setText("\u20B9" + " " + balance);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.GONE);
            }
        });
    }

    private void getProfile() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getProfile(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseStr = new String(response.body().bytes());
                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONArray responseArray = responseObj.getJSONArray("response");
                        JSONObject object = responseArray.getJSONObject(0);
                        if (object.getString("status").equalsIgnoreCase("Valid")) {
                            name = object.getString("name");
                            email = object.getString("email");
                            mobile = object.getString("mobile");
                            avaliability = object.getString("avaliability");
                            shift = object.getString("shift");
                            tvName.setText("Hi, " + name);
                            tvEmail.setText("Email : " + email);
                            tvMobile.setText("Mobile : " + mobile);
                            tvShift.setText("Shift : " + shift);
                            if (avaliability.equals("Online")) {
                                statusSwitch.setChecked(true);
                            } else {
                                statusSwitch.setChecked(false);
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDashBoardCount();
        getProfile();
        //getTipBalance();
        walletbalanceApiCall();
        //getSettingsAPI();
    }

    private void getDashBoardCount() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getDashBorad(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            new_orders = jsonObject.getString("new_orders");
                            pickup_orders = jsonObject.getString("pickup_orders");
                            completed_orders = jsonObject.getString("completed_orders");
                            setAdapter();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pbr.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    private void castingViews() {
        img_logout = findViewById(R.id.img_logout);
        tv_wallet_amount = findViewById(R.id.tv_wallet_amount);
        tv_goto_wallet = findViewById(R.id.tv_goto_wallet);
        swipe_layout = findViewById(R.id.swipe_layout);
        pbr = findViewById(R.id.pbr);
        tvName = findViewById(R.id.tvName);
        tvMobile = findViewById(R.id.tvMobile);
        tvEmail = findViewById(R.id.tvEmail);
        tvTip = findViewById(R.id.tvTipBalance);
        cardTip = findViewById(R.id.cardTip);
        cardShift = findViewById(R.id.cardShift);
        statusSwitch = findViewById(R.id.statusSwitch);
        tvReports = findViewById(R.id.tvReports);
        tvShift = findViewById(R.id.tvShift);
        ivSettings = findViewById(R.id.ivSettings);
    }

    private void setAdapter() {
        rv_home_ctgs = findViewById(R.id.rv_home_ctgs);
        rv_home_ctgs.setHasFixedSize(true);
        rv_home_ctgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        categoryList = new ArrayList<>();
        /*categoryList.add(new HomeCategoryMoadel("New Orders", new_orders));*/
        categoryList.add(new HomeCategoryMoadel("Pickup Orders", pickup_orders));
        categoryList.add(new HomeCategoryMoadel("Completed Orders", completed_orders));

        categoriesAdapter = new HomeCategoriesAdapter(this, categoryList);
        categoriesAdapter.setListenerInterface(this);
        rv_home_ctgs.setAdapter(categoriesAdapter);
    }


    @Override
    public void onCategeoryListenerInterface(int position) {
        if (position == 0) {
            Intent intent = new Intent(MainActivity.this, NewOrdersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if (position == 1) {
            startActivity(new Intent(this, PickupOrderActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if (position == 2) {
            Intent intent = new Intent(MainActivity.this, CompletedOrdersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        }
    }

    @Override
    public void onBackPressed() {
        openAlert();
    }

    private void openAlert() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_logout)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void walletbalanceApiCall() {
        pbr.setVisibility(View.VISIBLE);
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getWalletbalance(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    pbr.setVisibility(View.GONE);
                    swipe_layout.setRefreshing(false);
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String balance = jsonObject.getString("balance");
                            tv_wallet_amount.setText("\u20B9" + " " + balance);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        pbr.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                pbr.setVisibility(View.GONE);
            }
        });
    }

}
