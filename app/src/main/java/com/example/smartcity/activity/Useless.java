package com.example.smartcity.activity;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.io.*;
import java.net.Socket;
import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


import android.content.pm.PackageManager;


public class Useless {

    private static final String TAG = "HttpCoverageSender";
    private static final String SERVER_URL = "http://10.0.2.2:8080/upload-coverage"; // 替换为你的PC IP和端口

    public static void sendCoverageData(JSONObject json) {


        System.out.println("12345");
        System.out.println("12345");
        System.out.println("12345");
        new Thread(() -> {
            try {
                // 1. 获取覆盖率数据
                byte[] coverageData = getCoverageData();
                if (coverageData == null || coverageData.length == 0) {
                    Log.w(TAG, "No coverage data available");
                    return;
                }


                // 2. 创建JSON对象
                json.put("file_name", json.getString("appName") + "_" + json.getString("versionName") +".ec");
                json.put("file_content", Base64.encodeToString(coverageData, Base64.NO_WRAP));
                json.put("device_id", android.os.Build.MODEL);
                json.put("app_version", 1.0);
                json.put("timestamp", System.currentTimeMillis());


                // 3. 发送HTTP请求
                sendJsonToServer(json.toString());

            } catch (Exception e) {
                Log.e(TAG, "Error sending coverage: " + e.getMessage());
            }
        }).start();
    }

    private static void sendJsonToServer(String json) {
        HttpURLConnection connection = null;
        try {
            // 创建HTTP连接
            URL url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(30000);    // 30秒读取超时
            connection.setDoOutput(true);

            Log.w(TAG, "connection.set");
            // 发送JSON数据
            try (OutputStream os = connection.getOutputStream();
                 DataOutputStream dos = new DataOutputStream(os)) {
                byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
                dos.write(jsonBytes);
                dos.flush();
            }catch (Exception e) {
                Log.w(TAG, "Non-retryable error: " + e.getMessage());
            }

            Log.w(TAG, "dos.write");
            // 检查响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取服务器响应
                try (java.io.InputStream is = connection.getInputStream();
                     java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    String response = result.toString(StandardCharsets.UTF_8.name());
                    Log.w(TAG, "Server response: " + response);
                }
            } else {
                Log.w(TAG, "Server responded with code: " + responseCode);
                // 读取错误响应
                try (java.io.InputStream es = connection.getErrorStream();
                     java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = es.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    String error = result.toString(StandardCharsets.UTF_8.name());
                    Log.w(TAG, "Server error: " + error);
                }
            }
            Log.e(TAG, "Server response");

        } catch (Exception e) {
            Log.e(TAG, "Network error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static byte[] getCoverageData() {
        try {
            // 使用反射获取 Jacoco 覆盖率数据
            Class<?> rtClass = Class.forName("org.jacoco.agent.rt.RT");
            Object agent = rtClass.getMethod("getAgent").invoke(null);

            if (agent != null) {
                return (byte[]) agent.getClass()
                        .getMethod("getExecutionData", boolean.class)
                        .invoke(agent, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting coverage data: " + e.getMessage());
        }
        return new byte[0];
    }
}