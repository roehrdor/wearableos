/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.internalservice;

import android.os.RemoteException;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.internalapi.IGarmentInternalAPI;

/**
 * <p>
 * This class implements the functions provided by the internal SDK. These
 * functions will be called from the corresponding handle created in
 * {@link APIHandle}.
 * </p>
 * <p>
 * Note these functions will be executed in the service
 * </p> 
 * 
 * @author roehrdor
 */
public class APIInternalBinder extends IGarmentInternalAPI.Stub {

	//
	// Internal API Function call
	//
	@Override
	public String[] API_getRegisteredApplications() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	//
	// Calls to UserApp, oid represents the unique ID of the object 
	//
	@Override
	public int[] PRIVACY_USERAPP_getProhibitedSensors(int oid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean PRIVACY_USERAPP_sensorProhibited(int oid, int id)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean PRIVACY_USERAPP_grantPermission(int oid, int id)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean PRIVACY_USERAPP_revokePermission(int oid, int id)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean PRIVACY_USERAPP_denySensorType(int oid, int flag)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean PRIVACY_USERAPP_allowSensorType(int oid, int flag)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean PRIVACY_USERAPP_sensorTypeGranted(int oid, int flag)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void PRIVACY_USERAPP_grantActivityRecognition(int oid)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PRIVACY_USERAPP_denyActivityRecognition(int oid)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean PRIVACY_USERAPP_activityRecognitionGranted(int oid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
	//
	// Functions will be here
	//
}
