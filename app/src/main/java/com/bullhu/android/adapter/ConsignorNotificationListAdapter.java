package com.bullhu.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.activities.consignor.PaymentActivity;
import com.bullhu.android.common.Constants;
import com.bullhu.android.model.FreightModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConsignorNotificationListAdapter extends BaseAdapter {

    Context activity;
    ArrayList<FreightModel> deliveryConsignor = new ArrayList<>();

    public ConsignorNotificationListAdapter(Context activity){
        this.activity = activity;

    }

    public void setList(ArrayList<FreightModel> allConsignor){

        this.deliveryConsignor = allConsignor;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return deliveryConsignor.size();
    }

    @Override
    public Object getItem(int i) {
        return deliveryConsignor.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = new ViewHolder();

        if (view == null){

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_consignor_notification, viewGroup, false);

            viewHolder.txv_name = view.findViewById(R.id.txv_name);
            viewHolder.imv_user = view.findViewById(R.id.imv_user);
            viewHolder.txv_price = view.findViewById(R.id.txv_price);
            viewHolder.btn_accept = (Button)view.findViewById(R.id.btn_accept);
            viewHolder.btn_reject = (Button)view.findViewById(R.id.btn_reject);

            view.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) view.getTag();

        FreightModel delivery = deliveryConsignor.get(i);

        viewHolder.txv_name.setText(delivery.getDriver_name());
        if (delivery.getDriver_photo() != null)
            Glide.with(activity).load(delivery.getDriver_photo()).centerCrop().placeholder(R.drawable.img_user).into(viewHolder.imv_user);

        viewHolder.txv_price.setText(activity.getString(R.string.consignor_accept) + " " + activity.getString(R.string.currency) + delivery.getDelivery_price() + ". " + activity.getString(R.string.consignor_pickup_time));

        viewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, PaymentActivity.class)
                        .putExtra(Constants.PRICE, delivery.getDelivery_price())
                        .putExtra(Constants.DELIVERY_ID, delivery.getId()));
            }
        });

        viewHolder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Declined", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public class ViewHolder{

        CircleImageView imv_user;
        TextView txv_price, txv_name;
        Button btn_accept, btn_reject;
    }
}
