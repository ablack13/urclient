package com.scijoker.urclient;

import com.android.volley.VolleyError;

/**
 * Created by scijoker on 04.12.14.
 */
public interface ErrorHandlerImpl {
    public void handleClientError(Object response, OnResponseListener onResponseListener);

    public void handleVolleyError(int errorCode, VolleyError volley, OnResponseListener onResponseListener);
}