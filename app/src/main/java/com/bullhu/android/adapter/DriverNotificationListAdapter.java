package com.bullhu.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bullhu.android.model.FreightModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverNotificationListAdapter extends BaseAdapter {

    Context ac;
    ArrayList<FreightModel> deliveryDrivers = new ArrayList<>();

    public void setData(ArrayList<FreightModel> freightModels){

        this.deliveryDrivers = freightModels;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return deliveryDrivers.size();
    }

    @Override
    public Object getItem(int i) {
        return deliveryDrivers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        return view;
    }

    public class ViewHolder{

        CircleImageView imv_user;
        TextView txv_price, txv_name;
        Button btn_accept, btn_reject;
    }
}
