package com.elirex.fayeclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Sheng-Yuan Wang (2015/9/3).
 */
public class FayeService extends Service {

    private static final String LOG_TAG = FayeService.class.getSimpleName();

    private static String SERVER_HOST;
    private static String SERVER_PORT;
    private static String SERVER_PATH;
    private static String ACCESS_TOKEN;
    private static String AUTH_TOKEN;
    private static HashSet<String> channels;

    private FayeServiceBinder binder;

    private FayeClient fayeClient;
    private ArrayList<Listener> listeners;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new FayeServiceBinder();
    }

    public static void initFayeService(String host, int port, String path,
                                String accessToken, String authToken) {
        SERVER_HOST = host;
        SERVER_PORT = String.valueOf(port);
        SERVER_PATH = path;
        ACCESS_TOKEN = accessToken;
        AUTH_TOKEN = authToken;
    }

    public static void addChannel(String channel) {
        if(channels == null) {
            channels = new HashSet<String>();
        }
        channels.add(channel);
    }

    public static void addAllChannel(String... channel) {
        if(channels == null) {
            channels = new HashSet<String>();
        }
        channels.addAll(Arrays.asList(channel));
    }

    public static void removeChannel(String channel) {
        if(channels != null && channels.contains(channel)) {
            channels.remove(channel);
        }
    }

    public static void removeAllChannel() {
        if(channels != null) {
            channels.clear();
        }
    }

    private FayeClient.Listener fayeClientListener = new FayeClient.Listener() {
        @Override
        public void onConnectedToServer(FayeClient fc) {
            Log.i(LOG_TAG, "Connect to server");
            for(String channel : channels) {
                fc.subscribeToChannel(channel);
            }
        }

        @Override
        public void onDisconnectedFromServer(FayeClient fc) {
            Log.i(LOG_TAG, "Disconnected form server");
        }

        @Override
        public void onMessageReceived(FayeClient fc, String msg) {
            Log.i(LOG_TAG, "Message from server: " + msg);
            for(Listener listener : listeners) {
                listener.onMessageReceived(fc, msg);
            }
        }
    };

    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class FayeServiceBinder extends Binder {
        public FayeService getService() {
            return FayeService.this;
        }

        public FayeClient getClient() {
            return fayeClient;
        }
    }

    /* Interface */
    public interface Listener {
        public void onMessageReceived(FayeClient fc, String msg);
    }

}
