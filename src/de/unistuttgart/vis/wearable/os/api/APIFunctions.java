/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

import java.util.List;

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

    public static void registerCallback(IGarmentCallback callback, int cause) {
        if(APIHandle.isServiceBound()) {
            try {
                APIHandle.getGarmentAPIHandle().registerCallback(APIHandle.getAppPackageID(), callback, cause);
                return;
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void unregisterCallback(IGarmentCallback callback, int cause) {
        if(APIHandle.isServiceBound()) {
            try {
                APIHandle.getGarmentAPIHandle().unregisterCallback(APIHandle.getAppPackageID(), callback, cause);
                return;
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

	// =============================================================================
	//
	// Public SDK Functions
	//
	// =============================================================================


    public static PSensor[] getAllSensors() {
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
                return APIHandle.getGarmentAPIHandle().API_getAllSensorsByType(APIHandle.getAppPackageID(), sensorType.ordinal());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getSensorById(int id) {
        if (APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getSensorById(APIHandle.getAppPackageID(), id);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static SensorType[] getAvailableSensorTypes() {
        if(APIHandle.isServiceBound()) {
            try {
                int[] sensorTypes = APIHandle.getGarmentAPIHandle().API_getSensorTypes();
                SensorType[] sensorTypesO = new SensorType[sensorTypes.length];
                int i = -1;
                for(int sensorType : sensorTypes)
                    sensorTypesO[++i] = SensorType.values()[sensorType];
                return sensorTypesO;
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    //
    // Functions for the App to get the default Sensor
    //
    public static PSensor getHeartRateSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getHeartRateSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getAccelerometerSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getAccelerometerSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getMagneticFieldSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getMagneticFieldSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getGyroscopeSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getGyroscopeSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getLightSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getLightSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getPressureSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getPressureSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getProximitySensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getProximitySensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getGravitySensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getGravitySensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getRotationVectorSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getRotationVectorSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getRelativeHumiditySensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getRelativeHumiditySensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getTemperatureSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getTemperatureSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor getGPSSensor() {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getGPSSensor(APIHandle.getAppPackageID());
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }


    //
    // Functions for the apps to get the sensor values from their default
    // Sensors without the need to access a sensor object
    //
    public static List<SensorData> getHeartRate(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getHeartRate(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getAccelerometer(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getAccelerometer(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getMagneticField(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getMagneticField(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getGyroscope(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getGyroscope(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getLight(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getLight(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getPressure(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getPressure(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getProximity(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getProximity(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getGravity(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getGravity(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getRotationVector(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getRotationVector(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getRelativeHumidity(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getHeartRate(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getTemperature(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getTemperature(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<SensorData> getGPS(int numValues) {
        if(APIHandle.isServiceBound()) {
            try {
                return APIHandle.getGarmentAPIHandle().API_getGPS(APIHandle.getAppPackageID(), numValues).toSensorDataList();
            } catch(android.os.RemoteException e) {
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
