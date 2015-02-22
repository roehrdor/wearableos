/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.internalservice;

import android.os.RemoteException;

import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.internalapi.IGarmentInternalAPI;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.PSensorData;
import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;
import de.unistuttgart.vis.wearable.os.privacy.PrivacyManager;
import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.sensors.*;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

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
    public void API_addNewSensor(int sampleRate, int savePeriod, int smoothness, String displayedSensorName,
                                 int sensorType, String bluetoothID, int rawDataMeasurementSystem,
                                 int rawDataMeasurementUnit, int displayedMeasurementSystem, int displayedMeasurementUnit) {
        new Sensor(null, sampleRate, savePeriod, smoothness, displayedSensorName, SensorType.values()[sensorType], bluetoothID, MeasurementSystems.values()[rawDataMeasurementSystem],
                MeasurementUnits.values()[rawDataMeasurementUnit], MeasurementSystems.values()[displayedMeasurementSystem], MeasurementUnits.values()[displayedMeasurementUnit]);
    }

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

    @Override
    public String[] API_getSensorNames() throws RemoteException {
        return SensorManager.getSensorNames();
    }

    @Override
    public PSensor[] API_getAllSensors() throws RemoteException {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors();
        PSensor[] psensors = new PSensor[sensors.size()];
        int i = -1;
        for(Sensor s : sensors)
            psensors[++i] = s.toParcelable();
        return psensors;
    }

    @Override
    public PSensor API_getSensorById(int id) throws RemoteException {
        return SensorManager.getSensorByID(id).toParcelable();
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
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return false;
        return sensor.isEnabled();
	}

	@Override
	public void SENSORS_SENSOR_setEnabled(int sid, boolean isEnabled)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setEnabled(isEnabled);
	}

	@Override
	public String SENSORS_SENSOR_getDisplayedSensorName(int sid)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return sensor.getDisplayedSensorName();
	}

	@Override
	public void SENSORS_SENSOR_setDisplayedSensorName(int sid,
			String displayedSensorName) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setDisplayedSensorName(displayedSensorName);
	}

	@Override
	public int SENSORS_SENSOR_getSampleRate(int sid) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        return sensor.getSampleRate();
	}

	@Override
	public void SENSORS_SENSOR_setSampleRate(int sid, int sampleRate)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSampleRate(sampleRate);
	}

	@Override
	public int SENSORS_SENSOR_getSavePeriod(int sid) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        return sensor.getSavePeriod();
	}

	@Override
	public void SENSORS_SENSOR_setSavePeriod(int sid, int savePeriod)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSavePeriod(savePeriod);
	}

	@Override
	public float SENSORS_SENSOR_getSmoothness(int sid) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        return sensor.getSmoothness();
	}

	@Override
	public void SENSORS_SENSOR_setSmoothness(int sid, float smoothness)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSmoothness(smoothness);
	}

	@Override
	public int SENSORS_SENSOR_getSensorType(int sid) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getSensorType() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getSensorType().ordinal();
	}

	@Override
	public void SENSORS_SENSOR_setSensorType(int sid, int sensorType)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSensorType(SensorType.values()[sensorType]);
	}

	@Override
	public int SENSORS_SENSOR_getGraphType(int sid) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getGraphType() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getGraphType().ordinal();
	}

	@Override
	public void SENSORS_SENSOR_setGraphType(int sid, int graphType)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setGraphType(GraphType.values()[graphType]);
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getDisplayedMeasurementUnit() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getDisplayedMeasurementUnit().ordinal();
	}

	@Override
	public void SENSORS_SENSOR_setDisplayedMeasurementUnit(int sid,
			int displayedMeasurementUnit) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setDisplayedMeasurementUnit(MeasurementUnits.values()[displayedMeasurementUnit]);
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid)
			throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getDisplayedMeasurementSystem() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getDisplayedMeasurementSystem().ordinal();
	}

	@Override
	public void SENSORS_SENSOR_setDisplayedMeasurementSystem(int sid,
			int displayedMeasurementSystem) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setDisplayedMeasurementSystem(MeasurementSystems.values()[displayedMeasurementSystem]);
	}

    @Override
    public PSensorData SENSORS_SENSOR_getRawData(int sid) {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData().clone());
    }

    @Override
    public PSensorData SENSORS_SENSOR_getRawDataIB(int sid, int time, boolean plusMinusOneSecond) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(Utils.unixToDate(time), plusMinusOneSecond).clone());
    }

    @Override
    public PSensorData SENSORS_SENSOR_getRawDataII(int sid, int start, int end) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(Utils.unixToDate(start), Utils.unixToDate(end)).clone());
    }
}
