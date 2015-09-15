# Android Faye Client
> This is a simple Faye Client for android. It can publish-subscribe messaging between Faye server.

## Support
* SSL WebSocket connection
* Subscribe multic-channel
* Set the meta message

## Usage
#### Setup FayeClient
```java
// Initial Meta Message
MetaMessage meta = new MetaMessate();
// Initinal FayeClient
FayeClient mClient = new FayeClient("wws://fayesample.com/fayeservice, meta);

// Set FayeClient listener
mClient.setListener(new FayeClientListener() {
        @Override
        public void onConnectedServer(FayeClient fc) {
            Log.i(LOG_TAG, "Connected");
         }

        @Override
        public void onDisconnectedServer(FayeClient fc) {
            Log.i(LOG_TAG, "Disconnected");
        }

        @Override
        public void onReceivedMessage(FayeClient fc, String msg) {
            Log.i(LOG_TAG, "Message: " + msg);
        }
    });

// Connect to server
mClient.connectServer();;
```

#### Subscribe channel
You have two locations can subscribe channel.
1. Subscribe channel in the onConnectedServer(). If FayeClient calls onConnectedServer() method that mean is FayeClient is connected the Faye Server.
```java
mClient.setListener(new FayeClientListener() {
    @Override
    public void onConnectedServer(FayeClient fc) {
       Log.i(LOG_TAG, "Connected");
       fc.subscribeChannel("\channel-1");
       fc.subscribeChannel("\channel-2");
    }
    ...
});
```
2. Subscribe channe not in onConnectedServer(). Through this way, you must be check FayeClient is connected the Faye Server.

```java
if(mClient.isConnectedServer()) {
	mClient.subscribeChannel("\channel-3");
}
```

#### Publish message
```java
if(mClient.isConnectedServer()) {
	// Normal publish
	mClient.publish("/channel-1", "The sample message");

	// Include ext and id
	mClient.publich("/channel-2", "The message include ext and id", "{/"auth/": /"password/"}", "{/"user/":/"Tester/"}");
}
```

#### Unsubscribe channel
```java
// Unsubscribe one channel
mClient.unsubscribeChanne("/channel-1");

// Unsubscribe multi-channels
String channels[] = {"/channel-5", "/channel-2", "channel-4"};
mClient.unsubscribeChannels(channels);

// Unsubscribe all channel
mClient.unsubscribeAll();
```

#### Disconnect server
```java
public void stopFayeClient() {
	HandlerThread thread = new HandlerThread("TerminateThread");
	thread.start();
	new Handler(thread.getLooper()).post(new Runnable() {
		@Override
		public void run() {
			if(mClient.isConnectedServer()) {
				mClient.disconnectServer();
			}
		}
	});
}
```

#### Setting Meta Messsage
You can set handshake, connect, disconnect ,subscribe and unsubscribe's "ext" and "id" fields.	
The Meta Message detail you can look [Bayeux protocol](http://svn.cometd.org/trunk/bayeux/bayeux.html).
```java
MetaMessage message = new MetaMessage();
String ext = "{/"key/":/"sjaklfjdalijiejlaijfdlkj/"}";
String id = "{/"user/":/"Tester/"}";
// Set handshake's ext and id
message.setHandshakeExt(ext);
message.setHandshakeId(id);
// Set handshake's supportConnectionTypes field
// The supportConnectionTypes default include "long-polling", "callback-polling", "websocket" and "iframe".
String types[] = {"long-polling", callback-polling};
message.setSupportConnectionTypes(types);

// Set connect's ext and id
message.setConnectExt(ext);
message.setConnectId(id);

// Set disconnect's ext and id
message.setDisconnectExt(ext);
message.setDisconnectId(id);

// Set subscribe's ext and id
message.setSubscribeExt(ext);
message.setSubscribeId(id);

// Set unsubscribe's ext and id
message.setUnsubscribeExt(ext);
message.setUnsubscribeId(id);

// Set all ext and id, at once
message.setAllExt(ext);
message.setAllId(id);
```

## References
* [Faye](https://github.com/faye/faye) (github repo)
* [Bayeux Protocol](http://svn.cometd.org/trunk/bayeux/bayeux.html)
* [Android-Faye-Client](http://github.com/saulpower/Android-Faye-Client) (saulpower's github repo)
* [android-faye-client](https://code.google.com/p/android-faye-client) (Google code)

## License
```
   Copyright 2015 Sheng-Yuan, Wang (Elirex)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
