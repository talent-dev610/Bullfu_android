package com.bullhu.android.fragments.consignor;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.adapter.ConsignorHistoryListAdapter;
import com.bullhu.android.model.TransactionModel;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryConsignorFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ConsignorMainActivity activity;
    View view;
    ConsignorHistoryListAdapter adapter;
    @BindView(R.id.list_consignor_history)
    ListView list_consignor_history;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    ArrayList<TransactionModel> trans = new ArrayList<>();

    public HistoryConsignorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_consignor, container, false);
        ButterKnife.bind(this, view);
        loadLayout();
        return view;
    }

    private void loadLayout() {

        refreshLayout.setOnRefreshListener(this);
        adapter = new ConsignorHistoryListAdapter(activity);
        list_consignor_history.setAdapter(adapter);

        getAllTransaction();
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
        activity = (ConsignorMainActivity)context;
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
        getAllTransaction();
    }
}