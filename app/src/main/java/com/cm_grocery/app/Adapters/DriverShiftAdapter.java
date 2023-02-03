package com.cm_grocery.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cm_grocery.app.Model.DriverShiftModel;
import com.cm_grocery.app.R;

import java.util.List;

public class DriverShiftAdapter extends RecyclerView.Adapter<DriverShiftAdapter.ViewHolder> {
    private Context mContext;
    private List<DriverShiftModel> modelList;
    private String currentShift = "";
    private int checkedPosition = -1;

    public DriverShiftAdapter(Context mContext, List<DriverShiftModel> modelList, String currentShift) {
        this.mContext = mContext;
        this.modelList = modelList;
        this.currentShift = currentShift;
    }

    public interface CheckBoxListener {
        void onChecked(int pos);
    }

    public CheckBoxListener checkBoxListener;

    public void setCheckBoxListener(CheckBoxListener checkBoxListener) {
        this.checkBoxListener = checkBoxListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.shift_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DriverShiftModel model = modelList.get(position);
        holder.tvShift.setText(model.getShift());

        holder.itemView.setOnClickListener(v -> {
            if (checkBoxListener != null) {
                checkBoxListener.onChecked(position);
            }
        });

        holder.chkBox.setOnClickListener(v -> {
            if (checkBoxListener != null) {
                checkBoxListener.onChecked(position);
            }
        });

        if (checkedPosition == position) {
            holder.chkBox.setChecked(true);
        } else {
            holder.chkBox.setChecked(false);
        }

        for (int i = 0; i < modelList.size(); i++) {
            if (currentShift.equals(model.getShift_id())) {
                holder.chkBox.setChecked(true);
            } else {
                holder.chkBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvShift;
        private CheckBox chkBox;
        private CardView cardItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShift = itemView.findViewById(R.id.tvShift);
            chkBox = itemView.findViewById(R.id.chkBox);
            cardItem = itemView.findViewById(R.id.cardItem);
        }
    }
}
