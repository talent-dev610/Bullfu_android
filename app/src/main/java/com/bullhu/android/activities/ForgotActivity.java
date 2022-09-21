package com.bullhu.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.bullhu.android.R;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class ForgotActivity extends BaseActivity {

    @BindView(R.id.edt_email)
    EditText edt_email;
    String _email = "";
    String strPincode = "";
    @BindView(R.id.otp)
    OtpTextView otp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        ButterKnife.bind(this);
        loadLayout();
    }

    private void loadLayout() {

        otp.setVisibility(View.GONE);
        otp.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }
            @Override
            public void onOTPComplete(String otp) {
                strPincode = otp;
                if ( otp.length() == 6) {
                    //showToast("Verification success!");
                    checkConfirmCode();
                }

            }
        });
    }

    @OnClick(R.id.btn_send) void sendEmail(){

        _email = edt_email.getText().toString();

        if (_email.isEmpty()){
            showToast(getString(R.string.input_email));
        }
        else forgotPassword();
    }

    private void forgotPassword(){

        showHUD();
        ApiManager.forgotPassword(_email, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                hideHUD();
                switch (result){
                    case FAILURE:
                        showToast((String) resultParam);
                        break;

                    case SUCCESS:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                otp.setVisibility(View.VISIBLE);
                            }
                        });
                        break;
                }
            }
        });
    }

    private void checkConfirmCode(){

        showHUD();
        ApiManager.checkConfirmCode(_email, strPincode, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                hideHUD();
                switch (result){
                    case FAILURE:
                        showToast((String) resultParam);
                        break;

                    case SUCCESS:

                        gotoResetPassword();

                        break;
                }
            }
        });
    }

    void gotoResetPassword(){

        startActivity(new Intent(this, SecurityActivity.class).putExtra("Email", _email));
        finish();
    }

    @OnClick(R.id.imv_back) void gotoBack(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        gotoBack();
    }
}