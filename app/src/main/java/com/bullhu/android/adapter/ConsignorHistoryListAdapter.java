package com.bullhu.android.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.model.TransactionModel;
import com.bullhu.android.utils.Util;

import java.util.ArrayList;

public class ConsignorHistoryListAdapter extends BaseAdapter {

    ConsignorMainActivity activity;
    ArrayList<TransactionModel> transactions = new ArrayList<>();

    public ConsignorHistoryListAdapter(ConsignorMainActivity activity){

        this.activity = activity;
    }

    public void setData(ArrayList<TransactionModel> trans){

        this.transactions = trans;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int i) {
        return transactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = new ViewHolder();
        if (view == null){

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_consignor_history, viewGroup, false);

            viewHolder.txv_transaction_id = view.findViewById(R.id.txv_transaction_id);
            viewHolder.txv_amount = view.findViewById(R.id.txv_amount);
            viewHolder.txv_date = view.findViewById(R.id.txv_date);

            view.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) view.getTag();

        TransactionModel trans = transactions.get(i);

        viewHolder.txv_transaction_id.setText(trans.getId());
        viewHolder.txv_amount.setText(activity.getString(R.string.currency) + trans.getAmount().substring(0,trans.getAmount().length() - 2));
        viewHolder.txv_date.setText(" " + Util.convertUTCtoLocal(trans.getPaidAt()));

        return view;
    }

    public class ViewHolder{

        TextView txv_transaction_id, txv_amount, txv_date;
    }
}
