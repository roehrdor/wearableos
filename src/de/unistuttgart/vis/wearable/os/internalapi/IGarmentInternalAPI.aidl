/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.PSensorData;

interface IGarmentInternalAPI {

	//
	// Internal API Function calls
	//
	void        API_addNewSensor(int sampleRate, int savePeriod, float smoothness, String displayedSensorName,
	                             int sensorType, String bluetoothID, int rawDataMeasurementSystem,
	                             int rawDataMeasurementUnit, int displayedMeasurementSystem, int displayedMeasurementUnit);
    void        API_removeSensor(int sensorID);

	String[] 	API_getRegisteredApplications();
	PUserApp[] 	API_getRegisteredUserApplications();
	PUserApp 	API_getRegisteredUserAppByName(String name);

	String[]    API_getSensorNames();
	PSensor[]   API_getAllSensors();
	PSensor[]   API_getAllSensorsByType(int sensorType);
	PSensor     API_getSensorById(int id);
	
	
	// 
	// Function calls forward to UserApp object  
	//
	boolean 	PRIVACY_USERAPP_sensorProhibited(int oid, int id);
	boolean 	PRIVACY_USERAPP_grantPermission(int oid, int id);
	boolean 	PRIVACY_USERAPP_revokePermission(int oid, int id);
	boolean 	PRIVACY_USERAPP_denySensorType(int oid, int flag);
	boolean 	PRIVACY_USERAPP_allowSensorType(int oid, int flag);
	boolean 	PRIVACY_USERAPP_sensorTypeGranted(int oid, int flag);
	void 		PRIVACY_USERAPP_grantActivityRecognition(int oid);
	void 		PRIVACY_USERAPP_denyActivityRecognition(int oid);
	boolean		PRIVACY_USERAPP_activityRecognitionGranted(int oid);
	int         PRIVACY_USERAPP_getDefaultSensor(int oid, int sensorType);
	PSensor     PRIVACY_USERAPP_getDefaultSensorO(int oid, int sensorType);
	void        PRIVACY_USERAPP_setDefaultSensor(int oid, int sensorType, int sensorID);
	
	
	// 
	// Function calls forward to Sensor object  
	//
	boolean 	SENSORS_SENSOR_isEnabled(int sid);
	void 		SENSORS_SENSOR_setEnabled(int sid, boolean isEnabled);
	String 		SENSORS_SENSOR_getDisplayedSensorName(int sid);
	void 		SENSORS_SENSOR_setDisplayedSensorName(int sid, String displayedSensorName);
	int 		SENSORS_SENSOR_getSampleRate(int sid);
	void 		SENSORS_SENSOR_setSampleRate(int sid, int sampleRate);
	int 		SENSORS_SENSOR_getSavePeriod(int sid);
	void 		SENSORS_SENSOR_setSavePeriod(int sid, int savePeriod);
	float 		SENSORS_SENSOR_getSmoothness(int sid);
	void 		SENSORS_SENSOR_setSmoothness(int sid, float smoothness);
	int 		SENSORS_SENSOR_getSensorType(int sid);
	void 		SENSORS_SENSOR_setSensorType(int sid, int sensorType);
	int 		SENSORS_SENSOR_getGraphType(int sid);
	void 		SENSORS_SENSOR_setGraphType(int sid, int graphType);
	int 		SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid);
	int 		SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid);
	void 		SENSORS_SENSOR_setDisplayedMeasurementSystem(int sid, int displayedMeasurementSystem);
	void 		SENSORS_SENSOR_setDisplayedMeasurementUnit(int sid, int displayedMeasurementUnit);
	PSensorData SENSORS_SENSOR_getRawData(int sid);
	PSensorData SENSORS_SENSOR_getRawDataIB(int sid, long time, boolean plusMinusOneSecond);
	PSensorData SENSORS_SENSOR_getRawDataII(int sid, long start, long end);
	PSensorData SENSORS_SENSOR_getRawDataN(int sid, int numberOfValues, boolean fromStorage);
}