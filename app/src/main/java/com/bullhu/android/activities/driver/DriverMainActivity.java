package com.bullhu.android.activities.driver;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.bullhu.android.R;
import com.bullhu.android.activities.BaseActivity;
import com.bullhu.android.fragments.SettingsFragment;
import com.bullhu.android.fragments.consignor.HomeConsignorFragment;
import com.bullhu.android.fragments.driver.HistoryDriverFragment;
import com.bullhu.android.fragments.driver.HomeDriverFragment;
import com.bullhu.android.fragments.driver.NotificationDriverFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriverMainActivity extends BaseActivity {

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        ButterKnife.bind(this);
        loadLayout();
    }

    private void loadLayout() {

        bottomBar.selectTabAtPosition(0);

        //Bottom Bar Selection with index
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                switch (tabId){

                    case R.id.tab_home:
                        gotoHomeFragment();

                        break;

                    case R.id.tab_history:
                        gotoHistoryFragment();

                        break;

                    case R.id.tab_notification:
                        gotoNotificationFragment();
                        break;

                    case R.id.tab_settings:
                        gotoSettingsFragment();
                        break;

                    default:
                        gotoHomeFragment();
                }
            }
        });

        uploadToken();
    }


    public void gotoHomeFragment(){

        HomeDriverFragment fragment = new HomeDriverFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frm_container, fragment).commit();
    }

    public void gotoHistoryFragment(){

        HistoryDriverFragment fragment = new HistoryDriverFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frm_container, fragment).commit();
    }

    public void gotoNotificationFragment(){

        NotificationDriverFragment fragment = new NotificationDriverFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frm_container, fragment).commit();
    }

    public void gotoSettingsFragment(){

        SettingsFragment fragment = new SettingsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frm_container, fragment).commit();
    }

    @Override
    public void onBackPressed() { onExit(); }
}