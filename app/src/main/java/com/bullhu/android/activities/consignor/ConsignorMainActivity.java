package com.bullhu.android.activities.consignor;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.bullhu.android.R;
import com.bullhu.android.activities.BaseActivity;
import com.bullhu.android.fragments.SettingsFragment;
import com.bullhu.android.fragments.consignor.HistoryConsignorFragment;
import com.bullhu.android.fragments.consignor.HomeConsignorFragment;
import com.bullhu.android.fragments.consignor.NotificationConsignorFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.pixplicity.easyprefs.library.Prefs;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsignorMainActivity extends BaseActivity{

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consignor_main);
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

        HomeConsignorFragment fragment = new HomeConsignorFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frm_container, fragment).commit();

    }

    public void gotoHistoryFragment(){

        HistoryConsignorFragment fragment = new HistoryConsignorFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frm_container, fragment).commit();
    }

    public void gotoNotificationFragment(){

        NotificationConsignorFragment fragment = new NotificationConsignorFragment();
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