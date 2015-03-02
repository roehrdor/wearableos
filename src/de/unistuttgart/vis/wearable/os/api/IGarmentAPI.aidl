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
	
	void registerCallback(String app, IGarmentCallback callback, int ID);
	void unregisterCallback(String app, IGarmentCallback callback, int ID);

	PSensor[]   API_getAllSensors(String app);
	PSensor[]   API_getAllSensorsByType(int sensorType);
    PSensor     API_getSensorById(String app, int id);
	
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
    PSensorData SENSORS_SENSOR_getRawDataIB(String app, int sid, int time, boolean plusMinusOneSecond);
    PSensorData SENSORS_SENSOR_getRawDataII(String app, int sid, int start, int end);
    PSensorData SENSORS_SENSOR_getRawDataN(String app, int sid, int numberOfValues);
}