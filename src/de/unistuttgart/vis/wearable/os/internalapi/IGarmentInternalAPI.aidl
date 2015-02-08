/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;

interface IGarmentInternalAPI {
	//
	// Internal API Function calls
	//
	String[] 	API_getRegisteredApplications();
	PUserApp[] 	API_getRegisteredUserApplications();
	PUserApp 	API_getRegisteredUserAppByName(String name);
	
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
}