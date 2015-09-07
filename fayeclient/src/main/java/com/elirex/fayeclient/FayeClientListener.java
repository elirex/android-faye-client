package com.elirex.fayeclient;

/**
 * @author Sheng-Yuan Wang (2015/9/7).
 */
public interface FayeClientListener {

    public void onConnectedToServer(FayeClient fc);
    public void onDisconnectedFromServer(FayeClient fc);
    public void onMessageReceived(FayeClient fc, String msg);

}
