/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.internalservice;

import android.os.RemoteException;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityRecognitionModule;
import de.unistuttgart.vis.wearable.os.api.IGarmentDriver;
import de.unistuttgart.vis.wearable.os.cloud.Archiver;
import de.unistuttgart.vis.wearable.os.driver.DriverManager;
import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.internalapi.*;
import de.unistuttgart.vis.wearable.os.privacy.PrivacyManager;
import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.sensors.*;
import de.unistuttgart.vis.wearable.os.service.GarmentOSService;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * <p>
 * This class implements the functions provided by the internal SDK. These
 * functions will be called from the corresponding handle created in
 * {@link APIHandle}.
 * </p>
 * <p>
 * Note these functions will be executed in the service
 * </p>
 *
 * @author roehrdor
 */
public class APIInternalBinder extends IGarmentInternalAPI.Stub {

    @Override
    public void API_unpackArchiveFile(String file) throws RemoteException {
        Archiver.unpackArchiveFile(new File(file));
    }

    @Override
    public void API_unpackEncryptedArchiveFile(String file, String pw) throws RemoteException {
        Archiver.unpackEncryptedFile(pw, new File(file));
    }

    @Override
    public PGarmentDriver[] API_getDrivers() throws RemoteException {
        return DriverManager.getPDrivers();
    }

    @Override
    public PSensor API_addNewSensor(int driverID, int sampleRate, int savePeriod, float smoothness, String displayedSensorName,
                                 int sensorType, String bluetoothID, int rawDataMeasurementSystem,
                                 int rawDataMeasurementUnit, int displayedMeasurementSystem, int displayedMeasurementUnit) throws RemoteException{
        IGarmentDriver driver = driverID == Constants.NO_DRIVER ? null : DriverManager.getDriverByID(driverID);
        Sensor sensor = new Sensor(driver, sampleRate, savePeriod, smoothness, displayedSensorName, SensorType.values()[sensorType], bluetoothID, MeasurementSystems.values()[rawDataMeasurementSystem],
                MeasurementUnits.values()[rawDataMeasurementUnit], MeasurementSystems.values()[displayedMeasurementSystem], MeasurementUnits.values()[displayedMeasurementUnit]);

        if(sensor != null) {
            GarmentOSService.startBTService(sensor.getSensorID());
            return sensor.toParcelable();
        } else {
            return null;
        }
    }

    @Override
    public void API_removeSensor(int sensorID) throws RemoteException {
        SensorManager.removeSensor(sensorID);
    }

	@Override
	public String[] API_getRegisteredApplications() throws RemoteException {
		return PrivacyManager.instance.getAllAppNames();
	}

	@Override
	public PUserApp[] API_getRegisteredUserApplications() throws RemoteException {
		UserApp[] aua = PrivacyManager.instance.getAllApps();
		PUserApp[] apua = new PUserApp[aua.length];
		for(int i = 0; i != apua.length; ++i) {
			apua[i] = aua[i].toParcelable();			
		}		
		return apua; 				
	}

	@Override
	public PUserApp API_getRegisteredUserAppByName(String name) throws RemoteException {
		return PrivacyManager.instance.getApp(name).toParcelable();
	}

    @Override
    public String[] API_getSensorNames() throws RemoteException {
        return SensorManager.getSensorNames();
    }

    @Override
    public PSensor[] API_getAllSensors() throws RemoteException {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors();
        PSensor[] psensors = new PSensor[sensors.size()];
        int i = -1;
        for(Sensor s : sensors)
            psensors[++i] = s.toParcelable();
        return psensors;
    }

    @Override
    public PSensor[] API_getAllSensorsByType(int sensorType) throws RemoteException {
        java.util.Collection<Sensor> sensors = SensorManager.getAllSensors(SensorType.values()[sensorType]);
        PSensor[] pSensors = new PSensor[sensors.size()];
        int i = -1;
        for(Sensor s : sensors)
            pSensors[++i] = s.toParcelable();
        return pSensors;
    }

    @Override
    public PSensor API_getSensorById(int id) throws RemoteException {
        return SensorManager.getSensorByID(id).toParcelable();
    }

