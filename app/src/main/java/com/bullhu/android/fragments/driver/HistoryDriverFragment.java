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
import com.bullhu.android.adapter.DriverHistoryListAdapter;
import com.bullhu.android.model.TransactionModel;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryDriverFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    DriverMainActivity activity;
   View view;

   DriverHistoryListAdapter adapter;
   @BindView(R.id.list_driver_history)
   ListView list_driver_history;
   @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    ArrayList<TransactionModel> trans = new ArrayList<>();

    public HistoryDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_history_driver, container, false);
        ButterKnife.bind(this, view);
        loadLayout();
        return view;
    }

    private void loadLayout() {

        refreshLayout.setOnRefreshListener(this);
        adapter = new DriverHistoryListAdapter(activity);
        list_driver_history.setAdapter(adapter);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                getAllTransaction();
            }
        });
    }

    private void getAllTransaction(){

        activity.showHUD();
        ApiManager.allTransaction(new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                activity.hideHUD();
                switch (result){
                    case SUCCESS:

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                trans = (ArrayList<TransactionModel>) resultParam;
                                adapter.setData(trans);
                            }
                        });
                        break;

                    case FAILURE:

                        if (resultParam != null)
                            activity.showToast((String) resultParam);
                        else
                            activity.showToast("Something went wrong.");

                        break;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DriverMainActivity) context;
    }

    @Override
    public void onRefresh() {
        getAllTransaction();
        refreshLayout.setRefreshing(false);
    }
}