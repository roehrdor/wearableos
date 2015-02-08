/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;

/**
 * <p>
 * Internal API Functions that are used to modify settings in the settings app
 * or to change permissions for selected applications. These functions are only
 * available to the Garment OS settings application since other application
 * shall not be granted to do this kind of modifications. To see real SDK API
 * functions have a look at the
 * {@link de.unistuttgart.vis.wearable.os.api.APIFunctions} class.
 * </p>
 * <p>
 * Note 1: Since the implementation of any of these functions do not differ from
 * each other these are no comments added. Each of the following functions works
 * like this. First check whether the connection to the service has been
 * successfully established. If this is not the case a RuntimeExpcetion will be
 * thrown. Otherwise the function call will be redirected to the corresponding
 * handle created in {@link APIHandle} and the result in case of a non-void
 * function will be returned to the caller.
 * </p>
 * <p>
 * Note 2: These functions will be executed in the application and not the
 * service but will make the service functions being called.
 * </p>
 * <p>
 * Note 3: Functions will not execute asynchronously by default. To get this
 * kind of behavior the caller must call the function in a separate Thread.
 * </p>
 * 
 * @author roehrdor
 */
public class APIFunctions {
	// =============================================================================
	//
	// Private SDK Functions
	// These functions will not be included in the SDK for the Garment OS
	// library.
	// These functions are needed to provide several functionalities that shall
	// only be done by using the provided Settings Application.
	//
	// =============================================================================

	public static int[] PRIVACY_USERAPP_getProhibitedSensors(int oid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_getProhibitedSensors(oid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_sensorProhibited(int oid, int id) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_sensorProhibited(oid, id);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_grantPermission(int oid, int id) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_grantPermission(oid, id);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_revokePermission(int oid, int id) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_revokePermission(oid, id);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_denySensorType(int oid, int flag) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_denySensorType(oid, flag);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_allowSensorType(int oid, int flag) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_allowSensorType(oid, flag);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_sensorTypeGranted(int oid, int flag) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_sensorTypeGranted(oid, flag);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static void PRIVACY_USERAPP_grantActivityRecognition(int oid) {
		if (APIHandle.isServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_grantActivityRecognition(oid);
				return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static void PRIVACY_USERAPP_denyActivityRecognition(int oid) {
		if (APIHandle.isServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_denyActivityRecognition(oid);
				return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_activityRecognitionGranted(int oid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle()
						.PRIVACY_USERAPP_activityRecognitionGranted(oid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}
}
