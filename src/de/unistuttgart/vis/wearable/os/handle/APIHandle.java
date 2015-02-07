package de.unistuttgart.vis.wearable.os.handle;

import android.content.ComponentName;
import android.os.IBinder;
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
	public static IGarmentAPI garmentAPIHandle;
	public static IGarmentInternalAPI garmentInternalAPIHandle;
	
	private static android.content.Intent garmentAPIIntent = new android.content.Intent(IGarmentAPI.class.getName());
	private static android.content.Intent garmentInternalAPIIntent = new android.content.Intent(IGarmentInternalAPI.class.getName());
	
	public static boolean serviceBound = false;
	public static boolean serviceInternalBound = false;	
	
	
	
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
	@Override
	public void onCreate() {
		super.onCreate();
		
		//
		// In case we are not connected to the public service
		//
		if(!serviceBound) {		
			//
			// Bind the public service
			//
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
		
	};
	
	
}
