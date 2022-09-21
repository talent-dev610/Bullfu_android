package com.bullhu.android.fragments.driver;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bullhu.android.R;
import com.bullhu.android.activities.driver.DriverMainActivity;
import com.bullhu.android.adapter.ConsignorNotificationListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationDriverFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    DriverMainActivity activity;
    View view;

    ConsignorNotificationListAdapter adapter;
    @BindView(R.id.list_driver_notification)
    ListView list_driver_notification;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    public NotificationDriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification_driver, container, false);
        ButterKnife.bind(this, view);
        loadLayout();
        return view;
    }

    private void loadLayout(){

        refreshLayout.setOnRefreshListener(this);
        adapter = new ConsignorNotificationListAdapter(activity);
        list_driver_notification.setAdapter(adapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DriverMainActivity) context;
    }

    @Override
    public void onRefresh() {

        refreshLayout.setRefreshing(false);
    }
}