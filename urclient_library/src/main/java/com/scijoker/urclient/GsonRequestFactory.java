package com.scijoker.urclient;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Created by scijoker on 03.12.14.
 */
class GsonRequestFactory {
    public static GsonRequest create(String url, int methodType, Object body, Class returnedObjectClass, Map mapHeaders, Response.Listener listener, Response.ErrorListener errorListener, OnCancelListener cancelListener, String accessKey, DefaultRetryPolicy retryPolicy, Request.Priority priority) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        GsonRequest gsonRequest = null;
        String jsonStr = null;
        if (body != null) {
            if (body.getClass().getName().equals(String.class.getName())) {
                jsonStr = (String) body;
            } else if (body.getClass().getName().equals(Map.class.getName())) {
                jsonStr = gson.toJson(body, Map.class);
            } else {
                jsonStr = gson.toJson(body);
            }
        } else {
            jsonStr = "";
        }
        Logger.log("URClientGsonRequestFactory", "request json: " + jsonStr);
        gsonRequest = new GsonRequest(url, methodType, jsonStr, returnedObjectClass, mapHeaders, listener, errorListener, cancelListener, accessKey, retryPolicy, priority);
        return gsonRequest;
    }
}
