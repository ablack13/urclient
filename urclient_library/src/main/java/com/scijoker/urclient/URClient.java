package com.scijoker.urclient;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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
    private PRIORITY priority = PRIORITY.NORMAL;

    public static enum METHOD {POST, GET, PUT, DELETE}

    public static enum PRIORITY {LOW, NORMAL, HIGH, IMMEDIATE}

    private URClient() {
    }

    private void exec(final String url, final int methodType, final Class returnObjectClass) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    return;
                }
                isRunning = true;
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
                gsonRequest = GsonRequestFactory.create(url, methodType, body, returnObjectClass, mapHeaders, listener, errorListener, cancelListener, accessKey, new DefaultRetryPolicy(timeout, maxRetry, backoffMultiplier), getPriority());
                gsonRequest.setTag(TAG);
                VolleySingleton.getInstance(context, Logger.DEBUG).addToRequestQueue(gsonRequest);
            }
        }).start();
    }

    private Request.Priority getPriority() {
        switch (priority) {
            case LOW: {
                return Request.Priority.LOW;
            }
            case NORMAL: {
                return Request.Priority.LOW;
            }
            case HIGH: {
                return Request.Priority.LOW;
            }
            case IMMEDIATE: {
                return Request.Priority.IMMEDIATE;
            }
            default:
                return Request.Priority.NORMAL;
        }
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

    @Deprecated
    public static Builder createRequest() {
        return create();
    }

    public static Builder create() {
        PACKAGE_NAME = URClientService.getContext().getPackageName();
        return new URClient().new Builder();
    }

    @Deprecated
    private void cancelRequest() {
        cancel();
    }

    public void cancel() {
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
            _TAG = "URClient_" + System.currentTimeMillis();
        }

        @Deprecated
        public Builder setOnResponseListener(OnResponseListener onResponseListener) {
            return responseListener(onResponseListener);
        }

        public Builder responseListener(OnResponseListener onResponseListener) {
            URClient.this.onResponseListener = onResponseListener;
            return this;
        }

        @Deprecated
        public Builder setOnStartListener(OnStartListener StartRequestListener) {
            return startListener(StartRequestListener);
        }

        public Builder startListener(OnStartListener StartRequestListener) {
            startRequestListener = StartRequestListener;
            return this;
        }

        @Deprecated
        public Builder setOnCancelListener(OnCancelListener CancelListener) {
            return cancelListener(CancelListener);
        }

        public Builder cancelListener(OnCancelListener CancelListener) {
            cancelListener = CancelListener;
            return this;
        }

        public boolean cancel() {
            boolean isCanceled = isRunning;
            if (isRunning) {
                URClient.this.cancel();
                return true;
            }
            return isCanceled;
        }

        @Deprecated
        public Builder setErrorHandler(ErrorHandlerImpl errorHandlerImpl) {
            return errorHandler(errorHandlerImpl);
        }

        public Builder errorHandler(ErrorHandlerImpl errorHandlerImpl) {
            URClient.this.errorHandlerImpl = errorHandlerImpl;
            return this;
        }


        @Deprecated
        public Builder saveOnDevice(String AccessKey) {
            return save(AccessKey);
        }

        public Builder save(String AccessKey) {
            accessKey = AccessKey;
            return this;
        }

        @Deprecated
        public Builder setHeaders(Map headers) {
            return headers(headers);
        }

        public Builder headers(Map headers) {
            mapHeaders = headers;
            return this;
        }

        @Deprecated
        public Builder setBody(Object Body) {
            return body(Body);
        }

        public Builder body(Object Body) {
            body = Body;
            return this;
        }

        @Deprecated
        public Builder setRetryPolice(int timeoutInMillis, int maximumRetry, float BackoffMultiplier) {
            return retryPolice(timeoutInMillis, maximumRetry, BackoffMultiplier);
        }

        public Builder retryPolice(int timeoutInMillis, int maximumRetry, float BackoffMultiplier) {
            timeout = timeoutInMillis;
            maxRetry = maximumRetry;
            backoffMultiplier = BackoffMultiplier;
            return this;
        }

        public Builder priority(PRIORITY priority) {
            URClient.this.priority = priority;
            return this;
        }

        @Deprecated
        public Builder sendGET(String url, Class returnObject) {
            return send(url, METHOD.GET, returnObject);
        }

        @Deprecated
        public Builder sendPOST(String url, Class returnObject) {
            return send(url, METHOD.POST, returnObject);
        }

        @Deprecated
        public Builder sendDELETE(String url, Class returnObject) {
            return send(url, METHOD.DELETE, returnObject);
        }

        @Deprecated
        public Builder sendPUT(String url, Class returnObject) {
            return send(url, METHOD.PUT, returnObject);
        }

        public Builder send(String url, METHOD type, Class returnObject) {
            int methodType = 0;
            switch (type) {
                case GET: {
                    methodType = RequestMethod.Method_GET;
                    break;
                }
                case POST: {
                    methodType = RequestMethod.Method_POST;
                    break;
                }
                case PUT: {
                    methodType = RequestMethod.Method_PUT;
                    break;
                }
                case DELETE: {
                    methodType = RequestMethod.Method_DELETE;
                    break;
                }
            }
            URClient.this.exec(url, methodType, returnObject);
            return this;
        }
    }
}