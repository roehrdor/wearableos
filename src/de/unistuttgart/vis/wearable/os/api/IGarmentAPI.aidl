/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.PSensorData;

interface IGarmentAPI {
	long currentTime();
	
	void registerCallback(IGarmentCallback callback, int ID);
	void unregisterCallback(IGarmentCallback callback, int ID);

	PSensor[]   API_getAllSensors();
    PSensor     API_getSensorById(int id);
	
	// 
	// Function calls forward to Sensor object  
	//
	boolean 	SENSORS_SENSOR_isEnabled(int sid);	
	String 		SENSORS_SENSOR_getDisplayedSensorName(int sid);
	int 		SENSORS_SENSOR_getSampleRate(int sid);
	int 		SENSORS_SENSOR_getSavePeriod(int sid);
	float 		SENSORS_SENSOR_getSmoothness(int sid);
	int 		SENSORS_SENSOR_getSensorType(int sid);
	int 		SENSORS_SENSOR_getGraphType(int sid);	
	int 		SENSORS_SENSOR_getRawDataMeasurementUnit(int sid);
	int 		SENSORS_SENSOR_getRawDataMeasurementSystem(int sid);
	int 		SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid);
	int 		SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid);
	PSensorData SENSORS_SENSOR_getRawData(int sid);
    PSensorData SENSORS_SENSOR_getRawDataIB(int sid, int time, boolean plusMinusOneSecond);
    PSensorData SENSORS_SENSOR_getRawDataII(int sid, int start, int end);
}