/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.activityRecognition.NeuralNetworkManager;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementUnits;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    public static void unpackArchiveFile(java.io.File file) {
        if(APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().API_unpackArchiveFile(file.getAbsolutePath());
                return;
            } catch(android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void unpackEncryptedArchiveFile(java.io.File file, String key) {
        if(APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().API_unpackEncryptedArchiveFile(file.getAbsolutePath(), key);
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
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

    public static PSensor addNewSensor(int sampleRate, int savePeriod, float smoothness,
                             String displayedSensorName, SensorType sensorType, String bluetoothID,
                             MeasurementSystems rawDataMeasurementSystem, MeasurementUnits rawDataMeasurementUnit,
                             MeasurementSystems displayedMeasurementSystem, MeasurementUnits displayedMeasurementUnit) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().API_addNewSensor(Constants.NO_DRIVER, sampleRate, savePeriod,
                                    smoothness, displayedSensorName, sensorType.ordinal(), bluetoothID,
                                    rawDataMeasurementSystem.ordinal(), rawDataMeasurementUnit.ordinal(),
                                    displayedMeasurementSystem.ordinal(), displayedMeasurementUnit.ordinal());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor addNewSensor(int driverID, int sampleRate, int savePeriod, float smoothness,
                                       String displayedSensorName, SensorType sensorType, String bluetoothID,
                                       MeasurementSystems rawDataMeasurementSystem, MeasurementUnits rawDataMeasurementUnit,
                                       MeasurementSystems displayedMeasurementSystem, MeasurementUnits displayedMeasurementUnit) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().API_addNewSensor(driverID, sampleRate, savePeriod,
                        smoothness, displayedSensorName, sensorType.ordinal(), bluetoothID,
                        rawDataMeasurementSystem.ordinal(), rawDataMeasurementUnit.ordinal(),
                        displayedMeasurementSystem.ordinal(), displayedMeasurementUnit.ordinal());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void removeSensor(int sensorID) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().API_removeSensor(sensorID);
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

    public static PSensor[] getAllSensors(SensorType sensorType) {
        if(APIHandle.isServiceBound()) {
            try {
                if(sensorType == null)
                    return null;
                return APIHandle.getGarmentInternalAPIHandle().API_getAllSensorsByType(sensorType.ordinal());
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

    public static int PRIVACY_USERAPP_getDefaultSensor(int oid, SensorType sensorType) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                if(sensorType == null)
                    return Constants.ILLEGAL_VALUE;
                return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_getDefaultSensor(oid, sensorType.ordinal());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static PSensor PRIVACY_USERAPP_getDefaultSensorO(int oid, SensorType sensorType) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                if(sensorType == null)
                    return null;
                return APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_getDefaultSensorO(oid, sensorType.ordinal());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void PRIVACY_USERAPP_setDefaultSensor(int oid, SensorType sensorType, int sensorID) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                if(sensorType == null)
                    return;
                APIHandle.getGarmentInternalAPIHandle().PRIVACY_USERAPP_setDefaultSensor(oid, sensorType.ordinal(), sensorID);
                return;
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

    public static void SENSORS_SENSOR_addRawData(int sid, SensorData sensorData) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_addRawData(sid, sensorData.getLongUnixDate(), sensorData.getData());
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    @Deprecated
    public static boolean SENSORS_SENSOR_isEnabled(int sid) {
        return de.unistuttgart.vis.wearable.os.api.APIFunctions.SENSORS_SENSOR_isEnabled(sid);
    }

    @Deprecated
    public static PSensorData SENSORS_SENSOR_getRawData(int sid) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getRawData(sid);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    @Deprecated
    public static PSensorData SENSORS_SENSOR_getRawDataII(int sid, long start, long end) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().SENSORS_SENSOR_getRawDataII(sid, start, end);
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }


    @Deprecated
    public static int SENSORS_SENSOR_getSensorType(int sid) {
        return de.unistuttgart.vis.wearable.os.api.APIFunctions.SENSORS_SENSOR_getSensorType(sid);
    }




    // =====================================================================
    //
    // Function calls forward HAR Module
    //
    // =====================================================================

    public static void train(final String activity, final int windowLength) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().HAR_train(activity, windowLength);
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void train(final String activity, final int windowLength, final Date begin, final Date end) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().HAR_train_SiDD(activity, windowLength, Utils.dateToLongUnix(begin), Utils.dateToLongUnix(end));
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void stopTraining()  {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().HAR_stopTraining();
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<String> getActivityNames() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return Arrays.asList(APIHandle.getGarmentInternalAPIHandle().HAR_getActivityNames());
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void loadNeuralNetwork() throws FileNotFoundException {
        if (APIHandle.isInternalServiceBound()) {
            try {
                if(!APIHandle.getGarmentInternalAPIHandle().HAR_loadNeuralNetwork())
                    throw new FileNotFoundException();
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void saveNeuralNetwork() throws FileNotFoundException {
        if (APIHandle.isInternalServiceBound()) {
            try {
                if(!APIHandle.getGarmentInternalAPIHandle().HAR_saveNeuralNetwork())
                    throw new FileNotFoundException();
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void deleteNeuralNetwork() throws FileNotFoundException {
        if (APIHandle.isInternalServiceBound()) {
            try {
                if(!APIHandle.getGarmentInternalAPIHandle().HAR_deleteNeuralNetwork())
                    throw new FileNotFoundException();
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static boolean createNeuralNetwork() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().HAR_createNeuralNetwork();
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static NeuralNetworkManager.Status getNeuralNetworkStatus() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return NeuralNetworkManager.Status.values()[APIHandle.getGarmentInternalAPIHandle().HAR_getNeuralNetworkStatus()];
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<String> getSensors() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return new ArrayList<String>(Arrays.asList(APIHandle.getGarmentInternalAPIHandle().HAR_getSensors()));
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static List<String> getSupportedActivities() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return new ArrayList<String>(Arrays.asList(APIHandle.getGarmentInternalAPIHandle().HAR_getSupportedActivities()));
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static boolean isTraining() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().HAR_isTraining();
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static boolean isRecognizing() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                return APIHandle.getGarmentInternalAPIHandle().HAR_isRecognizing();
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void recognize(final int windowLength) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().HAR_recognize(windowLength);
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void stopRecognition() {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().HAR_stopRecognition();
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void addSensor(String sensor) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().HAR_addSensor(sensor);
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

    public static void addActivity(String activity) {
        if (APIHandle.isInternalServiceBound()) {
            try {
                APIHandle.getGarmentInternalAPIHandle().HAR_addActivity(activity);
                return;
            } catch (android.os.RemoteException e) {
            }
        }
        throw new RuntimeException("Connection failed");
    }

}
