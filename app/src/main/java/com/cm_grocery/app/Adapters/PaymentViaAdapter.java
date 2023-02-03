package com.cm_grocery.app.Adapters;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cm_grocery.app.Model.PaymentViaModel;
import com.cm_grocery.app.R;

import java.util.List;

public class PaymentViaAdapter extends RecyclerView.Adapter<PaymentViaAdapter.ViewHolder> {
    private Context mContext;
    private List<PaymentViaModel> viaModelList;
    private String selectedItem;
    int currenrPosition=-1;

    public PaymentViaAdapter(Context mContext, List<PaymentViaModel> viaModelList) {
        this.mContext = mContext;
        this.viaModelList = viaModelList;
    }

    public interface PaymentViaListener {
        void onPaymentViaListener(String paymentType);
    }

    private PaymentViaListener paymentViaListener;

    public void setPaymentViaListener(PaymentViaListener paymentViaListener) {
        this.paymentViaListener = paymentViaListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_payment_via_adapter, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentViaModel model = viaModelList.get(position);
        holder.tv_payment_type.setText(model.getPaymentViaString());

        if (currenrPosition==position){
            holder.tv_payment_type.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
            selectedItem = model.getPaymentViaString();
            paymentViaListener.onPaymentViaListener(selectedItem);
        }else {
            holder.tv_payment_type.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }

        holder.tv_payment_type.setOnClickListener(v -> {
            currenrPosition=position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return viaModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_payment_type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_payment_type = itemView.findViewById(R.id.tv_payment_type);
        }
    }
}