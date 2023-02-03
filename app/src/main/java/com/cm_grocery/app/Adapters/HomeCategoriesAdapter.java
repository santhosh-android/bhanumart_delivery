package com.cm_grocery.app.Adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cm_grocery.app.Model.HomeCategoryMoadel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInstance;
import com.cm_grocery.app.Services.CM_GroceryDeliveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder> {

    private Context mContext;
    private List<HomeCategoryMoadel> categoryMoadelList;
    private SharedPreferences sharedPreferences;
    private String db_id;

    public HomeCategoriesAdapter(Context mContext, List<HomeCategoryMoadel> categoryMoadelList) {
        this.mContext = mContext;
        this.categoryMoadelList = categoryMoadelList;
    }

    public interface CategeoryListenerInterface {
        void onCategeoryListenerInterface(int position);

    }

    public CategeoryListenerInterface listenerInterface;

    public void setListenerInterface(CategeoryListenerInterface listenerInterface) {
        this.listenerInterface = listenerInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_home_categories_adapter, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeCategoryMoadel modelList = categoryMoadelList.get(position);
        holder.tv_category.setText(modelList.getTittleName());
        holder.tv_count.setText(modelList.getCount());
    }

    @Override
    public int getItemCount() {
        return categoryMoadelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_category, tv_count;
        private CardView cv_home;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_count = itemView.findViewById(R.id.tv_count);
            cv_home = itemView.findViewById(R.id.cv_home);

            cv_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerInterface != null) {
                        listenerInterface.onCategeoryListenerInterface(getAdapterPosition());
                    }
                }
            });

        }
    }
}
