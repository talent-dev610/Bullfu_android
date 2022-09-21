package com.bullhu.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.bullhu.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.edt_current_password)
    EditText edt_current_password;
    @BindView(R.id.edt_new_password)
    EditText edt_new_password;
    @BindView(R.id.edt_confirm_password)
    EditText edt_confirm_password;
    String _current_password = "", _new_password = "", _confirm_password = "";
    String password = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_update) void update(){

        _current_password = edt_current_password.getText().toString();
        _new_password = edt_new_password.getText().toString();
        _confirm_password = edt_confirm_password.getText().toString();

        if (checkedValid()){
            password = _new_password;
            processChange();
        }

    }

    private void processChange(){

        finish();

        /*showHUD();
        ApiManager.resetPassword(Commons.thisUser.getId(), password, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                hideHUD();
                switch (result){
                    case SUCCESS:
                        showToast1("Success");
                        finish();
                        break;

                    case FAILURE:
                        showToast1(getString(R.string.error));
                        break;
                }
            }
        });*/
    }

    private boolean checkedValid(){

        if (_current_password.isEmpty()){
            showToast(R.string.enter_current_password);
            return false;
        }
        else if (_new_password.isEmpty()){
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

        finish();
    }

    @Override
    public void onExit() {
        gotoBack();
    }
}