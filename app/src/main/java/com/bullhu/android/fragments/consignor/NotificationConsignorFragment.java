package com.bullhu.android.fragments.consignor;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.activities.consignor.PaymentActivity;
import com.bullhu.android.adapter.ConsignorNotificationListAdapter;
import com.bullhu.android.common.Commons;
import com.bullhu.android.model.FreightModel;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationConsignorFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ConsignorMainActivity activity;
    View view;
    ArrayList<FreightModel> allForConsignor = new ArrayList<>();

    ConsignorNotificationListAdapter adapter;
    @BindView(R.id.list_consignor_notification)
    ListView list_notification;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    public NotificationConsignorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification_consignor, container, false);
        ButterKnife.bind(this, view);
        loadLayout();
        return view;


    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimator(android.R.anim.fade_in, true, android.R.anim.fade_out);
    }
    private void loadLayout() {

        refreshLayout.setOnRefreshListener(this);
        adapter = new ConsignorNotificationListAdapter(activity);
        list_notification.setAdapter(adapter);

        //activity.startActivity(new Intent(activity, PaymentActivity.class));
    }

    private void getAllDeliveryForConsignor(){

        activity.showHUD();

        ApiManager.getDeliveryAllForConsignor(Commons.ACCESS_TOKEN, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                activity.hideHUD();

                switch (result){

                    case SUCCESS:

                        allForConsignor = (ArrayList<FreightModel>) resultParam;

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setList(allForConsignor);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ConsignorMainActivity)context;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllDeliveryForConsignor();
    }

    @Override
    public void onRefresh() {
        getAllDeliveryForConsignor();
        refreshLayout.setRefreshing(false);
    }
}