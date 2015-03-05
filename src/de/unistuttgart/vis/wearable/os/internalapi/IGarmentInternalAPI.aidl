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
	PSensor     API_addNewSensor(int sampleRate, int savePeriod, float smoothness, String displayedSensorName,
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
	void 		SENSORS_SENSOR_setEnabled(int sid, boolean isEnabled);
	void 		SENSORS_SENSOR_setDisplayedSensorName(int sid, String displayedSensorName);
	void 		SENSORS_SENSOR_setSampleRate(int sid, int sampleRate);
	void 		SENSORS_SENSOR_setSavePeriod(int sid, int savePeriod);
	void 		SENSORS_SENSOR_setSmoothness(int sid, float smoothness);
	void 		SENSORS_SENSOR_setSensorType(int sid, int sensorType);
	void 		SENSORS_SENSOR_setGraphType(int sid, int graphType);
	void 		SENSORS_SENSOR_setDisplayedMeasurementSystem(int sid, int displayedMeasurementSystem);
	void 		SENSORS_SENSOR_setDisplayedMeasurementUnit(int sid, int displayedMeasurementUnit);
	void 		SENSORS_SENSOR_addRawData(int sid, long time, in float[] data);
	PSensorData SENSORS_SENSOR_getRawData(int sid);
	PSensorData SENSORS_SENSOR_getRawDataII(int sid, long start, long end);
}