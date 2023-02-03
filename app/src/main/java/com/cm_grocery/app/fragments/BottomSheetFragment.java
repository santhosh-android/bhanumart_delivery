package com.cm_grocery.app.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cm_grocery.app.Activity.MainActivity;
import com.cm_grocery.app.Adapters.PaymentViaAdapter;
import com.cm_grocery.app.Model.PaymentViaModel;
import com.cm_grocery.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetFragment extends BottomSheetDialogFragment implements PaymentViaAdapter.PaymentViaListener {
    private String orderId, db_id, totalAmount;
    private SharedPreferences sharedPreferences;
    private RecyclerView rv_payment_list;
    private PaymentViaAdapter paymentAdapter;
    private List<PaymentViaModel> paymentModel;
    private Button btn_delivery_confirm;
    private String selectedpayment;

    //private PaymentBottomSheetListener bottomSheetListener;
    private ProgressDialog progressDialog;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public static BottomSheetFragment getInstance() {
        return new BottomSheetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        sharedPreferences = getActivity().getSharedPreferences("zaroorath", Context.MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        Bundle arguments = getArguments();
        orderId = arguments.getString("order_id");
        totalAmount = arguments.getString("total");

        rv_payment_list = view.findViewById(R.id.rv_payment_list);
        rv_payment_list.setHasFixedSize(true);
        rv_payment_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        btn_delivery_confirm = view.findViewById(R.id.btn_delivery_confirm);
        setPaymentRecyclerView();

        btn_delivery_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeliveryApiCall();
            }
        });
        return view;
    }

    private void setPaymentRecyclerView() {
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
                            paymentModel = new ArrayList<>();
                            JSONArray optionsArray = jsonObject.getJSONArray("options");
                            for (int i = 0; i < optionsArray.length(); i++) {
                                JSONObject optionsObject = optionsArray.getJSONObject(i);
                                paymentModel.add(new PaymentViaModel(
                                        optionsObject.getString("payment_via")
                                ));
                            }
                            paymentAdapter = new PaymentViaAdapter(getActivity(), paymentModel);
                            paymentAdapter.setPaymentViaListener(BottomSheetFragment.this);
                            rv_payment_list.setAdapter(paymentAdapter);
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
        selectedpayment = paymentType;
    }

    private void confirmDeliveryApiCall() {
        progressDialog.show();
        Map<String, String> deliveryMap = new HashMap<>();
        deliveryMap.put("db_id", db_id);
        deliveryMap.put("order_id", orderId);
        deliveryMap.put("amount", totalAmount);
        if (selectedpayment!=null && !selectedpayment.isEmpty()){
            deliveryMap.put("payment_via", selectedpayment);
        }else {
            Toast.makeText(getActivity(), "Please Select Payment Type...", Toast.LENGTH_SHORT).show();
        }

        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.orderDeliveryApiCall(deliveryMap);
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


   /* public interface PaymentBottomSheetListener {
        void onEventSelected(String eventType);
    }

    public void setBottomSheetListener(PaymentBottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }*/

}
