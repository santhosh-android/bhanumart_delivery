package com.cm_grocery.app.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cm_grocery.app.Model.NewPickAndDropModel;
import com.cm_grocery.app.R;

import java.util.List;

public class NewPickAndDropAdapter extends RecyclerView.Adapter<NewPickAndDropAdapter.ViewHolder> {
    private Context mContext;
    private List<NewPickAndDropModel> newPickDropModel;

    public NewPickAndDropAdapter(Context mContext, List<NewPickAndDropModel> newPickDropModel) {
        this.mContext = mContext;
        this.newPickDropModel = newPickDropModel;
    }

    public interface NewPickUpOrderListener {
        void onNewOrderViewlistener(int position);
    }

    private NewPickUpOrderListener newPickUpOrderListener;

    public void setNewPickUpOrderListener(NewPickUpOrderListener newPickUpOrderListener) {
        this.newPickUpOrderListener = newPickUpOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_new_pick_and_drop_adapter, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewPickAndDropModel model = newPickDropModel.get(position);
        holder.order_id.setText(model.getOrderPickId());
        holder.tv_name.setText(model.getCustomerPickName());
        holder.tv_number.setText(model.getCustomerPickMobile());
        holder.tv_address_pick.setText(model.getPickAddress());
        holder.tv_address.setText(model.getDeliveryAddress());
    }

    @Override
    public int getItemCount() {
        return newPickDropModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id, tv_name, tv_number, tv_address_pick, tv_address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_address_pick = itemView.findViewById(R.id.tv_address_pick);
            tv_address = itemView.findViewById(R.id.tv_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (newPickUpOrderListener!=null){
                        newPickUpOrderListener.onNewOrderViewlistener(getAdapterPosition());
                    }
                }
            });
        }
    }
}