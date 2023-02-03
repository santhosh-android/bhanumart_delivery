package com.cm_grocery.app.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cm_grocery.app.Model.PickDropPickUpListModel;
import com.cm_grocery.app.R;

import java.util.List;

public class PickDropPickUpListAdapter extends RecyclerView.Adapter<PickDropPickUpListAdapter.ViewHolder> {
    private Context mContext;
    private List<PickDropPickUpListModel> pickUpDropModelList;

    public PickDropPickUpListAdapter(Context mContext, List<PickDropPickUpListModel> pickUpDropModelList) {
        this.mContext = mContext;
        this.pickUpDropModelList = pickUpDropModelList;
    }
    public interface PickDropListener{
        void onPickDropListener(int position);
    }
    private PickDropListener pickDropListener;

    public void setPickDropListener(PickDropListener pickDropListener) {
        this.pickDropListener = pickDropListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_p_ick_drop_pick_up_list_adapter, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PickDropPickUpListModel modelList = pickUpDropModelList.get(position);
        holder.order_id_pickList.setText(modelList.getOreder_pd());
        holder.tv_name_pickList.setText(modelList.getCustomer_pd());
        holder.tv_number_pickList.setText(modelList.getMobile_pd());
        holder.tv_address_pick_list.setText(modelList.getPickUp_address_pd());
        holder.tv_address_pickList.setText(modelList.getDelivery_pd());
    }

    @Override
    public int getItemCount() {
        return pickUpDropModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id_pickList, tv_name_pickList, tv_number_pickList, tv_address_pick_list, tv_address_pickList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_id_pickList=itemView.findViewById(R.id.order_id_pickList);
            tv_name_pickList=itemView.findViewById(R.id.tv_name_pickList);
            tv_number_pickList=itemView.findViewById(R.id.tv_number_pickList);
            tv_address_pick_list=itemView.findViewById(R.id.tv_address_pick_list);
            tv_address_pickList=itemView.findViewById(R.id.tv_address_pickList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if (pickDropListener!=null){
                      pickDropListener.onPickDropListener(getAdapterPosition());
                  }
                }
            });

        }
    }
}