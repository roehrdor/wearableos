/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.service;

import android.os.RemoteException;
import de.unistuttgart.vis.wearable.os.activity.Activity;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityRecognitionModule;
import de.unistuttgart.vis.wearable.os.api.*;
import de.unistuttgart.vis.wearable.os.driver.DriverManager;
import de.unistuttgart.vis.wearable.os.privacy.PrivacyManager;
import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

import java.util.HashSet;

/**
 * This class implements the function calls passed through by the
 * {@link APIFunctions} class.
 * 
 * @author roehrdor
 */
class APIBinder extends IGarmentAPI.Stub {

    /**
     *
     * @param driver
     * @return
     * @throws RemoteException
     */
    @Override
    public int registerDriver(IGarmentDriver driver) throws RemoteException {
        return DriverManager.addDriver(driver);
    }

    /**
     * Return the currently performed activity
     * @param app the app requesting the activity
     * @return the activity or {@link de.unistuttgart.vis.wearable.os.utils.Constants#ENUMERATION_NULL} if not available
     */
    @Override
    public int getCurrentActivity(String app) throws RemoteException {
        Activity activity;
        if((activity = ActivityRecognitionModule.getInstance().getCurrentActivity()) != null && activity.getActivityEnum() != null)
            return activity.getActivityEnum().ordinal();
        else
            return Constants.ENUMERATION_NULL;
    }

