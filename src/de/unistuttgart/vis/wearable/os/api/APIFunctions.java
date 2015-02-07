package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;

/**
 * This class provides of all the available function that can be called
 * from any application that has bound its connection to the service.
 * 
 * @author roehrdor
 */
public class APIFunctions {	
	public static long getTime() {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().currentTime();
			} catch(android.os.RemoteException e) {}
		}
		return 0xFFFFFFFFFFl;
	}
	
	// =============================================================================
	//
	// Public SDK Functions
	//
	// =============================================================================
}
