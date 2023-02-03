package com.cm_grocery.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.cm_grocery.app.UserSessionManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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

public class LoginActivity extends AppCompatActivity {

    private EditText et_username, et_password;
    private Button btn_login;
    private String userName, password, db_id, token = "";
    SharedPreferences sharedPreferences;
    private TextView tv_login;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        token = sharedPreferences.getString("device_id", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait... Until Login");
        progressDialog.setCancelable(false);
        progressDialog.hide();

        tv_login = findViewById(R.id.tv_login);

        castingViews();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                variablegetText();
                if (et_username.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                } else if (et_password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else if (et_password.getText().length() < 6) {
                    Toast.makeText(LoginActivity.this, "Password should be grater than 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    if (token.isEmpty()) {
                        getToken();
                    } else {
                        btnLoginApiCall(createLoginMapApi());
                        hideSoftKeyboard(LoginActivity.this);
                    }


                }
            }
        });
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("device_id", token);
                        editor.apply();
                        editor.commit();
                        btnLoginApiCall(createLoginMapApi());
                    }
                });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }


    private void castingViews() {
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
    }

    private void variablegetText() {
        userName = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();
    }

    private Map<String, String> createLoginMapApi() {
        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("mobile", userName);
        loginMap.put("password", password);
        loginMap.put("device_id", token);
        return loginMap;
    }

    private void btnLoginApiCall(Map<String, String> loginMapApi) {
        progressDialog.show();
        CM_GroceryDeliveryInterface CMGroceryDeliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> loginBodyCall = CMGroceryDeliveryInterface.getLogin(loginMapApi);
        loginBodyCall.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
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
                            progressDialog.hide();
                            String susMessage = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, susMessage, Toast.LENGTH_SHORT).show();
                            db_id = jsonObject.getString("db_id");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("UserId", db_id);
                            editor.apply();
                            editor.commit();

                            UserSessionManagement.getUserSessionManagement(LoginActivity.this).saveUser(db_id);
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(loginIntent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);

                        } else {
                            progressDialog.hide();
                            if (jsonObject.getString("status").equalsIgnoreCase("Invalid")) {
                                String failMessage = jsonObject.getString("message");
                                Toast.makeText(LoginActivity.this, failMessage, Toast.LENGTH_SHORT).show();
                            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}
