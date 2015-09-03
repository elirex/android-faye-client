package com.elirex.fayeclient;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.HashSet;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Sheng-Yuan Wang (2015/9/3).
 */
public class FayeClient {

    private static final String LOG_TAG = FayeClient.class.getSimpleName();

    private final String HANDSHAKE_CHANNEL = "/meta/handshake";
    private final String CONNECT_CHANNEL = "/meta/connect";
    private final String DISCONNECT_CHANNEL = "/meta/disconnect";
    private final String SUBSCRIBE_CHANNEL = "/meta/subscribe";
    private final String UNSUBSCRIBE_CHANNEL = "/meta/unsubscribe";

    private final int MESSAGE_ONOPEN = 1;
    private final int MESSAGE_ONCLOSE = 2;
    private final int MESSAGE_ONMESSAGE = 3;

    private WebSocket webSocket = null;
    private Listener listener = null;
    private HashSet<String> channels;
    private String serverUrl = "";
    private String authToken = "";
    private String accessToken = "";
    private String clientId = "";
    private boolean fayeConnected = false;
    private boolean websocketConnected = false;

    private Handler messageHandler;

    public FayeClient(String url, String authToken, String accessToken) {
        this(url, authToken, accessToken, "");
    }

    public FayeClient(String url, String authToken, String accessToken,
                      String channel) {
        serverUrl = url;
        this.authToken = authToken;
        this.accessToken = accessToken;
        channels = new HashSet<String>();
        if(channel.length() > 0) {
            channels.add(channel);
        }

    }