    /**
     * Return the activity performed at the given time
     * @param app the app requesting the activity
     * @param time the time the activity has been performed at
     * @return the activity or {@link de.unistuttgart.vis.wearable.os.utils.Constants#ENUMERATION_NULL} if not available
     */
    @Override
    public int getActivityAtTime(String app, long time) throws RemoteException {
        //TODO
        return Constants.ENUMERATION_NULL;
    }

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
     * Register a App to the Garment OS System by calling this, this function shall be called on any
     * app start using the Garment OS SDK
     *
     * @param app the app name that is now connected to the GarmentOS System
     */
    @Override
    public void registerApp(String app) throws android.os.RemoteException {
        PrivacyManager.instance.registerNewApp(app);
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

    /**
     * Get all the sensor types for which there was at least once a sensor connected for
     *
     * @return a array containing the ordinal values of the {@link de.unistuttgart.vis.wearable.os.sensors.SensorType}
     * that have been connected to the GarmentOS System
     */
    @Override
    public int[] API_getSensorTypes() throws RemoteException {
        java.util.Set<Integer> sensorTypes = new HashSet<Integer>();
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors();
        int[] sensorTypesArray;
        int i = -1;
        for(Sensor s : sensors) {
            sensorTypes.add(s.getSensorType().ordinal());
        }
        sensorTypesArray = new int[sensorTypes.size()];
        for(int sensorType : sensorTypes)
            sensorTypesArray[++i] = sensorType;
        return sensorTypesArray;
    }

    /**
     * This is a helper function to reduce duplicated code, it creates an array of {@link de.unistuttgart.vis.wearable.os.api.PSensor}
     * objects from the given list containing {@link de.unistuttgart.vis.wearable.os.sensors.Sensor} objects by calling
     * the {@link de.unistuttgart.vis.wearable.os.sensors.Sensor#toParcelableAPI()} function and storing the output in the array
     *
     * @param sensors the sensors collection to be converted into a PSensor array
     * @return the converted objects as array
     */
    protected PSensor[] sensorCollectionToPSensorArray(java.util.Collection<Sensor> sensors) {
        PSensor[] psensors = new PSensor[sensors.size()];
        int i = -1;
        for(Sensor s : sensors)
            psensors[++i] = s.toParcelableAPI();
        return psensors;
    }

    /**
     * Return all the sensors known to the GarmentOS System
     *
     * @param app the app that is requesting the Sensors
     * @return an array of {@link de.unistuttgart.vis.wearable.os.api.PSensor} objects
     */
    @Override
    public PSensor[] API_getAllSensors(String app) throws RemoteException {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors();
        return sensorCollectionToPSensorArray(sensors);
    }

    /**
     * Get all the sensors for the given {@link de.unistuttgart.vis.wearable.os.sensors.SensorType} known to the GarmentOS system
     *
     * @param sensorType the sensorType to search Sensors for
     * @param app        the app that is requesting the Sensors
     * @return an array of {@link de.unistuttgart.vis.wearable.os.api.PSensor} objects
     */
    @Override
    public PSensor[] API_getAllSensorsByType(String app, int sensorType) throws RemoteException {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors(SensorType.values()[sensorType]);
        return sensorCollectionToPSensorArray(sensors);
    }

    /**
     * Return the Sensor with the given ID
     *
     * @param app the app name that is requesting the sensor
     * @param id  the id of the sensor to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object for the searched sensor or null if not found
     */
    @Override
    public PSensor API_getSensorById(String app, int id) throws RemoteException {
        return SensorManager.getSensorByID(id).toParcelableAPI();
    }

    //
    // Functions for the App to get the default Sensor
    //

    /**
     * Helper function to reduce duplicated code, this function checks whether the
     * app has the rights to access the sensor and returns the default sensor for the
     * given sensor type and app.
     *
     * @param app        the app that is requesting the and sensor and to get the default sensor for by the given sensor type
     * @param sensorType the sensor type to get the default sensor for
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    protected PSensor getSensorByDefaultType(String app, SensorType sensorType) {
        UserApp userApp = PrivacyManager.instance.getApp(app);
        if(userApp == null)
            return null;
        int sensorID = userApp.getDefaultSensor(sensorType);
        if(checkPermissionDenied(app, sensorID))
            return null;
        return SensorManager.getSensorByID(sensorID).toParcelableAPI();
    }

    /**
     * Get the default HeartRate Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getHeartRateSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.HEARTRATE);
    }

    /**
     * Get the default Accelerometer Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getAccelerometerSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.ACCELEROMETER);
    }

    /**
     * Get the default Magnetic Field Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getMagneticFieldSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.MAGNETIC_FIELD);
    }

    /**
     * Get the default Gyroscope Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getGyroscopeSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.GYROSCOPE);
    }

    /**
     * Get the default Light Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getLightSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.LIGHT);
    }

    /**
     * Get the default Pressure Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getPressureSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.PRESSURE);
    }

    /**
     * Get the default Proximity Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getProximitySensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.PROXIMITY);
    }

    /**
     * Get the default Gravity Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getGravitySensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.GRAVITY);
    }

    /**
     * Get the default Rotation Vector Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getRotationVectorSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.ROTATION_VECTOR);
    }

    /**
     * Get the default Relative Humidity Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getRelativeHumiditySensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.RELATIVE_HUMIDITY);
    }

    /**
     * Get the default Temperature Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getTemperatureSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.TEMPERATURE);
    }

    /**
     * Get the default GPS Sensor for the given app
     * @param app the app requesting the sensor
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensor} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensor API_getGPSSensor(String app) throws RemoteException {
        return getSensorByDefaultType(app, SensorType.GPS_SENSOR);
    }


    //
    // Functions for the apps to get the sensor values from their default
    // Sensors without the need to access a sensor object
    //

    /**
     * Helper function to reduce duplicated code, returns for the given app and sensor type the latest number of values
     * recorded sensor data
     * @param app the app requesting
     * @param numValues the number of sensor data values to get
     * @param sensorType the sensor type to get the sensor data from
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    protected PSensorData getValuesByDefaultType(String app, int numValues, SensorType sensorType) {
        UserApp userApp = PrivacyManager.instance.getApp(app);
        if(userApp == null)
            return null;
        int sensorID = userApp.getDefaultSensor(sensorType);
        if(checkPermissionDenied(app, sensorID))
            return null;
        Sensor sensor = SensorManager.getSensorByID(sensorID);
        return sensor != null ? new PSensorData((java.util.Vector<SensorData>)(sensor.getRawData(numValues, true)).clone()) : null;
    }

    /**
     * Get the latest number of sensor data values from the default Heart Rate Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getHeartRate(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.HEARTRATE);
    }

    /**
     * Get the latest number of sensor data values from the default Accelerometer Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getAccelerometer(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.ACCELEROMETER);
    }

    /**
     * Get the latest number of sensor data values from the default Magnetic Field Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getMagneticField(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.MAGNETIC_FIELD);
    }

    /**
     * Get the latest number of sensor data values from the default Gyroscope Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getGyroscope(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.GYROSCOPE);
    }

    /**
     * Get the latest number of sensor data values from the default Light Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getLight(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.LIGHT);
    }

    /**
     * Get the latest number of sensor data values from the default Pressure Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getPressure(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.PRESSURE);
    }

    /**
     * Get the latest number of sensor data values from the default Proximity Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getProximity(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.PROXIMITY);
    }

    /**
     * Get the latest number of sensor data values from the default Gravity Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getGravity(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.GRAVITY);
    }

    /**
     * Get the latest number of sensor data values from the default Rotation Vector Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getRotationVector(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.ROTATION_VECTOR);
    }

    /**
     * Get the latest number of sensor data values from the default Relative Humidity Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getRelativeHumidity(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.RELATIVE_HUMIDITY);
    }

    /**
     * Get the latest number of sensor data values from the default Temperature Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getTemperature(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.TEMPERATURE);
    }

    /**
     * Get the latest number of sensor data values from the default GPS Sensor for the given app
     * @param app the app requesting the sensor values
     * @param numValues the number of sensor data values to get
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object or null if the app is lacking permissions
     *         or either the app or the sensor could not have been found or the default sensor has not yet been set
     */
    @Override
    public PSensorData API_getGPS(String app, int numValues) throws RemoteException {
        return getValuesByDefaultType(app, numValues, SensorType.GPS_SENSOR);
    }


    //
	// Functions will be here
	//

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function checks whether the given sensor is enabled
     *
     * @param app the app checking the sensor state
     * @param sid the sensor id
     * @return true if the sensor is enabled, false if the sensor is disabled or the app is lacking permissions
     */
	@Override
	public boolean SENSORS_SENSOR_isEnabled(String app, int sid) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return false;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        return sensor != null && sensor.isEnabled();
	}

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function requests the displayed Sensor name for the given sensor id
     *
     * @param app the app checking the sensor name
     * @param sid the sensor id
     * @return the sensor name if the app has the permissions and the sensor does exist, null otherwise
     */
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

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function returns the sample rate for the given sensor by its id
     *
     * @param app the app checking the sensor sample rate
     * @param sid the sensor id
     * @return the sensor sample rate if the app has the permission and the sensor does exist
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ILLEGAL_VALUE} otherwise
     */
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

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function returns the save period for the given sensor by its id
     *
     * @param app the app checking the sensor save period
     * @param sid the sensor id
     * @return the sensor save period if the app has the permission and the sensor does exist
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ILLEGAL_VALUE} otherwise
     */
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

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function returns the smoothness for the given sensor by its id
     *
     * @param app the app checking the sensor smoothness
     * @param sid the sensor id
     * @return the sensor smoothness if the app has the permission and the sensor does exist, a bytewise float representation of
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ILLEGAL_VALUE} otherwise
     */
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

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function returns the sensor type for the given sensor by its id
     *
     * @param app the app checking the sensor type
     * @param sid the sensor id
     * @return the sensor type if the app has the permission and the sensor does exist, otherwise
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ILLEGAL_VALUE} or
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ENUMERATION_NULL}
     */
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

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function returns the sensor graph type for the given sensor by its id
     *
     * @param app the app checking the graph type
     * @param sid the sensor id
     * @return the graph type if the app has the permission and the sensor does exist, otherwise
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ILLEGAL_VALUE} or
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ENUMERATION_NULL}
     */
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

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function returns the displayed measurement unit for the given sensor by its id
     *
     * @param app the app checking the displayed measurement unit
     * @param sid the sensor id
     * @return the displayed measurement unit if the app has the permission and the sensor does exist, otherwise
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ILLEGAL_VALUE} or
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ENUMERATION_NULL}
     */
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

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * This function returns the displayed measurement system for the given sensor by its id
     *
     * @param app the app checking the displayed measurement system
     * @param sid the sensor id
     * @return the displayed measurement system if the app has the permission and the sensor does exist, otherwise
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ILLEGAL_VALUE} or
     *         {@link de.unistuttgart.vis.wearable.os.utils.Constants#ENUMERATION_NULL}
     */
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


    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * Get raw data from the sensor
     *
     * @param app the app checking for raw data
     * @param sid the sensor id
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object containing the data or null
     *         if the app is lacking permissions or the sensor could not be found
     */
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




    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * Get raw data from the sensor at the given time stamp
     *
     * @param app the app checking for raw data
     * @param sid the sensor id
     * @param time the time stamp
     * @param plusMinusOneSecond if true data recorded one second before and after is included as well
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object containing the data or null
     *         if the app is lacking permissions or the sensor could not be found
     */
    @Override
    public PSensorData SENSORS_SENSOR_getRawDataIB(String app, int sid, long time, boolean plusMinusOneSecond) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(Utils.longUnixToDate(time), plusMinusOneSecond).clone());
    }

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * Get raw data from the sensor at the in the given time period
     *
     * @param app the app checking for raw data
     * @param sid the sensor id
     * @param start the start of the time period
     * @param end the end of the time period
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object containing the data or null
     *         if the app is lacking permissions or the sensor could not be found
     */
    @Override
    public PSensorData SENSORS_SENSOR_getRawDataII(String app, int sid, long start, long end) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(Utils.longUnixToDate(start), Utils.longUnixToDate(end)).clone());
    }

    /**
     * <b>THIS FUNCTION SHALL NEVER BE CALLED FROM OUTSIDE THE {@link de.unistuttgart.vis.wearable.os.api.PSensor} CLASS</b>
     * Get the number of values raw data fields from the sensor
     *
     * @param app the app checking for raw data
     * @param sid the sensor id
     * @param numberOfValues the amount of data fields to request
     * @return a {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object containing the data or null
     *         if the app is lacking permissions or the sensor could not be found
     */
    @Override
    public PSensorData SENSORS_SENSOR_getRawDataN(String app, int sid, int numberOfValues, boolean fromStorage) throws RemoteException {
        if(checkPermissionDenied(app, sid))
            return null;

        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(numberOfValues, fromStorage).clone());
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
