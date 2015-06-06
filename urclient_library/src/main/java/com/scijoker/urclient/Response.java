package com.scijoker.urclient;

import java.util.Map;

/**
 * Created by scijoker on 06.06.15.
 */
public class Response {
    private Object response;
    private Map<String, String> headers;

    public Response(Object response, Map<String, String> headers) {
        this.response = response;
        this.headers = headers;
    }

    public Object getResponseObject() {
        return response;
    }

    public Map<String, String> getResponseHeaders() {
        return headers;
    }
}
