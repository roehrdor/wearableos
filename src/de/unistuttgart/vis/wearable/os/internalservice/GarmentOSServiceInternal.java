/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.internalservice;

import de.unistuttgart.vis.wearable.os.internalapi.IGarmentInternalAPI;

/**
 * This service shall be bound by the settings app only.
 * 
 * @author roehrdor
 */
public class GarmentOSServiceInternal extends android.app.Service {
	public static android.content.Context context;
	
	//
	// This objects contains and implements all the call able functions called
	// from the internal API Interface
	//
	protected IGarmentInternalAPI.Stub APIInternalBinder = new APIInternalBinder();
	
	
	// =========================================================
	// Getter and setter functions
	// =========================================================
	/**
	 * <p>
	 * This functions returns the context of the
	 * </p>
	 * 
	 * @return
	 */
	public static android.content.Context getContext() {
		return context;
	}

	

	// =========================================================
	// Overriden functions
	// =========================================================
	@Override
	public int onStartCommand(android.content.Intent intent, int flags,
			int startId) {
		return START_STICKY;
	}

	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		if (IGarmentInternalAPI.class.getName().equals(intent.getAction()))
			return this.APIInternalBinder;
		else
			return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (context == null)
			context = getApplicationContext();			
	}
}
