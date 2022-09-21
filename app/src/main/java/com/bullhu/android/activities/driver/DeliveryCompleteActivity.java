package com.bullhu.android.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.bullhu.android.R;
import com.bullhu.android.activities.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryCompleteActivity extends BaseActivity {

    @BindView(R.id.txv_pickup_location)
    TextView txv_pickup_location;
    @BindView(R.id.txv_dropoff_location)
    TextView txv_dropoff_location;
    @BindView(R.id.txv_empty_return_location)
    TextView txv_empty_return_location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_complete);
        ButterKnife.bind(this);
        loadLayout();
    }

    private void loadLayout(){


    }

    @OnClick(R.id.btn_delivery_complete) void completeDelivery(){

        finish();
    }
}