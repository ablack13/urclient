package com.scijoker.urclient;

import java.util.Map;

/**
 * Created by scijoker on 06.06.15.
 */
public class Response {
    private Object response;
    private Map<String, String> headers;
    private Object tagObj;

    public Response(Object response, Map<String, String> headers, Object tagObj) {
        this.response = response;
        this.headers = headers;
        this.tagObj = tagObj;
    }

    public Object getResponseObj() {
        return response;
    }

    public Map<String, String> getResponseHeaders() {
        return headers;
    }

    public Object getTagObj() {
        return tagObj;
    }
}
