package com.scijoker.urclient;

import android.os.Environment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

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

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, int methodType, String jsonBody, Class clazz, Map headers, Response.Listener listener, Response.ErrorListener errorListener, OnCancelListener cancelListener, String accessKey, DefaultRetryPolicy retryPolicy) {
        /*
        * public JsonRequest(int method, String url, String requestBody, Listener<T> listener,
            ErrorListener errorListener) {*/
        super(methodType, url, jsonBody, listener, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.cancelListener = cancelListener;
        this.accessKey = accessKey;
        setRetryPolicy(retryPolicy);
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
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        boolean isDecompressed = false;
        String json = "";
        try {
            if (response.headers != null) {
                for (Map.Entry<String, String> entry : response.headers.entrySet()) {
                    Logger.log(TAG, "header: " + entry.getKey() + " = " + entry.getValue());
                    if (entry.getValue().contains("deflate")) {
                        json = decodeJsonFromGzip(response);
                        isDecompressed = true;
                        break;
                    } else if (entry.getValue().contains("gzip")) {
                        json = encodeJsonFromDeflate(response);
                        isDecompressed = true;
                        break;
                    }
                }
            }
            if (!isDecompressed) {
                json = new String(response.data, "UTF-8");
            }

        } catch (JsonSyntaxException e) {
            Logger.log(TAG, e.getMessage(), true, "JsonSyntaxException");
            return Response.error(new ParseError(e));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //write json to file before read?
        if (accessKey != null) {
            String filePath = writeJsonToFile(json);
            json = readJsonFromFile(filePath);
        }
        Logger.log(TAG, json, true, "parseNetworkResponse_" + System.currentTimeMillis());

        if (clazz.getName().equals(String.class.getName())) {
            return (Response<T>) Response.success(
                    json, HttpHeaderParser.parseCacheHeaders(response));
        } else {
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    private String decodeJsonFromGzip(NetworkResponse response) {
        String json = "";
        InputStream in = null;
        InflaterInputStream inputStream = null;
        try {
            in = new ByteArrayInputStream(response.data);
            inputStream = new InflaterInputStream(in);
            //stream length--start
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            int tempSize = 1024;
            byte[] tempBuff;
            tempBuff = new byte[tempSize];
            while ((len = inputStream.read(tempBuff, 0, tempSize)) != -1) {
                bos.write(tempBuff, 0, len);
            }
            tempBuff = bos.toByteArray();
            bos.close();
            //--finish
            json = new String(tempBuff, HTTP.UTF_8);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private String encodeJsonFromDeflate(NetworkResponse response) {
        String json = new String("UTF-8");
        try {
            GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
            InputStreamReader reader = new InputStreamReader(gStream);
            BufferedReader in = new BufferedReader(reader);
            String read;
            while ((read = in.readLine()) != null) {
                json += read;
            }
            reader.close();
            in.close();
            gStream.close();
            json = new String(json.getBytes(), "UTF-8");
        } catch (IOException e) {
            return null;
        }

        return json;
    }


    private String writeJsonToFile(String json) {
        File tempFile = null;
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            if (accessKey.equals("")) {
                inputStream = new ByteArrayInputStream(json.getBytes("UTF-8"));
            } else {
                EDTools EDTools = new EDTools();
                json = EDTools.encrypt(accessKey, json);
                inputStream = new ByteArrayInputStream(json.getBytes());
            }
            bis = new BufferedInputStream(inputStream, 1024);

            //создание папки для синка и  для временного файла
            File tempSyncFolder = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + URClient.PACKAGE_NAME + "/_URClient/Temp/");
            if (!tempSyncFolder.exists()) {
                tempSyncFolder.mkdirs();
            }

            String fileName = null;
            fileName = "urc_" + System.currentTimeMillis() + ".part";
            tempFile = new File(tempSyncFolder, fileName);
            if (tempFile.exists()) {
                tempFile.delete();
            }

            fos = new FileOutputStream(tempFile);
            bos = new BufferedOutputStream(fos);
            int len = 0;
            while ((len = bis.read()) > 0) {
                bos.write(len);
            }
            bos.flush();
            bos.close();
            fos.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();
    }

    private String readJsonFromFile(String filePath) {
        String json = null;
        StringBuilder text = null;
        File fileTemp = null;
        boolean isDeleted = false;
        try {
            text = new StringBuilder();
            fileTemp = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(fileTemp));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            if (!accessKey.equals("")) {
                EDTools EDTools = new EDTools();
                json = EDTools.decrypt(accessKey, text.toString());
            } else {
                json = text.toString();
            }
            fileTemp.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}