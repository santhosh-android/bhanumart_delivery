package com.cm_grocery.app.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cm_grocery.app.Model.PickUpCompletedModel;
import com.cm_grocery.app.R;

import java.util.List;

public class PickUpCompletedOrderAdapter extends RecyclerView.Adapter<PickUpCompletedOrderAdapter.ViewHolder> {
    private Context mContext;
    private List<PickUpCompletedModel> completedModel;

    public PickUpCompletedOrderAdapter(Context mContext, List<PickUpCompletedModel> completedModel) {
        this.mContext = mContext;
        this.completedModel = completedModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_pick_up_completed_order_adapter,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PickUpCompletedModel model = completedModel.get(position);
        holder.order_id.setText(model.getOrderId());
        holder.tv_name.setText(model.getCusName());
        holder.tv_number.setText(model.getMobileCus());
        holder.tv_address_pickup.setText(model.getPickAddress());
        holder.tv_address.setText(model.getDelAddress());
    }

    @Override
    public int getItemCount() {
        return completedModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id,tv_name,tv_number,tv_address_pickup,tv_address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id=itemView.findViewById(R.id.order_id);
            tv_name=itemView.findViewById(R.id.tv_name);
            tv_number=itemView.findViewById(R.id.tv_number);
            tv_address_pickup=itemView.findViewById(R.id.tv_address_pickup);
            tv_address=itemView.findViewById(R.id.tv_address);
        }
    }
}