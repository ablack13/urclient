package com.scijoker.urclient;


/**
 * Created by scijoker on 21.10.14.
 */
public interface OnResponseListener {
    public void onResponseSuccessful(Response response);

    public void onResponseFailed(int errorCode, String exceptionInfo);
}
