/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.service;

import android.os.RemoteException;
import de.unistuttgart.vis.wearable.os.api.*;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
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
	public void registerCallback(IGarmentCallback callback, int flag)
			throws android.os.RemoteException {
		if (callback != null) {
			GarmentOSSerivce.mCallbacks.register(new CallbackNode(
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
	public void unregisterCallback(IGarmentCallback callback, int flag)
			throws android.os.RemoteException {
		if (callback != null) {
			GarmentOSSerivce.mCallbacks.unregister(new CallbackNode(
					android.os.Binder.getCallingPid(), android.os.Binder
							.getCallingUid(), callback, flag));
		}
	}

    @Override
    public PSensor[] API_getAllSensors() throws RemoteException {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors();
        PSensor[] psensors = new PSensor[sensors.size()];
        int i = -1;
        for(Sensor s : sensors)
            psensors[++i] = s.toParcelableAPI();
        return psensors;
    }

    @Override
    public PSensor API_getSensorById(int id) throws RemoteException {
        return SensorManager.getSensorByID(id).toParcelableAPI();
    }

	//
	// Functions will be here
	//
	@Override
	public boolean SENSORS_SENSOR_isEnabled(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String SENSORS_SENSOR_getDisplayedSensorName(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int SENSORS_SENSOR_getSampleRate(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SENSORS_SENSOR_getSavePeriod(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float SENSORS_SENSOR_getSmoothness(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SENSORS_SENSOR_getSensorType(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SENSORS_SENSOR_getGraphType(int sid) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
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
