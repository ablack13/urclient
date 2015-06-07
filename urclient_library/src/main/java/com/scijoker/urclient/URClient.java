package com.scijoker.urclient;

import android.content.Context;
import android.os.Handler;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Map;

/**
 * Created by scijoker on 21.10.14.
 */
public class URClient {
    public static String PACKAGE_NAME;
    private String TAG = getClass().getSimpleName();
    private OnResponseListener onResponseListener;
    private Context context;
    private ErrorHandlerImpl errorHandlerImpl;
    private OnCancelListener cancelListener;
    private String accessKey;
    private GsonRequest gsonRequest;
    private OnStartListener startRequestListener;
    private boolean isRunning = false;
    private String _TAG;
    private Map mapHeaders;
    private Object body;
    private int timeout = 2500;
    private int maxRetry = 1;
    private float backoffMultiplier = 1.0f;

    private URClient() {
    }

    private void execute(final String url, final int methodType, final Class returnObjectClass) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    return;
                }
                isRunning = true;
                Logger.log(TAG, "call execute()");
                if (onResponseListener == null) {
                    onResponseListener = new OnResponseListener() {
                        @Override
                        public void onResponseSuccessful(com.scijoker.urclient.Response response) {
                            //not use, as default
                        }

                        @Override
                        public void onResponseFailed(int errorCode, String exceptionInfo) {
                            //not use, as default
                        }
                    };
                }
                if (startRequestListener != null) {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startRequestListener.onRequestStart();
                        }
                    });
                }
                if (mapHeaders == null) {
                    mapHeaders = DefaultClientHelper.getDefaultHeaders();
                }
                if (returnObjectClass == null) {
                    throw new NullPointerException("returnObject can't be a null");
                }
                gsonRequest = GsonRequestFactory.create(url, methodType, body, returnObjectClass, mapHeaders, listener, errorListener, cancelListener, accessKey, new DefaultRetryPolicy(timeout, maxRetry, backoffMultiplier));
                gsonRequest.setTag(TAG);
                VolleySingleton.getInstance(context, Logger.DEBUG).addToRequestQueue(gsonRequest);
            }
        }).start();
    }

    private Response.Listener listener = new Response.Listener() {
        @Override
        public void onResponse(final Object response) {
            isRunning = false;
            if (errorHandlerImpl != null) {
                errorHandlerImpl.handleClientError((com.scijoker.urclient.Response) response, onResponseListener);
            } else {
                onResponseListener.onResponseSuccessful((com.scijoker.urclient.Response) response);
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(final VolleyError error) {
            isRunning = false;
            if (errorHandlerImpl == null) {
                //volley error code [1.14.4.18.5.23.0.16.18.1.25.26.14.5.18]
                if (error.getMessage() != null) {
                    onResponseListener.onResponseFailed(188, error.getMessage());
                }
            } else {
                errorHandlerImpl.handleVolleyError(188, error, onResponseListener);
            }
        }
    };

    public static Builder createRequest() {
        PACKAGE_NAME = URClientService.getContext().getPackageName();
        return new URClient().new Builder();
    }

    private void cancelRequest() {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Logger.log(TAG, "cancelRequest();");
                if (gsonRequest == null) {
                    throw new NullPointerException("Before call 'cancel() initialize GsonRequest instance");
                } else if (isRunning) {
                    isRunning = false;
                    VolleySingleton.getInstance(context, Logger.DEBUG).cancelRequestFromQueue(_TAG);
                    gsonRequest.cancel();
                    if (cancelListener != null) {
                        cancelListener.onResponseCancelListener();
                    }
                }
            }
        });
    }

    public class Builder {
        public Builder() {
            context = URClientService.getContext();
            _TAG = "URClient" + System.currentTimeMillis();
        }

        public Builder setOnResponseListener(OnResponseListener onResponseListener) {
            URClient.this.onResponseListener = onResponseListener;
            return this;
        }

        public Builder setOnStartListener(OnStartListener StartRequestListener) {
            startRequestListener = StartRequestListener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener CancelListener) {
            cancelListener = CancelListener;
            return this;
        }

        public Builder setErrorHandler(ErrorHandlerImpl errorHandlerImpl) {
            URClient.this.errorHandlerImpl = errorHandlerImpl;
            return this;
        }


        public Builder saveOnDevice(String AccessKey) {
            accessKey = AccessKey;
            return this;
        }

        public Builder setHeaders(Map headers) {
            mapHeaders = headers;
            return this;
        }

        public Builder setBody(Object Body) {
            body = Body;
            return this;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void cancel() {
            cancelRequest();
        }

        public Builder setRetryPolice(int timeoutInMillis, int maximumRetry, float BackoffMultiplier) {
            timeout = timeoutInMillis;
            maxRetry = maximumRetry;
            backoffMultiplier = BackoffMultiplier;
            return this;
        }

        public Builder sendGET(String url, Class returnObject) {
            execute(url, RequestMethod.Method_GET, returnObject);
            return this;
        }

        public Builder sendPOST(String url, Class returnObject) {
            execute(url, RequestMethod.Method_POST, returnObject);
            return this;
        }

        public Builder sendDELETE(String url, Class returnObjectClass) {
            execute(url, RequestMethod.Method_DELETE, returnObjectClass);
            return this;
        }

        public Builder sendPUT(String url, Class returnObject) {
            execute(url, RequestMethod.Method_PUT, returnObject);
            return this;
        }
    }
}