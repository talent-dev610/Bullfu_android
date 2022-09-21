package com.bullhu.android.activities;

import static com.bullhu.android.BullhuApplication.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.activities.driver.DriverMainActivity;
import com.bullhu.android.common.Commons;
import com.bullhu.android.common.Constants;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.SmartCallback;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.edt_email) TextInputEditText edt_email;
    @BindView(R.id.edt_password)
    TextInputEditText edt_password;
    String _email = "", _password = "";
    boolean _isFromLogout = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initValue();
        checkPermission();
        loadLayout();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log
                        Prefs.putString(Constants.TOKEN, token);
                        Prefs.getString(Constants.TOKEN, "");
                        Log.d(TAG, Prefs.getString(Constants.TOKEN, ""));
                    }
                });
    }

    private void checkPermission() {

        PermissionManager.Builder()
                .permission(PermissionEnum.CAMERA, PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.WRITE_EXTERNAL_STORAGE
                        , PermissionEnum.ACCESS_FINE_LOCATION, PermissionEnum.ACCESS_COARSE_LOCATION, PermissionEnum.CALL_PHONE)
                .askAgain(false)
                .callback(new SmartCallback() {
                    @Override
                    public void result(boolean allPermissionsGranted, boolean somePermissionsDeniedForever){
                    }
                })
                .ask(this);
    }

    private void initValue(){

        _isFromLogout = getIntent().getBooleanExtra(Constants.KEY_LOGOUT, false);
    }
    private void loadLayout() {


        LinearLayout lytContainer = (LinearLayout) findViewById(R.id.lyt_container);
        lytContainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_email.getWindowToken(), 0);
                return false;
            }
        });

        if (_isFromLogout){

            edt_email.setText("");
            edt_password.setText("");

        }
        else {

            _email = Prefs.getString( Constants.PREFKEY_USEREMAIL, "");
            _password = Prefs.getString(Constants.PREFKEY_USERPWD, "");

            edt_email.setText(_email);
            edt_password.setText(_password);

            if (!_email.isEmpty() && !_password.isEmpty())
                processLogin();
        }

    }

    @OnClick(R.id.btn_login) void logIn(){

        _email = edt_email.getText().toString();
        _password = edt_password.getText().toString();

        //gotoMain();
        if (checkedValid()) {

            processLogin();
        }
    }

    void processLogin(){

        showHUD();
        ApiManager.login(_email, _password, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {

                hideHUD();

                switch (result){
                    case SUCCESS:

                        Prefs.putString(Constants.PREFKEY_USEREMAIL, _email);
                        Prefs.putString(Constants.PREFKEY_USERPWD, _password);

                        gotoMain();

                        break;

                    case FAILURE:

                        if (resultParam != null)
                            showToast("Incorrect email or password");
                        else showToast("Something went wrong");

                        break;
                }
            }
        });
    }

    private void gotoMain(){

        //int user_type = /*Commons.g_user.getUser_type()*/Prefs.getInt("user_type", 0);
        int user_type = Commons.g_user.getUser_type();

        if (user_type == 1){

            startActivity(new Intent(LoginActivity.this, DriverMainActivity.class));
        }
        else {

            startActivity(new Intent(LoginActivity.this, ConsignorMainActivity.class));

        }
        finish();
    }

    @OnClick(R.id.btn_register) void gotoSignUp(){

        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    @OnClick(R.id.txv_forgot) void gotoForgot(){

        startActivity(new Intent(this, ForgotActivity.class));
        finish();
    }

    private boolean checkedValid(){

        if (Objects.requireNonNull(edt_email.getText()).toString().isEmpty()){

            showToast(R.string.input_email);
            return false;
        }
        else if (Objects.requireNonNull(edt_password.getText()).toString().isEmpty()){

            showToast(R.string.input_password);
            return false;
        }

        return true;
    }
}