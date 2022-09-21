package com.bullhu.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.activities.driver.DriverMainActivity;
import com.bullhu.android.common.Commons;
import com.bullhu.android.common.Constants;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;
import com.google.android.material.textfield.TextInputEditText;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecurityActivity extends BaseActivity {

    @BindView(R.id.edt_new_password)
    TextInputEditText edt_new_password;
    @BindView(R.id.edt_confirm_password)
    TextInputEditText edt_confirm_password;
    String _new_password = "", _confirm_password = "", _email = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        ButterKnife.bind(this);
        _email = getIntent().getStringExtra("Email");
        loadLayout();
    }

    private void loadLayout() {


    }

    @OnClick(R.id.btn_update) void update(){

        _new_password = edt_new_password.getText().toString();
        _confirm_password = edt_confirm_password.getText().toString();

        if (checkedValid()){
            processReset();
        }

    }

    private void processReset(){

        showHUD();
        ApiManager.resetPassword(_email, _new_password, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                hideHUD();
                switch (result){

                    case SUCCESS:

                        showToast("Success");

                        Prefs.putString(Constants.PREFKEY_USEREMAIL, _email);
                        Prefs.putString(Constants.PREFKEY_USERPWD, _new_password);

                        gotoMain();
                        break;

                    case FAILURE:
                        if (resultParam == null)
                            showToast("Something went wrong");
                        else
                            showToast((String) resultParam);
                        break;
                }
            }
        });
    }

    void gotoMain(){

        int user_type = Commons.g_user.getUser_type();

        if (user_type == 1){

            startActivity(new Intent(this, DriverMainActivity.class));
        }
        else {

            startActivity(new Intent(this, ConsignorMainActivity.class));

        }
        finish();
    }

    private boolean checkedValid(){

        if (_new_password.isEmpty()){
            showToast(getString(R.string.enter_new_password));
            return false;
        }
        else if (!_new_password.equals(_confirm_password)){
            showToast(getString(R.string.not_match_password));
            return false;
        }
        return true;
    }

    @OnClick(R.id.imv_back) void gotoBack(){

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onExit() {
        gotoBack();
    }
}