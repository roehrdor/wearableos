/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.PSensorData;

interface IGarmentAPI {
	long currentTime();

	void registerApp(String app);

	void registerCallback(String app, IGarmentCallback callback, int ID);
	void unregisterCallback(String app, IGarmentCallback callback, int ID);

	int[]       API_getSensorTypes();

	PSensor[]   API_getAllSensors(String app);
	PSensor[]   API_getAllSensorsByType(String app, int sensorType);
    PSensor     API_getSensorById(String app, int id);

    //
    // Functions for the App to get the default Sensor
    //
    PSensor     API_getHeartRateSensor(String app);
    PSensor     API_getAccelerometerSensor(String app);
    PSensor     API_getMagneticFieldSensor(String app);
    PSensor     API_getGyroscopeSensor(String app);
    PSensor     API_getLightSensor(String app);
    PSensor     API_getPressureSensor(String app);
    PSensor     API_getProximitySensor(String app);
    PSensor     API_getGravitySensor(String app);
    PSensor     API_getRotationVectorSensor(String app);
    PSensor     API_getRelativeHumiditySensor(String app);
    PSensor     API_getTemperatureSensor(String app);
    PSensor     API_getGPSSensor(String app);

    //
    // Functions for the apps to get the sensor values from their default
    // Sensors without the need to access a sensor object
    //
    PSensorData API_getHeartRate(String app, int numValues);
    PSensorData API_getAccelerometer(String app, int numValues);
    PSensorData API_getMagneticField(String app, int numValues);
    PSensorData API_getGyroscope(String app, int numValues);
    PSensorData API_getLight(String app, int numValues);
    PSensorData API_getPressure(String app, int numValues);
    PSensorData API_getProximity(String app, int numValues);
    PSensorData API_getGravity(String app, int numValues);
    PSensorData API_getRotationVector(String app, int numValues);
    PSensorData API_getRelativeHumidity(String app, int numValues);
    PSensorData API_getTemperature(String app, int numValues);
    PSensorData API_getGPS(String app, int numValues);
	
	// 
	// Function calls forward to Sensor object  
	//
	boolean 	SENSORS_SENSOR_isEnabled(String app, int sid);
	String 		SENSORS_SENSOR_getDisplayedSensorName(String app, int sid);
	int 		SENSORS_SENSOR_getSampleRate(String app, int sid);
	int 		SENSORS_SENSOR_getSavePeriod(String app, int sid);
	float 		SENSORS_SENSOR_getSmoothness(String app, int sid);
	int 		SENSORS_SENSOR_getSensorType(String app, int sid);
	int 		SENSORS_SENSOR_getGraphType(String app, int sid);
	int 		SENSORS_SENSOR_getDisplayedMeasurementUnit(String app, int sid);
	int 		SENSORS_SENSOR_getDisplayedMeasurementSystem(String app, int sid);
	PSensorData SENSORS_SENSOR_getRawData(String app, int sid);
    PSensorData SENSORS_SENSOR_getRawDataIB(String app, int sid, long time, boolean plusMinusOneSecond);
    PSensorData SENSORS_SENSOR_getRawDataII(String app, int sid, long start, long end);
    PSensorData SENSORS_SENSOR_getRawDataN(String app, int sid, int numberOfValues);
}