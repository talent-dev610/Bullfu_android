package com.bullhu.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bullhu.android.R;
import com.bullhu.android.activities.BaseActivity;
import com.bullhu.android.activities.ChangePasswordActivity;
import com.bullhu.android.activities.LoginActivity;
import com.bullhu.android.activities.MyProfileActivity;
import com.bullhu.android.activities.driver.DriverMainActivity;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment {

    BaseActivity activity;
    View view;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.txv_my_profile) void updateProfile(){

        startActivity(new Intent(activity, MyProfileActivity.class));
    }

    @OnClick(R.id.txv_change_password) void changePassword(){
        startActivity(new Intent(activity, ChangePasswordActivity.class));
    }

    @OnClick(R.id.txv_logout) void logout(){

        Prefs.clear();

        startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
    }
}