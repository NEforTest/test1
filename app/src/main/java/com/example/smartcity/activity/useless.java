package com.example.smartcity.activity;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class useless {

    public void establishTCP() throws IOException {
        Log.d("TCP", "Client: Connecting...");
        Socket socket = new Socket("10.0.2.2", 3333);
        String message = "AndroidRes,Where is my Pig (Android)?";
        try {
            Log.d("TCP", "Client: Sending: '" + message + "'");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);
            out.println(message);
        } catch (Exception e) {
            Log.e("TCP", "S: Error", e);
        } finally {
            socket.close();
        }
    }
}
