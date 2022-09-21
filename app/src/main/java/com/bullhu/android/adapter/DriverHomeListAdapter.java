package com.bullhu.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bullhu.android.R;
import com.bullhu.android.activities.driver.DeliveryDetailActivity;
import com.bullhu.android.activities.driver.DriverMainActivity;
import com.bullhu.android.common.Constants;
import com.bullhu.android.fragments.driver.HomeDriverFragment;
import com.bullhu.android.model.FreightModel;
import com.bullhu.android.utils.Util;

import java.util.ArrayList;

public class DriverHomeListAdapter extends BaseAdapter {

    DriverMainActivity activity;
    ArrayList<FreightModel> freightModels = new ArrayList<>();
    HomeDriverFragment fragment;

    public DriverHomeListAdapter(DriverMainActivity activity, HomeDriverFragment frag){
        this.activity = activity;
        this.fragment = frag;
    }

    public void setList(ArrayList<FreightModel> models){

        this.freightModels = models;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return freightModels.size();
        //return 10;
    }

    @Override
    public Object getItem(int i) {
        return freightModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = new ViewHolder();
        if (view == null){

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_driver_home, viewGroup, false);

            holder.txv_name = view.findViewById(R.id.txv_name);
            holder.txv_size = view.findViewById(R.id.txv_size);
            holder.txv_weight = view.findViewById(R.id.txv_weight);
            holder.txv_time = view.findViewById(R.id.txv_pickup_time);
            holder.txv_pickup_at = view.findViewById(R.id.txv_pickup_at);
            holder.txv_dropoff_at = view.findViewById(R.id.txv_dropoff_at);
            holder.txv_return_location = view.findViewById(R.id.txv_return_location);
            holder.txv_order_ready = view.findViewById(R.id.txv_order_ready);
            holder.txv_accept = (Button) view.findViewById(R.id.txv_accept);

            view.setTag(holder);

        }
        else holder = (ViewHolder) view.getTag();

        FreightModel fre = freightModels.get(i);

        holder.txv_name.setText(fre.getConsignor_name());
        holder.txv_size.setText(" " + fre.getSize());
        holder.txv_weight.setText(" " + fre.getWeight());
        holder.txv_time.setText( " " + Util.getDateTime(fre.getPickup_time()));
        holder.txv_pickup_at.setText(" " + Util.getAddress(activity, fre.getPickup_location()));
        holder.txv_dropoff_at.setText(" " + Util.getAddress(activity, fre.getDropoff_location()));
        holder.txv_return_location.setText(" " + Util.getAddress(activity, fre.getReturn_location()));
        holder.txv_order_ready.setText(fre.isTdo_ready() ? " true" : " Yes");

        holder.txv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment.inputPrice(fre.getId());
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.startActivity(new Intent(activity, DeliveryDetailActivity.class)
                        .putExtra(Constants.SIZE, fre.getSize())
                        .putExtra(Constants.WEIGHT, fre.getWeight())
                        .putExtra(Constants.TIME, Util.getDateTime(fre.getPickup_time()))
                        .putExtra(Constants.PICKUP_AT, Util.getAddress(activity, fre.getPickup_location()))
                        .putExtra(Constants.DROPOFF_AT, Util.getAddress(activity, fre.getDropoff_location()))
                        .putExtra(Constants.EMPTY_RETURN, Util.getAddress(activity, fre.getReturn_location()))
                        .putExtra(Constants.TDO, fre.isTdo_ready() ? " true" : " Yes")
                        .putExtra(Constants.DELIVERY_ID, fre.getId())
                        .putExtra(Constants.NAME, fre.getConsignor_name())

                );

            }
        });
        return view;
    }

    public class ViewHolder{

        TextView txv_name, txv_size, txv_weight, txv_time, txv_pickup_at,
                txv_dropoff_at, txv_return_location, txv_order_ready;

        Button txv_accept;
    }


}
