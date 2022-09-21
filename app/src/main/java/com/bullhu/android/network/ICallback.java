package com.bullhu.android.network;

public interface ICallback {

    public enum RESULT {SUCCESS, FAILURE};
    public void onCompletion(RESULT result, Object resultParam);

}
