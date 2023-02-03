package com.cm_grocery.app.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.Activity.MainActivity;
import com.cm_grocery.app.Activity.PickUpOrderDetailsActivity;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class CodBottomSheetFragment extends BottomSheetDialogFragment {

    private TextView et_comments, tv_collect;
    private Button btn_submit;
    private EditText payment_via, comments, etAmount;
    private String db_id, orderid, total_amount;
    private SharedPreferences sharedPreferences;
    private double enteredAmount, total;


    private CodBottomSheetListener codBottomSheetListener;
    private ProgressDialog progressDialog;


    public CodBottomSheetFragment() {
        // Required empty public constructor
    }

    public static CodBottomSheetFragment getInstance() {
        return new CodBottomSheetFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cod_bottom_sheet, container, false);

        sharedPreferences = getActivity().getSharedPreferences("zaroorath", Context.MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        Bundle arguments = getArguments();
        orderid = arguments.getString("order_id");
        total_amount = arguments.getString("total");

        castingViews(view);

        tv_collect.setText("Please Collect Cash :" + total_amount);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etAmount.getText().toString().equals(total_amount)) {
                    Toast.makeText(requireActivity(), "Please enter valid amount", Toast.LENGTH_SHORT).show();
                } else if (payment_via.getText().toString().isEmpty()) {
                    Toast.makeText(requireActivity(), "Please enter payment via", Toast.LENGTH_SHORT).show();
                } else {
                    submitDeliveryApiCall();
                    dismiss();
                }
            }
        });
        return view;
    }

    private void castingViews(View view) {
        payment_via = view.findViewById(R.id.et_payment_via);
        comments = view.findViewById(R.id.et_comments);
        et_comments = view.findViewById(R.id.et_comments);
        btn_submit = view.findViewById(R.id.btn_submit);
        tv_collect = view.findViewById(R.id.tv_collect);
        etAmount = view.findViewById(R.id.etAmount);
    }

    private Map<String, String> submitDelApiMap() {
        Map<String, String> submitMap = new HashMap<>();
        submitMap.put("order_id", orderid);
        submitMap.put("db_id", db_id);
        submitMap.put("amount", etAmount.getText().toString());
        submitMap.put("payment_via", payment_via.getText().toString());
        submitMap.put("payment_remarks", et_comments.getText().toString());
        return submitMap;
    }

    private void submitDeliveryApiCall() {
        progressDialog.show();
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.orderDeliveryApiCall(submitDelApiMap());
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
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public interface CodBottomSheetListener {
        void onCodListener(String eventType);
    }

    public void setCodBottomSheetListener(CodBottomSheetListener codBottomSheetListener) {
        this.codBottomSheetListener = codBottomSheetListener;
    }
}
