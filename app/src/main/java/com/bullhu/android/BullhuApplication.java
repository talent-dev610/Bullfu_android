package com.bullhu.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDexApplication;

import android.content.ContextWrapper;
import android.os.Bundle;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.pixplicity.easyprefs.library.Prefs;

import co.paystack.android.PaystackSdk;

public class BullhuApplication extends MultiDexApplication {

    public static final String TAG = BullhuApplication.class.getSimpleName();
    private static BullhuApplication _instance;
    public RequestQueue _requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;

        //Paystack initialize
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(getString(R.string.paystack_publicKey));

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

    }

    public static synchronized BullhuApplication getInstance(){

        return _instance;
    }

    public RequestQueue getRequestQueue(){

        if(_requestQueue == null){
            _requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return _requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){

        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
}