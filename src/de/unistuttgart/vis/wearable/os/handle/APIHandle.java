/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.handle;

import android.content.ComponentName;
import android.os.IBinder;
import android.os.RemoteException;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.IGarmentAPI;
import de.unistuttgart.vis.wearable.os.internalapi.IGarmentInternalAPI;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * Kernel API Handle that shall be implemented in all apps using the garment
 * sdk. This class tries to connect the application to the garment kernel
 * service allowing the {@link APIFunctions} class to call the kernel apis.
 * 
 * @author roehrdor
 */
public class APIHandle extends android.app.Application {
	//
	// static handles for the services and intents and booleans to determine
	// whether the service has already been bound or not
	//
	private static IGarmentAPI garmentAPIHandle;
	private static IGarmentInternalAPI garmentInternalAPIHandle;
	
	private static android.content.Intent garmentAPIIntent = new android.content.Intent(IGarmentAPI.class.getName());
	private static android.content.Intent garmentInternalAPIIntent = new android.content.Intent(IGarmentInternalAPI.class.getName());
	
	private static boolean serviceBound = false;
	private static boolean serviceInternalBound = false;

    //
    // The unique application package ID
    //
    private static String appPackageID = null;

    /**
     * Get the App package ID to determine the rights of the application
     * @return the app package id
     */
    public static String getAppPackageID() {return appPackageID;}
	
	
	// =========================================================
	//
	// Connection Objects
	//
	// =========================================================
	//
	// Static connection object for the public service. This object defines
	// what is to be done when successfully binding the public service
	//
	private static android.content.ServiceConnection serviceConnection = new android.content.ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			garmentAPIHandle = IGarmentAPI.Stub.asInterface(service);
            try {
                garmentAPIHandle.registerApp(appPackageID);
            } catch(RemoteException e) {
            }
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// No longer connected to the service
			garmentAPIHandle = null;
			serviceBound = false;
		}		
	};	
	
	//
	// Static connection object for the public service. This object defines
	// what is to be done when successfully binding the internal service
	//
	private static android.content.ServiceConnection serviceInternalConnection = new android.content.ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			garmentInternalAPIHandle = IGarmentInternalAPI.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// No longer connected to the internal service
			garmentInternalAPIHandle = null;
			serviceInternalBound = false;
		}	
	};

	
	
	
	
	// =========================================================
	//
	// Functions
	//
	// =========================================================
	/**
	 * Get the garment API Handle to make API function calls
	 * 
	 * @return the garment API Handle
	 */
	public static IGarmentAPI getGarmentAPIHandle() {
		return garmentAPIHandle;
	}
	
	/**
	 * Get the internal garment API Handle to make internal API function calls
	 * 
	 * @return the garment API Handle for internal function calls
	 */
	public static IGarmentInternalAPI getGarmentInternalAPIHandle() {
		return garmentInternalAPIHandle;
	}
	
	/**
	 * Tests whether the service is bound.
	 * 
	 * @return true if the service is bound
	 */
	public static boolean isServiceBound() {
		return serviceBound && garmentAPIHandle != null;
	}
	
	/**
	 * Test whether the internal service is bound.
	 * 
	 * @return true if the internal service is bound
	 */
	public static boolean isInternalServiceBound() {
		return serviceInternalBound && garmentInternalAPIHandle != null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

        //
        // Get the unique Application package ID
        //
        appPackageID = getApplicationInfo().packageName;

		//
		// In case we are not connected to the public service
		//
		if(!serviceBound) {		
			//
			// Bind the public service
			//
			garmentAPIIntent.putExtra("AppProcess", appPackageID);
			serviceBound = bindService(Utils.explicitFromImplicit(getApplicationContext(), garmentAPIIntent), 
									serviceConnection, android.content.Context.BIND_AUTO_CREATE);			
		}
		
		//
		// In case we are not connected to the internal service
		//
		if(!serviceInternalBound) {
			//
			// Bind the internal service
			//
			serviceInternalBound = bindService(Utils.explicitFromImplicit(getApplicationContext(), garmentInternalAPIIntent), 
									serviceInternalConnection, android.content.Context.BIND_AUTO_CREATE);
		}
	}
}
