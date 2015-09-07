package com.elirex.fayeclient;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Sheng-Yuan Wang (2015/9/4).
 */
public class FayeServiceConnection implements ServiceConnection{

    private static final String LOG_TAG =
            FayeServiceConnection.class.getSimpleName();

    private FayeService service;
    private FayeClient client;
    private FayeServiceListener listener;

    public FayeServiceConnection(FayeServiceListener listener) {
       this.listener = listener;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        FayeService.FayeServiceBinder binder = ((FayeService.FayeServiceBinder) iBinder);
        service = binder.getService();
        client = binder.getClient();
        service.addListener(listener);
        Log.i(LOG_TAG, "Faye Service connected.");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
       if(service != null)  {
           service.removeListener(listener);
           service = null;
           Log.i(LOG_TAG, "Faye service disconnected.");
       }
    }

    public FayeClient getClient() {
        return client;
    }

}
