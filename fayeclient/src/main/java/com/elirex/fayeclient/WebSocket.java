package com.elirex.fayeclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author Sheng-Yuan Wang (2015/9/3).
 */
public class WebSocket extends WebSocketClient {

    private static final String LOG_TAG = WebSocket.class.getSimpleName();

    public static final int MESSAGE_ONOPEN = 1;
    public static final int MESSAGE_ONCLOSE = 2;
    public static final int MESSAGE_ONMESSAGE = 3;
    public static final int MESSAGE_ONERROR = 4;

    private Handler messageHandler;

    public WebSocket(URI serverUri, Handler handler) {
        super(serverUri);
        messageHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        messageHandler.sendMessage(Message.obtain(messageHandler, MESSAGE_ONOPEN));
    }

    @Override
    public void onMessage(String s) {
        messageHandler.sendMessage(Message.obtain(messageHandler,
                MESSAGE_ONMESSAGE, s));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i(LOG_TAG, "code: " + code + ", reason: "
                + reason + ", remote: " + remote);
        messageHandler.sendMessage(
                Message.obtain(messageHandler, MESSAGE_ONCLOSE));
    }

    @Override
    public void onError(Exception e) {
        Log.e(LOG_TAG, "On WebSocket Error:", e);
    }

}
