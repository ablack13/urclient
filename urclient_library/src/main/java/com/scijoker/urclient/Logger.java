/*
 * Copyright (c) 2014. Developed by Andrew Prayzner aka Scijoker
 * e-mail: scijoker@gmail.com
 */

package com.scijoker.urclient;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by scijoker on 05.12.14.
 */
public class Logger {
    public static boolean DEBUG;

    public static void log(String className, String message) {
        log(className, message, false);
    }

    public static void log(String className, String message, boolean isLogToDisk) {
        log(className, message, isLogToDisk, null);
    }

    public static void log(String className, String message, boolean isLogToDisk, String logFileName) {
        if (DEBUG) {
            Log.d("[URClientLogger_DEBUG_" + className + "]", message);
            if (isLogToDisk) {
                writeLogsToFile(logFileName, message);
            }
        }
    }

    private static void writeLogsToFile(final String logName, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File tempFile = null;
                InputStream inputStream = null;
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                try {
                    inputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));
                    bis = new BufferedInputStream(inputStream, 1024);

                    //создание папки для синка и  для временного файла
                    File tempSyncFolder = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + URClient.PACKAGE_NAME + "/_URClient/Debug/");
                    if (!tempSyncFolder.exists()) {
                        tempSyncFolder.mkdirs();
                    }

                    String fileName = null;
                    if (logName != null) {
                        fileName = "urc_" + logName + "_DEBUG.txt";
                    } else {
                        fileName = "urc_" + System.currentTimeMillis() + "_DEBUG.txt";
                    }
                    tempFile = new File(tempSyncFolder, fileName);
                    tempFile.createNewFile();

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
            }
        }).start();

    }
}
