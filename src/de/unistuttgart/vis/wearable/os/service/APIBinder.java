/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.service;

import android.os.RemoteException;
import de.unistuttgart.vis.wearable.os.api.*;
import de.unistuttgart.vis.wearable.os.privacy.PrivacyManager;
import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * This class implements the function calls passed through by the
 * {@link APIFunctions} class.
 * 
 * @author roehrdor
 */
class APIBinder extends IGarmentAPI.Stub {

	/**
	 * Returns the result from the function call
	 * {@link System#currentTimeMillis()}
	 * 
	 * @return the current time
	 */
	@Override
	public long currentTime() throws android.os.RemoteException {
		return System.currentTimeMillis();
	}

	/**
	 * Register a callback handle to the kernel and save it according to its pid
	 * and uid
	 *
	 * @param callback
	 *            the callback handle that might be called
	 * @param flag
	 *            the callback flag
	 */
	@Override
	public void registerCallback(String app, IGarmentCallback callback, int flag)
			throws android.os.RemoteException {
		if (callback != null) {
			de.unistuttgart.vis.wearable.os.service.GarmentOSService.mCallbacks.register(new CallbackNode(
					android.os.Binder.getCallingPid(), android.os.Binder
							.getCallingUid(), callback, flag));
		}
	}

	/**
	 * Delete the given calback handle from the kernel callback list
	 *
	 * @param callback
	 *            the callback handle to remove
	 * @param flag
	 *            the callback flag
	 */
	@Override
	public void unregisterCallback(String app, IGarmentCallback callback, int flag)
			throws android.os.RemoteException {
		if (callback != null) {
			de.unistuttgart.vis.wearable.os.service.GarmentOSService.mCallbacks.unregister(new CallbackNode(
					android.os.Binder.getCallingPid(), android.os.Binder
							.getCallingUid(), callback, flag));
		}
	}

    @Override
    public PSensor[] API_getAllSensors(String app) throws RemoteException {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors();
        PSensor[] psensors = new PSensor[sensors.size()];
        int i = -1;
        for(Sensor s : sensors)
            psensors[++i] = s.toParcelableAPI();
        return psensors;
    }

    @Override
    public PSensor[] API_getAllSensorsByType(int sensorType) {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors(SensorType.values()[sensorType]);
        PSensor[] psensors = new PSensor[sensors.size()];
        int i = -1;
        for(Sensor s : sensors)
            psensors[++i] = s.toParcelableAPI();
        return psensors;
    }

    @Override
    public PSensor API_getSensorById(String app, int id) throws RemoteException {
        return SensorManager.getSensorByID(id).toParcelableAPI();
    }

	//
	// Functions will be here
	//
	@Override
	public boolean SENSORS_SENSOR_isEnabled(String app, int sid) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return false;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return false;
        return sensor.isEnabled();
	}

	@Override
	public String SENSORS_SENSOR_getDisplayedSensorName(String app, int sid)
			throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return sensor.getDisplayedSensorName();
	}

	@Override
	public int SENSORS_SENSOR_getSampleRate(String app, int sid) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return Constants.ILLEGAL_VALUE;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        return sensor.getSampleRate();
	}

	@Override
	public int SENSORS_SENSOR_getSavePeriod(String app, int sid) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return Constants.ILLEGAL_VALUE;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        return sensor.getSavePeriod();
	}

	@Override
	public float SENSORS_SENSOR_getSmoothness(String app, int sid) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return Utils.getFloatFromIntByte(Constants.ILLEGAL_VALUE);

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        return sensor.getSmoothness();
	}

	@Override
	public int SENSORS_SENSOR_getSensorType(String app, int sid) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return Constants.ILLEGAL_VALUE;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getSensorType() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getSensorType().ordinal();
	}

	@Override
	public int SENSORS_SENSOR_getGraphType(String app, int sid) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return Constants.ILLEGAL_VALUE;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getGraphType() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getGraphType().ordinal();
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementUnit(String app, int sid)
			throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return Constants.ILLEGAL_VALUE;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getDisplayedMeasurementUnit() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getDisplayedMeasurementUnit().ordinal();
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementSystem(String app, int sid)
			throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return Constants.ILLEGAL_VALUE;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return Constants.ILLEGAL_VALUE;
        if(sensor.getDisplayedMeasurementSystem() == null)
            return Constants.ENUMERATION_NULL;
        return sensor.getDisplayedMeasurementSystem().ordinal();
	}

    @Override
    public PSensorData SENSORS_SENSOR_getRawData(String app, int sid) {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData().clone());
    }

    @Override
    public PSensorData SENSORS_SENSOR_getRawDataIB(String app, int sid, int time, boolean plusMinusOneSecond) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(Utils.unixToDate(time), plusMinusOneSecond).clone());
    }

    @Override
    public PSensorData SENSORS_SENSOR_getRawDataII(String app, int sid, int start, int end) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(Utils.unixToDate(start), Utils.unixToDate(end)).clone());
    }

    @Override
    public PSensorData SENSORS_SENSOR_getRawDataN(String app, int sid, int numberOfValues) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData(sensor.getRawData(numberOfValues));
    }

    /**
     * Check whether the given app has the permission to access the given sensor
     *
     * @param app      the app
     * @param sensorID the sensor id
     * @return true if the permission is denied
     */
    protected static boolean checkPermissionDenied(String app, int sensorID) {
        UserApp userApp = PrivacyManager.instance.getApp(app);
        return userApp == null || userApp.sensorProhibited(sensorID);
    }
}
