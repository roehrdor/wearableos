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

	// =====================================================================
	// 
	// Function calls forward to UserApp object  
	//
	// -------------------------------------------------------------
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

	
	// =====================================================================
	// 
	// Function calls forward to Sensor object  
	//
	// -------------------------------------------------------------
	//
	// Calls to Sensor, sid represents the unique ID of the object 
	//
	@Override
	public boolean SENSORS_SENSOR_isEnabled(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void SENSORS_SENSOR_setEnabled(int sid, boolean isEnabled)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String SENSORS_SENSOR_getDisplayedSensorName(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void SENSORS_SENSOR_setDisplayedSensorName(int sid,
			String displayedSensorName) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getSampleRate(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setSampleRate(int sid, int sampleRate)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getSavePeriod(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setSavePeriod(int sid, int savePeriod)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float SENSORS_SENSOR_getSmoothness(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setSmoothness(int sid, float smoothness)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getSensorType(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setSensorType(int sid, int sensorType)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getGraphType(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setGraphType(int sid, int graphType)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getRawDataMeasurementUnit(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setRawDataMeasurementUnit(int sid,
			int rawDataMeasurementUnit) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getRawDataMeasurementSystem(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setRawDataMeasurementSystem(int sid,
			int rawDataMeasurementSystem) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setDisplayedMeasurementUnit(int sid,
			int displayedMeasurementUnit) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void SENSORS_SENSOR_setDisplayedMeasurementSystem(int sid,
			int displayedMeasurementSystem) throws RemoteException {
		// TODO Auto-generated method stub
		
	}	
}
