package com.bullhu.android.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bullhu.android.R;
import com.bullhu.android.common.Constants;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;
import com.dovar.dtoast.DToast;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pixplicity.easyprefs.library.Prefs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BaseActivity extends AppCompatActivity implements Handler.Callback {

    KProgressHUD hud = null;
    public Context _context = null;
    public boolean _isEndFlag;    // double click back button to finish the app
    public Handler _handler = null;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        _context = this;
        _handler = new Handler(this);
        // Obtain the FirebaseAnalytics instance.

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

    }

    public void showToast(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DToast.make(BaseActivity.this)
                        .setText(com.dovar.dtoast.R.id.tv_content_default, message)
                        .setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 120)
                        .show();
            }
        });


    }

    public void showToast(int id) {
        showToast(getString(id));
    }


    public void showHUD(String label, String detail) {

        if (hud != null)
            hideHUD();

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setDetailsLabel(detail)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    public void showHUD(String label) {

        if (hud != null)
            hideHUD();

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    public void showHUD() {

        if (hud != null)
            hideHUD();

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    public void hideHUD() {

        if (hud != null)
            hud.dismiss();

        hud = null;
    }

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(_context).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, _context.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void printHash(){

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static final int BACK_TWO_CLICK_DELAY_TIME = 3000 ; //ms
    public void onExit() {

        if (_isEndFlag == false) {

            Toast.makeText(this, getString(R.string.str_back_one_more_end),
                    Toast.LENGTH_SHORT).show();
            _isEndFlag = true;

            _handler.postDelayed(_exitRunner, BACK_TWO_CLICK_DELAY_TIME);

        } else if (_isEndFlag == true) {

            finish();
        }
    }

    public Runnable _exitRunner = new Runnable() {
        @Override
        public void run() {
            _isEndFlag = false ;
        }
    };

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what){
            default:
                break;
        }
        return false;
    }

    public void uploadToken(){

        ApiManager.uploadToken(Prefs.getString(Constants.TOKEN, ""), new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                switch (result){
                    case SUCCESS:
                        Log.d("TokenUpload == Success", "");
                        break;
                    case FAILURE:
                        Log.d("TokenUpload == Failed", "");
                        break;
                }
            }
        });
    }

}
