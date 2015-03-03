/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.api;

import android.util.Log;
import de.unistuttgart.vis.wearable.os.service.CallbackNode;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import android.os.Message;

/**
 * This class is being used as callback Handler for the Garment OS Service.
 * 
 * @author roehrdor
 */
public class CallbackHandler extends android.os.Handler {
	//
	// Reference to the list of call back nodes
	//
	private final android.os.RemoteCallbackList<CallbackNode> mCallbacks;
	private int mValue = 0;
	
	public CallbackHandler(android.os.RemoteCallbackList<CallbackNode> callbackList) {
		this.mCallbacks = callbackList;
	}
	
	@Override
	public void handleMessage(Message msg) {
		//
		// msg.what specifies the reason for handling this message
		// - CALLBACk means that we need to start a broadcast to all bound
		//		applications and then selecting those who registered for 
		//		the callback that has been triggered
		//
		switch (msg.what) {

			//
			// A callback event has been triggered and detected so we might
			// need to calback a bound application
			// - msg.arg1 will specify the the reason for the callback, the
			//		detected event
			// - msg.obj will be sent back to the application. In case this obj
			// 		is an instance of a {@link BaseCallbackObject} this obj will
			// 		be sent directly otherwise it will be sent as attribute of
			// 		a newly created {@link CallbackObject} object.
			//
			case Constants.CALLBACK: {
				// start the broadcast
				final int N = mCallbacks.beginBroadcast();
				CallbackNode cachedNode = null;

				// Iterate over all elements in the callback list
				for (int i = 0; i != N; ++i) {
					try {
						// Check whether the application wants to receive this
						// callback
						if ((cachedNode = mCallbacks.getBroadcastItem(i)).isFlagSet(msg.arg1)) {

							// if instance of BaseCallbackObject send directly
							if (msg.obj instanceof BaseCallbackObject)cachedNode.getCallbackHandle().callback((BaseCallbackObject) msg.obj);

							// else sent it as part of a new instance of the
							// CallbackObject class
							else
								cachedNode.getCallbackHandle().callback(new CallBackObject(msg.obj));
						}
					} catch (android.os.RemoteException e) {
						// do not handle this exception, this one will be
						// thrown e.g. if an application can no longer receive
						// a broadcast (closing without unregistering)
					}
				}

				// finish broadcasts
				mCallbacks.finishBroadcast();
				break;
			}
			default:
				super.handleMessage(msg);
		}
	}
}