    {
        HandlerThread thread = new HandlerThread("FayeHandler");
        thread.start();
        messageHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                    case MESSAGE_ONOPEN:
                        Log.i(LOG_TAG, "onOpen() executed");
                        websocketConnected = true;
                        handShake();
                        break;

                    case MESSAGE_ONCLOSE:
                        Log.i(LOG_TAG, "onClosed() executed");
                        websocketConnected = false;
                        fayeConnected = false;
                        if(listener != null && listener instanceof Listener) {
                            listener.disconnectedFromServer(FayeClient.this);
                        }
                        break;
                    case MESSAGE_ONMESSAGE:
                        try {
                            Log.i(LOG_TAG, "onMessage executed");
                            parseFayeMessage((String) msg.obj);
                        } catch (NotYetConnectedException e) {
                            // Do noting
                        }
                        break;
                }
            }
        };
    }

    /* Public Methods */
    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void addChannel(String channel) {
        channels.add(channel);
    }

    public boolean isWebsocketConnected() {
        return websocketConnected;
    }

    public boolean isFayeConnected() {
        return fayeConnected;
    }

    public void connectToServer() {
        openWebSocketConnection();
    }

    public void disconnectFromServer() {
        for(String channel : channels) {
            unsubscribe(channel);
        }
        channels.clear();
        disconnect();
    }

    public void subscribeToChannel(String channel) {
        subscribe(channel);
        channels.add(channel);
    }

    public void unsubscribeFromChannel(String channel) {
        unsubscribe(channel);
        channels.remove(channel);
    }

    /* Private Methods */

    private Socket getSSLWebSocket() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            return factory.createSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openWebSocketConnection() {
        // Clean up any existing socket
        if(webSocket != null) {
            webSocket.close();
        }
        try {
            URI uri = new URI(serverUrl);
            webSocket = new WebSocket(uri, messageHandler);
            Log.d(LOG_TAG, "Scheme:" + uri.getScheme());
            if(uri.getScheme().equals("wss")) {
                webSocket.setSocket(getSSLWebSocket());
            }
            webSocket.connect();
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "Server URL error", e);
        }
    }

    private void closeWebSocketConnection() {
        if(webSocket != null) {
            webSocket.close();
        }
        listener = null;
    }

    private void handShake() {
        String handshake = String
                .format("{\"supportedConnectionTypes\":[\"long-polling\",\"callback-polling\",\"iframe\",\"websocket\"],\"minimumVersion\":\"1.0beta\",\"version\":\"1.0\",\"channel\":\"/meta/handshake\", \"ext\":{\"accessToken\":\"%s\"}}",
                        accessToken);
        webSocket.send(handshake);
    }

    private void subscribe(String channel) {
        String subscribe = String
                .format("{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/subscribe\", \"ext\":{\"accessToken\":\"%s\"}}",
                        clientId, channel, accessToken);
        webSocket.send(subscribe);
    }

    private void unsubscribe(String channel) {
        String unsubscribe = String
                .format("{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/unsubscribe\",\"ext\":{\"accessToken\":\"%s\"}}",
                        clientId, channel, accessToken);
        webSocket.send(unsubscribe);

    }

    private void connect() {
        String connect = String
                .format("{\"clientId\":\"%s\",\"connectionType\":\"long-polling\",\"channel\":\"/meta/connect\",\"ext\":{\"accessToken\":\"%s\"}}",
                        clientId, accessToken);
        webSocket.send(connect);
    }

    private void disconnect() {
        String disconnect = String
                .format("{\"clientId\":\"%s\",\"connectionType\":\"long-polling\",\"channel\":\"/meta/disconnect\",\"ext\":{\"accessToken\":\"%s\"}}",
                        clientId, accessToken);
        webSocket.send(disconnect);
    }

    private void parseFayeMessage(String message) {
        JSONArray arr = null;
        JSONObject obj = null;

        try {
            arr = new JSONArray(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int length = arr.length();
        for(int i = 0; i < length; ++i) {
            obj = arr.optJSONObject(i);

            if(obj.optString("channel").equals(HANDSHAKE_CHANNEL)) {
                if(obj.optBoolean("successful")) {
                    clientId = obj.optString("clientId");
                    if(listener != null && listener instanceof Listener) {
                        listener.connectedToServer(this);
                    }
                    connect();
                } else {
                    Log.e(LOG_TAG, "onMessage(): Error with bayeux handshake");
                }
            } else if(obj.optString("channel").equals(CONNECT_CHANNEL)) {
                if(obj.optBoolean("successful")) {
                    fayeConnected = true;
                    connect();
                } else {
                    Log.e(LOG_TAG, "onMessage(): Error connecting to faye");
                }
            } else if(obj.optString("channel").equals(DISCONNECT_CHANNEL)) {
                if(obj.optBoolean("successful")) {
                    fayeConnected = false;
                    closeWebSocketConnection();
                    if(listener != null && listener instanceof Listener) {
                        listener.disconnectedFromServer(this);
                    }
                } else {
                    Log.e(LOG_TAG, "onMessage(): Error disconnecting from faye");
                }
            } else if(obj.optString("channel").equals(SUBSCRIBE_CHANNEL)) {
                if(obj.optBoolean("successful")) {
                    Log.i(LOG_TAG, String.format(
                            "Subscribed to channel %s on fay",
                            obj.optString("subscription")));
                } else {
                    Log.e(LOG_TAG, String.format(
                            "Error subscribing to channel %s on faye with error %s",
                            obj.optString("subscription"),
                            obj.optString("error")));
                }
            } else if(obj.optString("channel").equals(UNSUBSCRIBE_CHANNEL)) {
                Log.e(LOG_TAG, String.format("Unsubscribed from channel %s on faye",
                        obj.optString("subscription")));
            } else {
                if(channels.contains(obj.optString("channel"))) {
                    if(obj.optString("data") != null) {
                        if(listener != null && listener instanceof Listener) {
                            listener.messageReceived(this, obj.optString("data"));
                        }
                    }
                } else {
                    Log.e(LOG_TAG, String.format("No match for channel %s",
                            obj.optString("channel")));
                }
            }
        }
    }


    /* Interface */
    public interface Listener {
        public void connectedToServer(FayeClient fc);
        public void disconnectedFromServer(FayeClient fc);
        public void messageReceived(FayeClient fc, String msg);
    }

}
