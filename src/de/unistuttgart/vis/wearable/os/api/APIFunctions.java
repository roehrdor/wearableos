/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

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
                return APIHandle.getGarmentAPIHandle().API_getAllSensors(APIHandle.getAppPackageID());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor[] getAllSensors(SensorType sensorType) {
        if(APIHandle.isServiceBound()) {
            try {
                if(sensorType == null)
                    return null;
                return APIHandle.getGarmentAPIHandle().API_getAllSensorsByType(sensorType.ordinal());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor API_getSensorById(int id) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getSensorById(APIHandle.getAppPackageID(), id);
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
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_isEnabled(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static String SENSORS_SENSOR_getDisplayedSensorName(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getDisplayedSensorName(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getSampleRate(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getSampleRate(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getSavePeriod(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getSavePeriod(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static float SENSORS_SENSOR_getSmoothness(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getSmoothness(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getSensorType(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getSensorType(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getGraphType(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getGraphType(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getDisplayedMeasurementUnit(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getDisplayedMeasurementUnit(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}

	public static int SENSORS_SENSOR_getDisplayedMeasurementSystem(int sid) {
		if (APIHandle.isServiceBound()) {
			try {
				return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getDisplayedMeasurementSystem(APIHandle.getAppPackageID(), sid);
			} catch (android.os.RemoteException e) {
			}
		}
		throw new RuntimeException("Connection failed");
	}


    public static PSensorData SENSORS_SENSOR_getRawData(int sid) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getRawData(APIHandle.getAppPackageID(), sid);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensorData SENSORS_SENSOR_getRawDataIB(int sid, int time, boolean plusMinusOneSecond) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getRawDataIB(APIHandle.getAppPackageID(), sid, time, plusMinusOneSecond);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensorData SENSORS_SENSOR_getRawDataII(int sid, int start, int end) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getRawDataII(APIHandle.getAppPackageID(), sid, start, end);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensorData SENSORS_SENSOR_getRawDataN(int sid, int numberOfValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().SENSORS_SENSOR_getRawDataN(APIHandle.getAppPackageID(), sid, numberOfValues);
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

}
