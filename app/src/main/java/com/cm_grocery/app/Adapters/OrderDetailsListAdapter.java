package com.cm_grocery.app.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cm_grocery.app.Model.OrderDetailsModelList;
import com.cm_grocery.app.R;

import java.util.List;

public class OrderDetailsListAdapter extends RecyclerView.Adapter<OrderDetailsListAdapter.ViewHolder> {
    private Context mContext;
    private List<OrderDetailsModelList> orderModelList;

    public OrderDetailsListAdapter(Context mContext, List<OrderDetailsModelList> orederModelList) {
        this.mContext = mContext;
        this.orderModelList = orederModelList;
    }

    @NonNull
    @Override
    public OrderDetailsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_order_details_list_adapter, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsListAdapter.ViewHolder holder, int position) {
        OrderDetailsModelList modelList = orderModelList.get(position);

        Glide.with(mContext)
                .load(modelList.getItemImg())
                .into(holder.itemImage);

        holder.tv_item_name.setText(modelList.getItemName());
        holder.tv_price_view.setText(modelList.getItemPrice());
        holder.tv_qty.setText(modelList.getItemQty());
       /* if (modelList.getVendorDeailsModel() == null) {
            holder.vendorLayout.setVisibility(View.GONE);
        } else {
            holder.vendorLayout.setVisibility(View.VISIBLE);
            holder.shop_name.setText(modelList.getVendorDeailsModel().getShopName());
            holder.person_name.setText(modelList.getVendorDeailsModel().getContactName());
            holder.mobile.setText(modelList.getVendorDeailsModel().getMobile());
            holder.mobile_alternative.setText(modelList.getVendorDeailsModel().getAlternateMobile());
            holder.address_vendor.setText(modelList.getVendorDeailsModel().getAddress());
        }*/
    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView tv_item_name, tv_price_view, tv_qty;
        private LinearLayout vendorLayout;
        private TextView shop_name, person_name, mobile, mobile_alternative, address_vendor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_price_view = itemView.findViewById(R.id.tv_price_view);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            vendorLayout = itemView.findViewById(R.id.vendor_layout);

            shop_name = itemView.findViewById(R.id.shop_name);
            person_name = itemView.findViewById(R.id.person_name);
            mobile = itemView.findViewById(R.id.mobile);
            mobile_alternative = itemView.findViewById(R.id.mobile_alternative);
            address_vendor = itemView.findViewById(R.id.address_vendor);

        }
    }
}
