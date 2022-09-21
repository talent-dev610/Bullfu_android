package com.bullhu.android.activities.driver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bullhu.android.R;
import com.bullhu.android.activities.BaseActivity;
import com.bullhu.android.common.Commons;
import com.bullhu.android.common.Constants;
import com.bullhu.android.databinding.ActivityDeliveryDetailBinding;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;

public class DeliveryDetailActivity extends BaseActivity implements View.OnClickListener{

    ActivityDeliveryDetailBinding deliveryDetail;
    String size = "", weight = "", time = "", pick_at = "", dropoff_at = "", return_location = "", TDO = "", delivery_id = "", name = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deliveryDetail = DataBindingUtil.setContentView(this, R.layout.activity_delivery_detail);

        name = getIntent().getStringExtra(Constants.NAME);
        delivery_id = getIntent().getStringExtra(Constants.DELIVERY_ID);
        size = getIntent().getStringExtra(Constants.SIZE);
        weight = getIntent().getStringExtra(Constants.WEIGHT);
        time = getIntent().getStringExtra(Constants.TIME);
        pick_at = getIntent().getStringExtra(Constants.PICKUP_AT);
        dropoff_at = getIntent().getStringExtra(Constants.DROPOFF_AT);
        return_location = getIntent().getStringExtra(Constants.EMPTY_RETURN);
        TDO = getIntent().getStringExtra(Constants.TDO);

        loadLayout();
    }

    private void loadLayout(){

        deliveryDetail.imvBack.setOnClickListener(this);
        deliveryDetail.btnAccept.setOnClickListener(this);

        deliveryDetail.txvName.setText(name);
        deliveryDetail.txvSize.setText(size);
        deliveryDetail.txvWeight.setText(weight);
        deliveryDetail.txvPickupTime.setText(time);
        deliveryDetail.txvDropoffAt.setText(dropoff_at);
        deliveryDetail.txvPickupAt.setText(pick_at);
        deliveryDetail.txvEmptyLocation.setText(return_location);
        deliveryDetail.txvTdo.setText(TDO);
    }

    private void processAccept(String price){

        showHUD();
        ApiManager.deliveryAccept(delivery_id, price, Commons.ACCESS_TOKEN, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {

                hideHUD();
                switch (result){

                    case SUCCESS:
                        onBackPressed();
                        break;

                    case FAILURE:
                        if (resultParam != null)
                            showToast((String) resultParam);
                        else showToast("Something went wrong");
                        break;
                }
            }
        });
    }

    public void inputPrice(){

        final Dialog dialog = new Dialog(this);
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
                    processAccept(price);
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
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imv_back:

                onBackPressed();
                break;

            case R.id.btn_accept:
                inputPrice();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}