package com.scijoker.urclient;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by scijoker on 03.12.14.
 */
class GsonRequest<T> extends JsonRequest<T> {
    private final String TAG = getClass().getSimpleName();
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private final OnCancelListener cancelListener;
    private String accessKey;
    private Priority priority;

    /**
     * Make a GET/POST request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, int methodType, String jsonBody, Class clazz, Map headers, Response.Listener listener, Response.ErrorListener errorListener, OnCancelListener cancelListener, String accessKey, DefaultRetryPolicy retryPolicy, Priority priority) {
        /*
        * public JsonRequest(int method, String url, String requestBody, Listener<T> listener,
            ErrorListener errorListener) {*/
        super(methodType, url, jsonBody, listener, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.cancelListener = cancelListener;
        this.accessKey = accessKey;
        this.priority = priority;
        setRetryPolicy(retryPolicy);
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public void cancel() {
        super.cancel();
        if (cancelListener != null) {
            cancelListener.onResponseCancelListener();
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        com.scijoker.urclient.Response response = null;
        String json = new ResponseDecoder(networkResponse).decode();
        if (accessKey != null) {
            ResponseFileManager responseFileManager = new ResponseFileManager(accessKey);
            String filePath = responseFileManager.write(json);
            json = responseFileManager.read(filePath);
        }
        Logger.log(TAG, json, true, "parseNetworkResponse_" + System.currentTimeMillis());
        if (clazz.getName().equals(String.class.getName())) {
            response = new com.scijoker.urclient.Response(json, networkResponse.headers);
            return (Response<T>) Response.success(response, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } else {
            response = new com.scijoker.urclient.Response(gson.fromJson(json, clazz), networkResponse.headers);
            return (Response<T>) Response.success(
                    response, HttpHeaderParser.parseCacheHeaders(networkResponse));
        }
    }
}