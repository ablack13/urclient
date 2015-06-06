package com.scijoker.urclient;

import com.android.volley.*;
import com.google.gson.JsonSyntaxException;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by scijoker on 06.06.15.
 */
public class ResponseDecoder {
    private String TAG = getClass().getSimpleName();
    private NetworkResponse response;

    public ResponseDecoder(NetworkResponse response) {
        this.response = response;
    }

    public String decode() {
        String json = "";
        if (response.data == null || response.data.length == 0) {
            return json;
        }
        boolean isDecompressed = false;
        try {
            if (response.headers != null) {
                for (Map.Entry<String, String> entry : response.headers.entrySet()) {
                    Logger.log(TAG, "header: " + entry.getKey() + " = " + entry.getValue());
                    if (hasDeflateCompression(entry)) {
                        json = decodeJsonFromGzip(response);
                        isDecompressed = true;
                        break;
                    } else if (hasGzipCompression(entry)) {
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
            return json;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return json;
    }

    private boolean hasGzipCompression(Map.Entry<String, String> entry) {
        return entry.getValue().contains("gzip");
    }


    private boolean hasDeflateCompression(Map.Entry<String, String> entry) {
        return entry.getValue().contains("deflate");
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
}