    // =====================================================================
	// 
	// Function calls forward to UserApp object  
	//
	// -------------------------------------------------------------
	//
	// Calls to UserApp, oid represents the unique ID of the object 
	//
	@Override
	public boolean PRIVACY_USERAPP_sensorProhibited(int oid, int id) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        return userApp == null || userApp.sensorProhibited(id);
	}

	@Override
	public boolean PRIVACY_USERAPP_grantPermission(int oid, int id) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        return userApp != null && userApp.grantPermission(id);
	}

	@Override
	public boolean PRIVACY_USERAPP_revokePermission(int oid, int id) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        return userApp != null && userApp.revokePermission(id);
	}

	@Override
	public boolean PRIVACY_USERAPP_denySensorType(int oid, int flag) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        return userApp != null && userApp.denySensorType(flag);
	}

	@Override
	public boolean PRIVACY_USERAPP_allowSensorType(int oid, int flag) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        return userApp != null && userApp.allowSensorType(flag);
	}

	@Override
	public boolean PRIVACY_USERAPP_sensorTypeGranted(int oid, int flag) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        return userApp != null && userApp.sensorTypeGranted(flag);
	}

	@Override
	public void PRIVACY_USERAPP_grantActivityRecognition(int oid) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        if(userApp != null)
            userApp.grantActivityRecognition();
	}

	@Override
	public void PRIVACY_USERAPP_denyActivityRecognition(int oid) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        if(userApp != null)
            userApp.denyActivityRecognition();
	}

	@Override
	public boolean PRIVACY_USERAPP_activityRecognitionGranted(int oid) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        return userApp != null && userApp.activityRecognitionGranted();
	}

    @Override
    public int PRIVACY_USERAPP_getDefaultSensor(int oid, int sensorType) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        if(userApp != null)
            return userApp.getDefaultSensor(SensorType.values()[sensorType]);
        return Constants.ILLEGAL_VALUE;
    }

    @Override
    public PSensor PRIVACY_USERAPP_getDefaultSensorO(int oid, int sensorType) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        int sensorID;
        if(userApp != null && (sensorID = userApp.getDefaultSensor(SensorType.values()[sensorType])) != 0)
            return SensorManager.getSensorByID(sensorID).toParcelable();
        return null;
    }

    @Override
    public void PRIVACY_USERAPP_setDefaultSensor(int oid, int sensorType, int sensorID) throws RemoteException {
        UserApp userApp = PrivacyManager.instance.getApp(oid);
        if(userApp != null)
            userApp.setDefaultSensor(SensorType.values()[sensorType], sensorID);
    }

	
	// =====================================================================
	// 
	// Function calls forward to Sensor object  
	//
	// -------------------------------------------------------------
	//
	// Calls to Sensor, sid represents the unique ID of the object 
	//
	@Override
	public void SENSORS_SENSOR_setEnabled(int sid, boolean isEnabled) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setEnabled(isEnabled);
	}

    @Override
	public void SENSORS_SENSOR_setDisplayedSensorName(int sid, String displayedSensorName) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setDisplayedSensorName(displayedSensorName);
	}

	@Override
	public void SENSORS_SENSOR_setSampleRate(int sid, int sampleRate) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSampleRate(sampleRate);
	}

    @Override
	public void SENSORS_SENSOR_setSavePeriod(int sid, int savePeriod) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSavePeriod(savePeriod);
	}

	@Override
	public void SENSORS_SENSOR_setSmoothness(int sid, float smoothness) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSmoothness(smoothness);
	}

	@Override
	public void SENSORS_SENSOR_setSensorType(int sid, int sensorType) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setSensorType(SensorType.values()[sensorType]);
	}

	@Override
	public void SENSORS_SENSOR_setGraphType(int sid, int graphType) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setGraphType(GraphType.values()[graphType]);
	}

	@Override
	public void SENSORS_SENSOR_setDisplayedMeasurementUnit(int sid, int displayedMeasurementUnit) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setDisplayedMeasurementUnit(MeasurementUnits.values()[displayedMeasurementUnit]);
	}

    @Override
    public void SENSORS_SENSOR_addRawData(int sid, long time, float[] data) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.addRawData(new SensorData(data, time));
    }

    @Override
	public void SENSORS_SENSOR_setDisplayedMeasurementSystem(int sid, int displayedMeasurementSystem) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return;
        sensor.setDisplayedMeasurementSystem(MeasurementSystems.values()[displayedMeasurementSystem]);
	}

    @Override
    public PSensorData SENSORS_SENSOR_getRawData(int sid) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData().clone());
    }

    @Override
    public PSensorData SENSORS_SENSOR_getRawDataII(int sid, long start, long end) throws RemoteException {
        Sensor sensor;
        sensor = SensorManager.getSensorByID(sid);
        if(sensor == null)
            return null;
        return new PSensorData((java.util.Vector<SensorData>)sensor.getRawData(Utils.longUnixToDate(start), Utils.longUnixToDate(end)).clone());
    }


    // =====================================================================
    //
    // Function calls forward to HAR Module
    //
    // =====================================================================


    @Override
    public void HAR_train(String activity, int windowLength) throws RemoteException {
        ActivityRecognitionModule.getInstance().train(activity, windowLength);
    }

    @Override
    public void HAR_train_SiDD(String activity, int windowLength, long begin, long end) throws RemoteException {
        ActivityRecognitionModule.getInstance().train(activity, windowLength, Utils.longUnixToDate(begin), Utils.longUnixToDate(end));
    }

    @Override
    public void HAR_stopTraining() throws RemoteException {
        ActivityRecognitionModule.getInstance().stopTraining();
    }

    @Override
    public String[] HAR_getActivityNames() throws RemoteException {
        List<String> activities = ActivityRecognitionModule.getInstance().getActivityNames();
        String[] activitiesArray = new String[activities.size()];
        activities.toArray(activitiesArray);
        return activitiesArray;
    }

    @Override
    public boolean HAR_loadNeuralNetwork() throws RemoteException {
        try {
            ActivityRecognitionModule.getInstance().loadNeuralNetwork();
        } catch (FileNotFoundException fileNotFoundException) {
            return false;
        }
        return true;
    }

    @Override
    public boolean HAR_saveNeuralNetwork() throws RemoteException {
        try {
            ActivityRecognitionModule.getInstance().saveNeuralNetwork();
        } catch (FileNotFoundException fileNotFoundException) {
            return false;
        }
        return true;
    }

    @Override
    public boolean HAR_deleteNeuralNetwork() throws RemoteException {
        try {
            ActivityRecognitionModule.getInstance().deleteNeuralNetwork();
        } catch (FileNotFoundException fileNotFoundException) {
            return false;
        }
        return true;
    }

    @Override
    public boolean HAR_createNeuralNetwork() throws RemoteException {
        return ActivityRecognitionModule.getInstance().createNeuralNetwork();
    }

    @Override
    public int HAR_getNeuralNetworkStatus() throws RemoteException {
        return ActivityRecognitionModule.getInstance().getNeuralNetworkStatus().ordinal();
    }

    @Override
    public String[] HAR_getSensors() throws RemoteException {
        List<String> sensorsList = ActivityRecognitionModule.getInstance().getSensors();
        String[] sensorsArray = new String[sensorsList.size()];
        sensorsList.toArray(sensorsArray);
        return sensorsArray;
    }

    @Override
    public String[] HAR_getSupportedActivities() throws RemoteException {
        List<String> supportedActivitiesList = ActivityRecognitionModule.getInstance().getSupportedActivities();
        String[] supportedActivitiesArray = new String[supportedActivitiesList.size()];
        supportedActivitiesList.toArray(supportedActivitiesArray);
        return supportedActivitiesArray;
    }

    @Override
    public boolean HAR_isTraining() throws RemoteException {
        return ActivityRecognitionModule.getInstance().isTraining();
    }

    @Override
    public boolean HAR_isRecognizing() throws RemoteException {
        return ActivityRecognitionModule.getInstance().isRecognizing();
    }

    @Override
    public void HAR_recognize(int windowLength) throws RemoteException {
        ActivityRecognitionModule.getInstance().recognize(windowLength);
    }

    @Override
    public void HAR_stopRecognition() throws RemoteException {
        ActivityRecognitionModule.getInstance().stopRecognition();
    }

    @Override
    public void HAR_addSensor(String sensor) throws RemoteException {
        ActivityRecognitionModule.getInstance().addSensor(sensor);
    }

    @Override
    public void HAR_addActivity(String activity) throws RemoteException {
        ActivityRecognitionModule.getInstance().addActivity(activity);
    }

    @Override
    public void HAR_closeNeuralNetwork() throws RemoteException {
        ActivityRecognitionModule.getInstance().closeNeuralNetwork();
    }

    @Override
    public void HAR_removeSensor(String sensor) throws RemoteException {
        ActivityRecognitionModule.getInstance().removeSensor(sensor);
    }

    @Override
    public void HAR_removeActivity(String activity) throws RemoteException {
        ActivityRecognitionModule.getInstance().removeActivity(activity);
    }
}
