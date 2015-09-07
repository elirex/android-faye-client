package com.elirex.fayeclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sheng-Yuan Wang (2015/9/7).
 */
public class MetaMessage {

    private static final String KEY_CHANNEL = "channel";
    private static final String KEY_VERSION = "version";
    private static final String KEY_MIN_VERSION = "minimumVersion";
    private static final String KEY_SUPPORT_CONNECTION_TYPES = "supportedConnectionTypes";
    private static final String KEY_CLIENT_ID = "clientId";
    private static final String KEY_SUBSCRIPTION = "subscriptoin";
    private static final String KEY_CONNECTION_TYPE = "connectionType";
    private static final String KEY_DATA = "data";
    private static final String KEY_EXT = "ext";
    private static final String KEY_ID = "id";

    private static final String HANDSHAKE_CHANNEL = "/meta/handshake";
    private static final String CONNECT_CHANNEL = "/meta/connect";
    private static final String DISCONNECT_CHANNEL = "/meta/disconnect";
    private static final String SUBSCRIBE_CHANNEL = "/meta/subscribe";
    private static final String UNSUBSCRIBE_CHANNEL = "/meta/unsubscribe";
    private static final String PUBLISH_CHANNEL = "";


    private String version = "1.0";
    private String minimumVersion = "1.0beta";
    private String clientId;
    private String supportConnectionTypes = "[\"long-polling\",\"callback-polling\",\"iframe\",\"websocket\"]";
    private String connectionType = "long-polling";


    private String handShakeExt = "";
    private String handShakeId = "";
    private String connectExt = "";
    private String connectId = "";
    private String disconnectExt = "";
    private String disconnectId = "";
    private String subscribeExt = "";
    private String subscribeId = "";

    // HandShark
    public void setSupportConnectionTypes(String... support) {
        JSONArray arr = null;
        if(support.length != 0) {
            arr = new JSONArray();
            for(String type : support) {
                arr.put(type);
            }
            supportConnectionTypes = arr.toString();
        }
    }

    public String handShake() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, HANDSHAKE_CHANNEL)
                .put(KEY_VERSION, minimumVersion)
                .put(KEY_VERSION, version);
        if(handShakeExt != null) {
            json.put(KEY_EXT, handShakeExt);
        }
        if(handShakeId != null) {
            json.put(KEY_ID, handShakeId);
        }
        return json.toString();
    }

    public void setHandshakeExt(String ext) {
        handShakeExt = ext;
    }

    public void setHandShakeId(String id) {
        handShakeId = id;
    }

    // Connect
    public void setClient(String id) {
       clientId = id;
    }

    public void setConnectionType(String type) {
        connectionType = type;
    }

    public void setConnectExt(String ext) {
        connectExt = ext;
    }

    public void setConnectId(String id) {
        connectId = id;
    }

    public String connect() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, CONNECT_CHANNEL)
                .put(KEY_CLIENT_ID, clientId)
                .put(KEY_CONNECTION_TYPE, connectionType);

        if(connectExt != null) {
            json.put(KEY_EXT, connectExt);
        }
        if(connectId != null) {
            json.put(KEY_ID, connectId);
        }
        return json.toString();
    }

    // Disconnect
    public void setDisconnectExt(String ext) {
       disconnectExt = ext;
    }

    public void setDisconnectId(String id) {
        disconnectId = id;
    }

    public String disconnectExt(String ext) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, DISCONNECT_CHANNEL)
                .put(KEY_CLIENT_ID, clientId);

        if(disconnectExt != null) {
            json.put(KEY_EXT, disconnectExt);
        }

        if(disconnectId != null) {
            json.put(KEY_ID, disconnectId);
        }
        return json.toString();
    }

    // Subscribe
    public void setSubscribeExt(String ext) {
        subscribeExt = ext;
    }

    public void setSubscribeId(String id) {
        subscribeId = id;
    }

    public String subscribe(String subscription) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, SUBSCRIBE_CHANNEL);
        return json.toString();

    }

}
