package com.scijoker.urclient;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by scijoker on 06.06.15.
 */
public class ResponseFileManager {
    private String accessKey;

    public ResponseFileManager(String accessKey) {
        this.accessKey = accessKey;
    }

    public String write(String json) {
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();
    }

    public String read(String filePath) {
        String json = null;
        StringBuilder text = null;
        File fileTemp = null;
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
            if (accessKey != null && !accessKey.equals("")) {
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
