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
import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;
import de.unistuttgart.vis.wearable.os.privacy.PrivacyManager;
import de.unistuttgart.vis.wearable.os.privacy.UserApp;

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

	@Override
	public String[] API_getRegisteredApplications() throws RemoteException {
		return PrivacyManager.instance.getAllAppNames();
	}

	@Override
	public PUserApp[] API_getRegisteredUserApplications()
			throws RemoteException {
		UserApp[] aua = PrivacyManager.instance.getAllApps();
		PUserApp[] apua = new PUserApp[aua.length];
		for(int i = 0; i != apua.length; ++i) {
			apua[i] = aua[i].toParcelable();			
		}		
		return apua; 				
	}

	@Override
	public PUserApp API_getRegisteredUserAppByName(String name)
			throws RemoteException {
		return PrivacyManager.instance.getApp(name).toParcelable();
	}

	//
	// Calls to UserApp, oid represents the unique ID of the object 
	//
	@Override
	public boolean PRIVACY_USERAPP_sensorProhibited(int oid, int id)
			throws RemoteException {
		return PrivacyManager.instance.getApp(oid).sensorProhibited(id);
	}

	@Override
	public boolean PRIVACY_USERAPP_grantPermission(int oid, int id)
			throws RemoteException {
		return PrivacyManager.instance.getApp(oid).grantPermission(id);
	}

	@Override
	public boolean PRIVACY_USERAPP_revokePermission(int oid, int id)
			throws RemoteException {
		return PrivacyManager.instance.getApp(oid).revokePermission(id);
	}

	@Override
	public boolean PRIVACY_USERAPP_denySensorType(int oid, int flag)
			throws RemoteException {
		return PrivacyManager.instance.getApp(oid).denySensorType(flag);
	}

	@Override
	public boolean PRIVACY_USERAPP_allowSensorType(int oid, int flag)
			throws RemoteException {
		return PrivacyManager.instance.getApp(oid).allowSensorType(flag);
	}

	@Override
	public boolean PRIVACY_USERAPP_sensorTypeGranted(int oid, int flag)
			throws RemoteException {
		return PrivacyManager.instance.getApp(oid).sensorTypeGranted(flag);
	}

	@Override
	public void PRIVACY_USERAPP_grantActivityRecognition(int oid)
			throws RemoteException {
		PrivacyManager.instance.getApp(oid).grantActivityRecognition();
	}

	@Override
	public void PRIVACY_USERAPP_denyActivityRecognition(int oid)
			throws RemoteException {
		PrivacyManager.instance.getApp(oid).denyActivityRecognition();
	}

	@Override
	public boolean PRIVACY_USERAPP_activityRecognitionGranted(int oid)
			throws RemoteException {
		return PrivacyManager.instance.getApp(oid).activityRecognitionGranted();
	}


}
