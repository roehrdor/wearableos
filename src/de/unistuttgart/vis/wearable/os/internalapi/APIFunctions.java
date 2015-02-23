/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementUnits;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

/**
 * <p>
 * Internal API Functions that are used to modify settings in the settings app
 * or to change permissions for selected applications. These functions are only
 * available to the Garment OS settings application since other application
 * shall not be granted to do this kind of modifications. To see real SDK API
 * functions have a look at the
 * {@link de.unistuttgart.vis.wearable.os.api.APIFunctions} class.
 * </p>
 * <p>
 * Note 1: Since the implementation of any of these functions do not differ from
 * each other these are no comments added. Each of the following functions works
 * like this. First check whether the connection to the service has been
 * successfully established. If this is not the case a RuntimeExpcetion will be
 * thrown. Otherwise the function call will be redirected to the corresponding
 * handle created in {@link APIHandle} and the result in case of a non-void
 * function will be returned to the caller.
 * </p>
 * <p>
 * Note 2: These functions will be executed in the application and not the
 * service but will make the service functions being called.
 * </p>
 * <p>
 * Note 3: Functions will not execute asynchronously by default. To get this
 * kind of behavior the caller must call the function in a separate Thread.
 * </p>
 * 
 * @author roehrdor
 */
public class APIFunctions {
	// =============================================================================
	//
	// Private SDK Functions
	// These functions will not be included in the SDK for the Garment OS
	// library.
	// These functions are needed to provide several functionalities that shall
	// only be done by using the provided Settings Application.
	//
	// =============================================================================

    public static void addNewSensor(int sampleRate, int savePeriod, int smoothness,
                             String displayedSensorName, SensorType sensorType, String bluetoothID,
                             MeasurementSystems rawDataMeasurementSystem, MeasurementUnits rawDataMeasurementUnit,
                             MeasurementSystems displayedMeasurementSystem, MeasurementUnits displayedMeasurementUnit) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().API_addNewSensor(sampleRate, savePeriod,
                                    smoothness, displayedSensorName, sensorType.ordinal(), bluetoothID,
                                    rawDataMeasurementSystem.ordinal(), rawDataMeasurementUnit.ordinal(),
                                    displayedMeasurementSystem.ordinal(), displayedMeasurementUnit.ordinal());
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

	public static String[] getRegisteredApplications() {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().API_getRegisteredApplications();
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static PUserApp[] API_getRegisteredUserApplications() {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().API_getRegisteredUserApplications();
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}
	
	public static PUserApp API_getRegisteredUserAppByName(String name) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().API_getRegisteredUserAppByName(name);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

    public static String[] API_getSensorNames() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().API_getSensorNames();
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor[] API_getAllSensors() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().API_getAllSensors();
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor API_getSensorById(int id) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().API_getSensorById(id);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }
	
	// =====================================================================
	// 
	// Function calls forward to UserApp object  
	//
	// =====================================================================
	public static boolean PRIVACY_USERAPP_sensorProhibited(int oid, int id) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_sensorProhibited(oid, id);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_grantPermission(int oid, int id) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_grantPermission(oid, id);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_revokePermission(int oid, int id) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_revokePermission(oid, id);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_denySensorType(int oid, int flag) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_denySensorType(oid, flag);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_allowSensorType(int oid, int flag) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_allowSensorType(oid, flag);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_sensorTypeGranted(int oid, int flag) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_sensorTypeGranted(oid, flag);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static void PRIVACY_USERAPP_grantActivityRecognition(int oid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_grantActivityRecognition(oid);
				return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static void PRIVACY_USERAPP_denyActivityRecognition(int oid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_denyActivityRecognition(oid);
				return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static boolean PRIVACY_USERAPP_activityRecognitionGranted(int oid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_activityRecognitionGranted(oid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}
	
	
	// =====================================================================
	// 
	// Function calls forward to Sensor object  
	//
	// =====================================================================
	public static boolean SENSORS_SENSOR_isEnabled(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_isEnabled(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setEnabled(int sid, boolean isEnabled) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setEnabled(sid, isEnabled);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static String SENSORS_SENSOR_getDisplayedSensorName(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getDisplayedSensorName(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setDisplayedSensorName(int sid, String displayedSensorName) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setDisplayedSensorName(sid, displayedSensorName);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static int SENSORS_SENSOR_getSampleRate(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSampleRate(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setSampleRate(int sid, int sampleRate) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setSampleRate(sid, sampleRate);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static int SENSORS_SENSOR_getSavePeriod(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSavePeriod(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setSavePeriod(int sid, int savePeriod) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setSavePeriod(sid, savePeriod);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static float SENSORS_SENSOR_getSmoothness(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSmoothness(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setSmoothness(int sid, float smoothness) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setSmoothness(sid, smoothness);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static int SENSORS_SENSOR_getSensorType(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSensorType(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setSensorType(int sid, int sensorType) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setSensorType(sid, sensorType);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static int SENSORS_SENSOR_getGraphType(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getGraphType(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setGraphType(int sid, int graphType) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setGraphType(sid, graphType);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static int SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getDisplayedMeasurementUnit(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setDisplayedMeasurementUnit(int sid, int displayedMeasurementUnit) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setDisplayedMeasurementUnit(sid, displayedMeasurementUnit);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static int SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getDisplayedMeasurementSystem(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

	public static void SENSORS_SENSOR_setDisplayedMeasurementSystem(int sid, int displayedMeasurementSystem) {
		if (APIHandle.isInternalServiceBound()) {
			try {
				APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_setDisplayedMeasurementSystem(sid, displayedMeasurementSystem);
                return;
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");	
	}

    public static PSensorData SENSORS_SENSOR_getRawData(int sid) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getRawData(sid);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensorData SENSORS_SENSOR_getRawDataIB(int sid, int time, boolean plusMinusOneSecond) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getRawDataIB(sid, time, plusMinusOneSecond);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensorData SENSORS_SENSOR_getRawDataII(int sid, int start, int end) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getRawDataII(sid, start, end);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }
}
