package com.cm_grocery.app.PickandDrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cm_grocery.app.Adapters.PickDropPickUpListAdapter;
import com.cm_grocery.app.Model.PickDropPickUpListModel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickAndDropPickUpLIstActivity extends AppCompatActivity implements PickDropPickUpListAdapter.PickDropListener {
    private RecyclerView rv_pick_list;
    private List<PickDropPickUpListModel>  pickdropModelList;
    private PickDropPickUpListAdapter pickListAdapter;
    private SharedPreferences sharedPreferences;
    private String db_id, orderId;
    private TextView tv_no_pickList;
    private ProgressDialog progressDialog;
    private ImageView img_back_pick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_and_drop_pick_up_l_ist);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait getting Orders");
        progressDialog.show();
        img_back_pick=findViewById(R.id.img_back_pick);
        img_back_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_no_pickList=findViewById(R.id.tv_no_completed_pick);
        rv_pick_list=findViewById(R.id.rv_pick_list);
        rv_pick_list.setHasFixedSize(true);
        rv_pick_list.setLayoutManager(new LinearLayoutManager(this));

        pickUpandDropPickListApi();
    }

    private void pickUpandDropPickListApi() {
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getPickPickUp(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body()!=null){
                    progressDialog.hide();
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")){
                            String orders = jsonObject.getString("orders");
                            pickdropModelList=new ArrayList<>();
                            if (!orders.equalsIgnoreCase("null")){
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i <jsonArray.length() ; i++) {
                                    JSONObject orderObject = jsonArray.getJSONObject(i);
                                    orderId=orderObject.getString("order_id");
                                    String customerName = orderObject.getString("customer_name");
                                    String mobile = orderObject.getString("mobile");
                                    String pickAddress = orderObject.getString("pickup_address");
                                    String address = orderObject.getString("delivery_address");
                                    pickdropModelList.add(new PickDropPickUpListModel(orderId,customerName,mobile,pickAddress,address));
                                }
                                pickListAdapter=new PickDropPickUpListAdapter(PickAndDropPickUpLIstActivity.this,pickdropModelList);
                                pickListAdapter.setPickDropListener(PickAndDropPickUpLIstActivity.this);
                                rv_pick_list.setAdapter(pickListAdapter);
                            }else {
                                Toast.makeText(PickAndDropPickUpLIstActivity.this, "No Orders Found", Toast.LENGTH_SHORT).show();
                                tv_no_pickList.setVisibility(View.VISIBLE);
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
    public void onPickDropListener(int position) {
        Intent intent = new Intent(PickAndDropPickUpLIstActivity.this, PickUpOrderPickUpDetailsActivity.class);
        intent.putExtra("order_id", pickdropModelList.get(position).getOreder_pd());
        startActivity(intent);
    }
}