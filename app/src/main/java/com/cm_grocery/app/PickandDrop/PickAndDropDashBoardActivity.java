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

import com.cm_grocery.app.Activity.LoginActivity;
import com.cm_grocery.app.Adapters.PickAndDropDashAdapter;
import com.cm_grocery.app.Model.PickAndDropDashModel;
import com.cm_grocery.app.R;
import com.cm_grocery.app.UserSessionManagement;

import java.util.ArrayList;
import java.util.List;

public class PickAndDropDashBoardActivity extends AppCompatActivity implements PickAndDropDashAdapter.OrderTypeListener {
    private RecyclerView rv_pick_ctgs;
    private List<PickAndDropDashModel> pickModelList;
    private ProgressDialog progressDialog;
    private ImageView imglogout_pb;
    private SharedPreferences sharedPreferences;
    private String db_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_and_drop_dash_board);

        sharedPreferences = this.getSharedPreferences("zaroorath", MODE_PRIVATE);
        db_id = sharedPreferences.getString("UserId", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        castingViews();
        imglogout_pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logOut = new Intent(PickAndDropDashBoardActivity.this, LoginActivity.class);
                logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                UserSessionManagement.getUserSessionManagement(PickAndDropDashBoardActivity.this).logOut();
                startActivity(logOut);
            }
        });

        pickRecyclerInitilization();

    }

    private void pickRecyclerInitilization() {
        rv_pick_ctgs.setHasFixedSize(true);
        rv_pick_ctgs.setLayoutManager(new LinearLayoutManager(this));
        pickModelList = new ArrayList<>();
        pickModelList.add(new PickAndDropDashModel("New Orders", ""));
        pickModelList.add(new PickAndDropDashModel("PickUp Orders", ""));
        pickModelList.add(new PickAndDropDashModel("Completd Orders", ""));

        PickAndDropDashAdapter dashAdapter = new PickAndDropDashAdapter(this, pickModelList);
        dashAdapter.setOrderTypeListener(this);
        rv_pick_ctgs.setAdapter(dashAdapter);
    }

    private void castingViews() {
        rv_pick_ctgs = findViewById(R.id.rv_pick_ctgs);
        imglogout_pb = findViewById(R.id.img_logout_pd);
    }

    @Override
    public void OnOrderType(int position) {
        if (position == 0) {
            startActivity(new Intent(this, NewPickAndDropOrders.class));
        } else if (position == 1) {
            startActivity(new Intent(this, PickAndDropPickUpLIstActivity.class));
        } else if (position == 2) {


            startActivity(new Intent(this, PickUpCompletedOrderActivity.class));
        }
    }

    @Override
    public void onNewPickListener(boolean check) {
        if (check) {
            progressDialog.hide();
        }
    }

    @Override
    public void onPickPickListener(boolean check) {
        if (check) {
            progressDialog.hide();
        }
    }

    @Override
    public void onCompletedPickListener(boolean check) {
        if (check) {
            progressDialog.hide();
        }
    }
}