package com.cm_grocery.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cm_grocery.app.Model.ReportsModel;
import com.cm_grocery.app.R;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    private Context mContext;
    private List<ReportsModel> modelList;

    public ReportsAdapter(Context mContext, List<ReportsModel> modelList) {
        this.mContext = mContext;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.reports_list, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportsModel model = modelList.get(position);
        holder.tvName.setText(model.getName());
        if (model.getName().equalsIgnoreCase("total_amount")) {
            holder.tvValue.setText("\u20B9 " + model.getValue());
        } else if (model.getName().equalsIgnoreCase("total_kilometer")) {
            holder.tvValue.setText(model.getValue() + " KM");
        } else {
            holder.tvValue.setText(model.getValue());
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}
