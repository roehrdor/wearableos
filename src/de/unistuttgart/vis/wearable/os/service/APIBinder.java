/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.service;

import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.IGarmentAPI;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;

/**
 * This class implements the function calls passed through by the
 * {@link APIFunctions} class.
 * 
 * @author roehrdor
 */
class APIBinder extends IGarmentAPI.Stub {

	/**
	 * Returns the result from the function call
	 * {@link System#currentTimeMillis()}
	 * 
	 * @return the current time
	 */
	@Override
	public long currentTime() throws android.os.RemoteException {
		return System.currentTimeMillis();
	}

	/**
	 * Register a callback handle to the kernel and save it according to its pid
	 * and uid
	 *
	 * @param callback
	 *            the callback handle that might be called
	 * @param flag
	 *            the callback flag
	 */
	@Override
	public void registerCallback(IGarmentCallback callback, int flag)
			throws android.os.RemoteException {
		if (callback != null) {
			GarmentOSSerivce.mCallbacks.register(new CallbackNode(
					android.os.Binder.getCallingPid(), android.os.Binder
							.getCallingUid(), callback, flag));
		}
	}

	/**
	 * Delete the given calback handle from the kernel callback list
	 *
	 * @param callback
	 *            the callback handle to remove
	 * @param flag
	 *            the callback flag
	 */
	@Override
	public void unregisterCallback(IGarmentCallback callback, int flag)
			throws android.os.RemoteException {
		if (callback != null) {
			GarmentOSSerivce.mCallbacks.unregister(new CallbackNode(
					android.os.Binder.getCallingPid(), android.os.Binder
							.getCallingUid(), callback, flag));
		}
	}

	//
	// Functions will be here
	//
}
