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
    private static final String KEY_SUBSCRIPTION = "subscription";
    private static final String KEY_CONNECTION_TYPE = "connectionType";
    private static final String KEY_DATA = "data";
    private static final String KEY_EXT = "ext";
    private static final String KEY_ID = "id";

    private static final String HANDSHAKE_CHANNEL = "/meta/handshake";
    private static final String CONNECT_CHANNEL = "/meta/connect";
    private static final String DISCONNECT_CHANNEL = "/meta/disconnect";
    private static final String SUBSCRIBE_CHANNEL = "/meta/subscribe";
    private static final String UNSUBSCRIBE_CHANNEL = "/meta/unsubscribe";

    private String mVersion = "1.0";
    private String mMinimumVersion = "1.0beta";
    private String mClientId;
    private JSONArray mSupportConnectionTypes;
    private String mConnectionType = "long-polling";

    private String mHandShakeExt;
    private String mHandShakeId;
    private String mConnectExt;
    private String mConnectId;
    private String mDisconnectExt;
    private String mDisconnectId;
    private String mSubscribeExt;
    private String mSubscribeId;
    private String mUnsubscribeExt;
    private String mUnsubscribeId;
    private String mPublishExt;
    private String mPublishId;

    // HandShark
    public void setSupportConnectionTypes(String... support) {
        JSONArray arr = null;
        if(support.length != 0) {
            arr = new JSONArray();
            for(String type : support) {
                arr.put(type);
            }
            mSupportConnectionTypes = arr;
        }
    }

    public String handShake() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, HANDSHAKE_CHANNEL)
                .put(KEY_MIN_VERSION, mMinimumVersion)
                .put(KEY_VERSION, mVersion);
        if(mSupportConnectionTypes == null) {
            mSupportConnectionTypes = new JSONArray();
            mSupportConnectionTypes.put("long-polling");
            mSupportConnectionTypes.put("callback-polling");
            mSupportConnectionTypes.put("iframe");
            mSupportConnectionTypes.put("websocket");
        }
        json.put(KEY_SUPPORT_CONNECTION_TYPES, mSupportConnectionTypes);
        if(mHandShakeExt != null) {
            JSONObject obj = isJSONObject(mHandShakeExt);
            JSONArray arr = isJSONArray(mHandShakeExt);
            if(obj != null) {
                json.put(KEY_EXT, obj);
            } else if(arr != null) {
                json.put(KEY_EXT, arr);
            } else {
                json.put(KEY_EXT, mHandShakeExt);
            }
        }

        if(mHandShakeId != null) {
            JSONObject obj = isJSONObject(mHandShakeId);
            JSONArray arr = isJSONArray(mHandShakeId);
            if(obj != null) {
                json.put(KEY_ID, obj);
            } else if(arr != null) {
                json.put(KEY_ID, arr);
            } else {
                json.put(KEY_ID, mHandShakeId);
            }
        }
        return json.toString();
    }

    public void setHandshakeExt(String ext) {
        mHandShakeExt = ext;
    }

    public void setHandShakeId(String id) {
        mHandShakeId = id;
    }

    // Connect
    public void setClient(String id) {
       mClientId = id;
    }

    public void setConnectionType(String type) {
        mConnectionType = type;
    }

    public void setConnectExt(String ext) {
        mConnectExt = ext;
    }

    public void setConnectId(String id) {
        mConnectId = id;
    }

    public String connect() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, CONNECT_CHANNEL)
                .put(KEY_CLIENT_ID, mClientId)
                .put(KEY_CONNECTION_TYPE, mConnectionType);

        if(mConnectExt != null) {
            JSONObject obj = isJSONObject(mConnectExt);
            JSONArray arr = isJSONArray(mConnectExt);
            if (obj != null) {
                json.put(KEY_EXT, obj);
            } else if (arr != null) {
                json.put(KEY_EXT, arr);
            } else {
                json.put(KEY_EXT, mConnectExt);
            }
        }
        if(mConnectId != null) {
            JSONObject obj = isJSONObject(mConnectId);
            JSONArray arr = isJSONArray(mConnectId);
            if(obj != null) {
                json.put(KEY_ID, obj);
            } else if(arr != null) {
                json.put(KEY_ID, arr);
            } else {
                json.put(KEY_ID, mConnectId);
            }
        }
        return json.toString();
    }

    // Disconnect
    public void setDisconnectExt(String ext) {
       mDisconnectExt = ext;
    }

    public void setDisconnectId(String id) {
        mDisconnectId = id;
    }

    public String disconnect() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, DISCONNECT_CHANNEL)
                .put(KEY_CLIENT_ID, mClientId);


        if(mDisconnectExt != null) {
            JSONObject obj = isJSONObject(mDisconnectExt);
            JSONArray arr = isJSONArray(mDisconnectExt);
            if(obj != null) {
                json.put(KEY_EXT, obj);
            } else if(arr != null) {
                json.put(KEY_EXT, arr);
            } else {
                json.put(KEY_EXT, mDisconnectExt);
            }
        }

        if(mDisconnectId != null) {
            JSONObject obj = isJSONObject(mDisconnectId);
            JSONArray arr = isJSONArray(mDisconnectId);
            if(obj != null) {
                json.put(KEY_ID, obj);
            } else if(arr != null) {
                json.put(KEY_ID, arr);
            } else {
                json.put(KEY_ID, mDisconnectId);
            }
        }
        return json.toString();
    }

    // Subscribe
    public void setSubscribeExt(String ext) {
        mSubscribeExt = ext;
    }

    public void setSubscribeId(String id) {
        mSubscribeId = id;
    }

    public String subscribe(String subscription) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, SUBSCRIBE_CHANNEL)
                .put(KEY_CLIENT_ID, mClientId)
                .put(KEY_SUBSCRIPTION, subscription);

        if(mSubscribeExt != null) {
            JSONObject obj = isJSONObject(mSubscribeExt);
            JSONArray arr = isJSONArray(mSubscribeExt);
            if(obj != null) {
                json.put(KEY_EXT, obj);
            } else if(arr != null) {
                json.put(KEY_EXT, arr);
            } else {
                json.put(KEY_EXT, mSubscribeExt);
            }
        }

        if(mSubscribeId != null) {
            JSONObject obj = isJSONObject(mSubscribeId);
            JSONArray arr = isJSONArray(mSubscribeId);
            if(obj != null) {
                json.put(KEY_ID, obj);
            } else if(arr != null) {
                json.put(KEY_ID, arr);
            } else {
                json.put(KEY_ID, mSubscribeId);
            }
        }

        return json.toString();
    }

    // Unsubscribe
    public void setUnsubscribeExt(String ext) {
        mUnsubscribeExt = ext;
    }

    public void setUnsubscribeId(String id) {
        mUnsubscribeId = id;
    }

    public String unsubscribe(String subscription) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, UNSUBSCRIBE_CHANNEL)
                .put(KEY_CLIENT_ID, mClientId)
                .put(KEY_SUBSCRIPTION, subscription);

        if (mUnsubscribeExt != null) {
            JSONObject obj = isJSONObject(mUnsubscribeExt);
            JSONArray arr = isJSONArray(mUnsubscribeExt);
            if(obj != null) {
                json.put(KEY_EXT, obj);
            } else if(arr != null) {
                json.put(KEY_EXT, arr);
            } else {
                json.put(KEY_EXT, mUnsubscribeExt);
            }
        }
        if (mUnsubscribeId != null) {
            JSONObject obj = isJSONObject(mUnsubscribeId);
            JSONArray arr = isJSONArray(mUnsubscribeId);
            if(obj != null) {
                json.put(KEY_ID, obj);
            } else if (arr != null) {
                json.put(KEY_ID, arr);
            } else {
                json.put(KEY_ID, mUnsubscribeId);
            }
        }
        return json.toString();
    }

    public void setAllExt(String ext) {
         mHandShakeExt = ext;
         mConnectExt = ext;
         mDisconnectExt = ext;
         mSubscribeExt = ext;
         mUnsubscribeExt = ext;
    }

    public void  setAllId(String id) {
        mHandShakeId = id;
        mConnectId = id;
        mDisconnectId = id;
        mSubscribeId = id;
        mUnsubscribeId = id;
    }

    private JSONObject isJSONObject(String context) {
        if(context.startsWith("{")) {
            try {
                return new JSONObject(context);
            } catch (JSONException e) {
                return null;
            }

        }
        return null;
    }

    private JSONArray isJSONArray(String content) {
        if(content.startsWith("[")) {
            try {
                return new JSONArray(content);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    // Publish
    public void setPublishExt(String ext) {
        mPublishExt = ext;
    }

    public void setPublishId(String id) {
        mPublishId = id;
    }

    public String publish(String channel, String data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_CHANNEL, channel)
                .put(KEY_DATA, data);

        if(mClientId != null) {
            json.put(KEY_CLIENT_ID, mClientId);
        }

        if(mPublishExt != null) {
            json.put(KEY_EXT, mPublishExt);
        }

        if(mPublishId != null) {
            json.put(KEY_ID, mPublishId);
        }
        return json.toString();
    }

}
