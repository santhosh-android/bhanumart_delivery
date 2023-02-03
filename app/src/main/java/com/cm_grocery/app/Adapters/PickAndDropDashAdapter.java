package com.cm_grocery.app.Adapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cm_grocery.app.Model.PickAndDropDashModel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickAndDropDashAdapter extends RecyclerView.Adapter<PickAndDropDashAdapter.ViewHolder> {
    private Context mContext;
    private List<PickAndDropDashModel> pickDashModelList;
    private SharedPreferences sharedPreferences;
    private String db_id;

    public PickAndDropDashAdapter(Context mContext, List<PickAndDropDashModel> pickDashModelList) {
        this.mContext = mContext;
        this.pickDashModelList = pickDashModelList;
    }

    public interface OrderTypeListener {
        void OnOrderType(int position);

        void onNewPickListener(boolean check);

        void onPickPickListener(boolean check);

        void onCompletedPickListener(boolean check);
    }

    private OrderTypeListener orderTypeListener;

    public void setOrderTypeListener(OrderTypeListener orderTypeListener) {
        this.orderTypeListener = orderTypeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_pick_and_drop_dash_adapter, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PickAndDropDashModel dropDashModel = pickDashModelList.get(position);
        holder.txt_category.setText(dropDashModel.getOrderType());
        newOrdersPickApiCall(holder.txt_count, position);
        pickOrdersApiCall(holder.txt_count, position);
        completedApiCall(holder.txt_count, position);
    }


    @Override
    public int getItemCount() {
        return pickDashModelList.size();
    }

    private void newOrdersPickApiCall(TextView count, int position) {
        sharedPreferences = mContext.getSharedPreferences("zaroorath", Context.MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> bodyCall = deliveryInterface.getPickNewOrders(db_id);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responeString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responeString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                if (orderTypeListener != null) {
                                    orderTypeListener.onNewPickListener(true);
                                }
                                if (position == 0) {
                                    count.setText(String.valueOf(jsonArray.length()));
                                }
                            } else {
                                if (orderTypeListener != null) {
                                    orderTypeListener.onNewPickListener(true);
                                }
                                if (position == 0) {
                                    count.setText("0");
                                }
                            }

                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        if (orderTypeListener != null) {
                            orderTypeListener.onNewPickListener(true);
                        }
                        if (position == 0) {
                            count.setText("0");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (orderTypeListener != null) {
                    orderTypeListener.onNewPickListener(true);
                }
                if (position == 0) {
                    count.setText("0");
                }

            }
        });
    }

    private void pickOrdersApiCall(TextView countPick, int position) {
        CM_GroceryDeliveryInterface deliveryInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = deliveryInterface.getPickPickUp(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                if (orderTypeListener != null) {
                                    orderTypeListener.onPickPickListener(true);
                                }
                                if (position == 1) {
                                    countPick.setText(String.valueOf(jsonArray.length()));
                                }
                            } else {
                                if (orderTypeListener != null) {
                                    orderTypeListener.onPickPickListener(true);
                                }
                                if (position == 1) {
                                    countPick.setText("0");
                                }

                            }

                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        if (position == 1) {
                            if (orderTypeListener != null) {
                                orderTypeListener.onPickPickListener(true);
                            }
                            countPick.setText("0");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (position == 1) {
                    if (orderTypeListener != null) {
                        orderTypeListener.onPickPickListener(true);
                    }
                    countPick.setText("0");
                }
            }
        });
    }

    private void completedApiCall(TextView completeCount, int position) {
        CM_GroceryDeliveryInterface zaroorathInterface = CM_GroceryDeliveryInstance.getRetrofit().create(CM_GroceryDeliveryInterface.class);
        Call<ResponseBody> responseBodyCall = zaroorathInterface.getCompletedPickup(db_id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseString = new String(response.body().bytes());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray responseArray = responseObject.getJSONArray("response");
                        JSONObject jsonObject = responseArray.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("Valid")) {
                            String orders = jsonObject.getString("orders");
                            if (!orders.equalsIgnoreCase("null")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                if (orderTypeListener != null) {
                                    orderTypeListener.onCompletedPickListener(true);
                                }
                                if (position == 2) {
                                    completeCount.setText(String.valueOf(jsonArray.length()));
                                }
                            } else {
                                if (orderTypeListener != null) {
                                    orderTypeListener.onCompletedPickListener(true);
                                }
                                if (position == 2) {
                                    completeCount.setText("0");
                                }

                            }

                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        if (position == 2) {
                            if (orderTypeListener != null) {
                                orderTypeListener.onPickPickListener(true);
                            }
                            completeCount.setText("0");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (position == 2) {
                    if (orderTypeListener != null) {
                        orderTypeListener.onPickPickListener(true);
                    }
                    completeCount.setText("0");
                }
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_category, txt_count;
        private CardView card_home;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_count = itemView.findViewById(R.id.txt_count);
            card_home = itemView.findViewById(R.id.card_home);

            card_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderTypeListener != null) {
                        orderTypeListener.OnOrderType(getAdapterPosition());
                    }
                }
            });
        }
    }
}