package com.bullhu.android.fragments.driver;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bullhu.android.R;
import com.bullhu.android.activities.driver.DriverMainActivity;
import com.bullhu.android.adapter.DriverHomeListAdapter;
import com.bullhu.android.common.Commons;
import com.bullhu.android.model.FreightModel;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeDriverFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

     DriverMainActivity activity;

    View view;
    DriverHomeListAdapter adapter;
    @BindView(R.id.list_driver_home)
    ListView list_driver_home;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    ArrayList<FreightModel> freightModels;

    public HomeDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_driver, container, false);
        ButterKnife.bind(this, view);
        loadLayout();
        return view;
    }

    private void loadLayout(){

        refreshLayout.setOnRefreshListener(this);
        adapter = new DriverHomeListAdapter(activity, this);
        list_driver_home.setAdapter(adapter);

    }

    private void getAllDelivery(){

        activity.showHUD();
        ApiManager.getDeliveryAllForDrivers(Commons.ACCESS_TOKEN, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                activity.hideHUD();

                switch (result){

                    case SUCCESS:
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                freightModels = (ArrayList<FreightModel>) resultParam;
                                adapter.setList(freightModels);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        break;

                    case FAILURE:
                        if (resultParam != null){
                            activity.showToast((String) resultParam);
                        }
                        else activity.showToast("Something went wrong");

                        break;
                }
            }
        });
    }

    private void processAccept(String delivery_id, String price){
        activity.showHUD();
        ApiManager.deliveryAccept(delivery_id, price, Commons.ACCESS_TOKEN, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {

                activity.hideHUD();
                switch (result){

                    case SUCCESS:
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loadLayout();
                            }
                        });
                        break;

                    case FAILURE:
                        if (resultParam != null)
                            activity.showToast((String) resultParam);
                        else activity.showToast("Something went wrong");
                        break;
                }
            }
        });
    }

    public void inputPrice(String delivery_id){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_input_price);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TextView txv_cancel = (TextView)dialog.findViewById(R.id.txv_cancel);
        EditText edt_price = (EditText) dialog.findViewById(R.id.edt_price);
        Button btn_send = (Button) dialog.findViewById(R.id.btn_send);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String price = edt_price.getText().toString();
                if (!price.isEmpty())
                    processAccept(delivery_id, price);
                dialog.dismiss();
            }
        });

        txv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DriverMainActivity) context;
    }

    @Override
    public void onRefresh() {

        getAllDelivery();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllDelivery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.hideHUD();
    }
}