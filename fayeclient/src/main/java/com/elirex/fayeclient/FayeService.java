package com.elirex.fayeclient;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
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

    FayeServiceBinder binder;

    private FayeClient fayeClient;
    private ArrayList<FayeServiceListener> listeners = new ArrayList<FayeServiceListener>();

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new FayeServiceBinder();
        startFayeClient();
        Log.i(LOG_TAG, "Faye Service Starts: " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFayeClient();
        Log.i(LOG_TAG, "Faye Service Stopped: " + this);
    }

    public static void initFayeService(String host, int port, String path,
                                String accessToken, String authToken) {
        initFayeService(host, port, path);
        ACCESS_TOKEN = accessToken;
        AUTH_TOKEN = authToken;
    }

    public static void initFayeService(String host, int port, String path, String... channel) {
        initFayeService(host, port, path);
        if(channels == null) {
            channels = new HashSet<String>();
        }
        channels.addAll(Arrays.asList(channel));
    }

    public static void initFayeService(String host, int port, String path) {
        SERVER_HOST = host;
        SERVER_PORT = String.valueOf(port);
        SERVER_PATH = path;
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

    public static ServiceConnection bind(Context context,
                                         final FayeServiceListener listener) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if(FayeService.class.getName()
                    .equals(service.service.getClassName())) {
                break;
            }
        }
        /*
        ServiceConnection connection = new ServiceConnection() {

            private FayeService service;
            private FayeClient client;

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if(service != null) {

                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }

            public FayeClient getClient() {
                return client;
            }
        };
        */
        FayeServiceConnection connection = new FayeServiceConnection(listener);
        context.bindService(new Intent(context, FayeService.class),
                connection, Context.BIND_AUTO_CREATE);
        return connection;
    }

    public static void unbind(Context context, ServiceConnection connection) {
        connection.onServiceDisconnected(null);
        context.unbindService(connection);
    }

    private FayeClientListener fayeClientListener = new FayeClientListener() {
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
            fc.connectToServer();
        }

        @Override
        public void onMessageReceived(FayeClient fc, String msg) {
            Log.i(LOG_TAG, "Message from server: " + msg);
            for(FayeServiceListener listener : listeners) {
                listener.onMessageReceived(fc, msg);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        if(binder == null) {
            binder = new FayeServiceBinder();
        }
        return binder;
    }

    protected void addListener(FayeServiceListener listener) {
        listeners.add(listener);
    }

    protected void removeListener(FayeServiceListener listener) {
        listeners.remove(listener);
        if(listeners.size() == 0) {
            stopSelf();
        }
    }

    private void startFayeClient() {
        fayeClient = new FayeClient(SERVER_HOST + ":" + SERVER_PORT
                + SERVER_PATH, AUTH_TOKEN, ACCESS_TOKEN);
        for(String channel : channels) {
            fayeClient.addChannel(channel);
        }
        fayeClient.setListener(fayeClientListener);
        fayeClient.connectToServer();
    }

    private void stopFayeClient() {
        HandlerThread thread = new HandlerThread("FayeTerminateHandlerThread");
        thread.start();
        new Handler(thread.getLooper()).post(new Runnable() {

            @Override
            public void run() {
                if (fayeClient.isWebsocketConnected()) {
                    fayeClient.disconnectFromServer();
                    channels.clear();
                }

            }
        });
    }

    public class FayeServiceBinder extends Binder {
        public FayeService getService() {
            return FayeService.this;
        }

        public FayeClient getClient() {
            return fayeClient;
        }
    }

}
