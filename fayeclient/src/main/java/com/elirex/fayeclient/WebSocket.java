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

    private final int MESSAGE_ONOPEN = 1;
    private final int MESSAGE_ONCLOSE = 2;
    private final int MESSAGE_ONMESSAGE = 3;

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
        Log.e(LOG_TAG, "Code:" + code + ", Reason:" + reason + ", Remote:" + remote);
        messageHandler.sendMessage(
                Message.obtain(messageHandler, MESSAGE_ONCLOSE));
    }

    @Override
    public void onError(Exception e) {
        Log.e(LOG_TAG, "onError", e);
    }

}
