/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;

/**
 * This class provides of all the available function that can be called from any
 * application that has bound its connection to the service.
 * 
 * @author roehrdor
 */
public class APIFunctions {
	public static long getTime() {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().currentTime();
			} catch (android.os.RemoteException e) {
			}
		}
		return 0xFFFFFFFFFFl;
	}

	// =============================================================================
	//
	// Public SDK Functions
	//
	// =============================================================================


    public static PSensor[] API_getAllSensors() {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getAllSensors();
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor API_getSensorById(int id) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getSensorById(id);
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
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_isEnabled(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static String SENSORS_SENSOR_getDisplayedSensorName(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getDisplayedSensorName(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getSampleRate(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSampleRate(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getSavePeriod(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSavePeriod(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static float SENSORS_SENSOR_getSmoothness(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSmoothness(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getSensorType(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getSensorType(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getGraphType(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getGraphType(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getRawDataMeasurementUnit(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getRawDataMeasurementUnit(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getRawDataMeasurementSystem(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getRawDataMeasurementSystem(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getDisplayedMeasurementUnit(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getDisplayedMeasurementSystem(sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}


    public static PSensorData SENSORS_SENSOR_getRawData(int sid) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getRawData(sid);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensorData SENSORS_SENSOR_getRawDataIB(int sid, int time, boolean plusMinusOneSecond) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getRawDataIB(sid, time, plusMinusOneSecond);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensorData SENSORS_SENSOR_getRawDataII(int sid, int start, int end) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getRawDataII(sid, start, end);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

}
