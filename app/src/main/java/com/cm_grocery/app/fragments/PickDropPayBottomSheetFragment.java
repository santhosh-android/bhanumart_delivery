package com.cm_grocery.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cm_grocery.app.Activity.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.cm_grocery.app.Activity.ChooseDeliveryActivity;
import com.cm_grocery.app.Adapters.PaymentViaAdapter;
import com.cm_grocery.app.Model.PaymentViaModel;
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

public class PickDropPayBottomSheetFragment extends BottomSheetDialogFragment implements PaymentViaAdapter.PaymentViaListener {

    private RecyclerView rv_payment_list_pick;
    private PaymentViaAdapter adapterPayment;
    private List<PaymentViaModel> paymentModelList;
    private EditText et_amount_collected;
    private String selectedPay, orderId, db_id, amount;
    private SharedPreferences sharedPreferences;
    private Button btn_delivery_confirm_pick;
    private ProgressDialog progressDialog;

    public PickDropPayBottomSheetFragment() {
        // Required empty public constructor
    }

    public static PickDropPayBottomSheetFragment getInstance() {
        return new PickDropPayBottomSheetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_pick_drop_pay_bottom_sheet, container, false);

        Bundle arguments = getArguments();
        orderId = arguments.getString("order_id");

        sharedPreferences = getActivity().getSharedPreferences("zaroorath", Context.MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        et_amount_collected = mView.findViewById(R.id.et_amount_collected);

        btn_delivery_confirm_pick = mView.findViewById(R.id.btn_delivery_confirm_pick);
        rv_payment_list_pick = mView.findViewById(R.id.rv_payment_list_pick);
        rv_payment_list_pick.setHasFixedSize(true);
        rv_payment_list_pick.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewsetUp();

        btn_delivery_confirm_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = et_amount_collected.getText().toString().trim();
                if (!validateAmount()) {
                    return;
                } else {
                    confirmPickUpDelivery();
                }
            }
        });


        return mView;
    }


    private void recyclerViewsetUp() {
        progressDialog.show();
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getpaymentVia();
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
                            paymentModelList = new ArrayList<>();
                            JSONArray optionsArray = jsonObject.getJSONArray("options");
                            for (int i = 0; i < optionsArray.length(); i++) {
                                JSONObject optionsObject = optionsArray.getJSONObject(i);
                                paymentModelList.add(new PaymentViaModel(
                                        optionsObject.getString("payment_via")
                                ));
                            }
                            adapterPayment = new PaymentViaAdapter(getActivity(), paymentModelList);
                            adapterPayment.setPaymentViaListener(PickDropPayBottomSheetFragment.this);
                            rv_payment_list_pick.setAdapter(adapterPayment);
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
    public void onPaymentViaListener(String paymentType) {
        selectedPay = paymentType;
    }

    private boolean validateAmount() {
        if (amount.isEmpty()) {
            et_amount_collected.setError("Enter Amount");
            return false;
        } else {
            et_amount_collected.setError(null);
            return true;
        }
    }

    private void confirmPickUpDelivery() {
        progressDialog.show();
        Map<String, String> deliveryMap = new HashMap<>();
        deliveryMap.put("db_id", db_id);
        deliveryMap.put("order_id", orderId);
        deliveryMap.put("amount", amount);
        if (selectedPay != null && !selectedPay.isEmpty()) {
            deliveryMap.put("payment_via", selectedPay);
        } else {
            Toast.makeText(getActivity(), "Please Select Payment Type...", Toast.LENGTH_SHORT).show();
        }

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
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
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
}