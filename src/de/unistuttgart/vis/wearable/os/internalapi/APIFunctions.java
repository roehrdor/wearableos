package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;

public class APIFunctions {
	// =============================================================================
	//
	// Private SDK Functions
	//	These functions will not be included in the SDK for the Garment OS library. 
	// 	These functions are needed to provide several functionalities that shall
	//	only be done by using the provided Settings Application. 
	//
	// =============================================================================
	
	public static int[] 	PRIVACY_USERAPP_getProhibitedSensors(int oid) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_getProhibitedSensors(oid);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static boolean 	PRIVACY_USERAPP_sensorProhibited(int oid, int id) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_sensorProhibited(oid, id);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static boolean 	PRIVACY_USERAPP_grantPermission(int oid, int id) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_grantPermission(oid, id);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");	
	}
	
	public static boolean 	PRIVACY_USERAPP_revokePermission(int oid, int id) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_revokePermission(oid, id);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");	
	}
	
	public static boolean 	PRIVACY_USERAPP_denySensorType(int oid, int flag) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_denySensorType(oid, flag);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");	
	}
	
	public static boolean 	PRIVACY_USERAPP_allowSensorType(int oid, int flag) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_allowSensorType(oid, flag);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static boolean 	PRIVACY_USERAPP_sensorTypeGranted(int oid, int flag) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_sensorTypeGranted(oid, flag);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static void 		PRIVACY_USERAPP_grantActivityRecognition(int oid) {
		if(APIHandle.isServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_grantActivityRecognition(oid);
				return;
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static void 		PRIVACY_USERAPP_denyActivityRecognition(int oid) {
		if(APIHandle.isServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_denyActivityRecognition(oid);
				return;
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static boolean		PRIVACY_USERAPP_activityRecognitionGranted(int oid) {
		if(APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_activityRecognitionGranted(oid);
			} catch(android.os.RemoteException e) {}
		}
		throw new RuntimeException("Connection failed");
	}
}
