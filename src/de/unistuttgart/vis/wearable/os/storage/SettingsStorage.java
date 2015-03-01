/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.storage;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.service.GarmentOSService;

/**
 * This class is used store the sensor properties and the privacy settings.
 * 
 * @author roehrdor
 */
public class SettingsStorage {
	public static final String FILE_NAME_SENSOR = "storageS";
    public static final String FILE_NAME_APPS = "storageA";

    //
    // Auto update Thread save changes automatically every few seconds
    //
    private static Runnable updater = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                }
                SensorManager.save();
            }
        }
    };

    static {
        new Thread(updater).start();
    }

    /**
     * Read the sensor settings. This function will return null if they have not
     * yet been saved
     *
     * @return the sensor settings
     */
    public static java.util.HashMap<Integer, Sensor> readSensors() {
        android.content.Context context = GarmentOSService.getContext();
        java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME_SENSOR);
        java.io.ObjectInputStream ois = null;
        java.io.FileInputStream fis = null;
        HashMap<Integer, Sensor> ret = null;

        try {
            if (file.exists()) {
                fis = new java.io.FileInputStream(file);
                ois = new java.io.ObjectInputStream(fis);
                ret = (HashMap<Integer, Sensor>)ois.readObject();
            }
        } catch (java.io.IOException ioe) {
        } catch (java.lang.ClassNotFoundException cnfe) {
        } finally {
            try {
                if(ois != null) ois.close();
                if(fis != null) fis.close();
            } catch(IOException ioe) {}
        }
        return ret;
    }

    /**
     * Save the given Map to file for later use
     *
     * @param sensors the map to save
     * @param context the context of the application to choose the right folder
     */
    private static void saveSensors(java.util.Map<Integer, Sensor> sensors, Context context) {
        java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME_SENSOR);
        java.io.ObjectOutputStream oos = null;
        java.io.FileOutputStream fos = null;
        try {
            fos = new java.io.FileOutputStream(file);
            oos = new java.io.ObjectOutputStream(fos);
            oos.writeObject(sensors);

            oos.close();
            fos.close();

        } catch (IOException io) {}
    }


    /**
     * Read the Application settings from the file. This function will return
     * null if they have not yet been saved.
     *
     * @return the application settings
     */
    public static java.util.HashMap<String, UserApp> readApps() {
        android.content.Context context = de.unistuttgart.vis.wearable.os.service.GarmentOSService.getContext();
        java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME_APPS);
        java.io.ObjectInputStream ois = null;
        java.io.FileInputStream fis = null;
        HashMap<String, UserApp> ret = null;

        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);

               ret = (HashMap<String, UserApp>) ois.readObject();
            }
        } catch (java.io.IOException ioe) {
        } catch (java.lang.ClassNotFoundException cnfe) {
        } finally {
            try {
                if(ois != null) ois.close();
                if(fis != null) fis.close();
            } catch(IOException ioe) {}
        }
        return ret;
    }


    /**
     * Save the given Map to file for later use
     *
     * @param app     the map to save
     * @param context the context of the application to choose the right folder
     */
    private static void saveApps(java.util.Map<String, UserApp> app, Context context) {
        java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME_APPS);
        java.io.ObjectOutputStream oos = null;
        java.io.FileOutputStream fos = null;
        try {
            fos = new java.io.FileOutputStream(file);
            oos = new java.io.ObjectOutputStream(fos);

            oos.writeObject(app);

            oos.close();
            fos.close();

        } catch (IOException io) {}
    }


	/**
	 * Save the sensor and the privacy settings to file. If any of the
	 * parameters is null the old value will be kept and now be overwritten.
	 * 
	 * @param sensors
	 *            the sensors settings
	 * @param privacy
	 *            the privacy settings
	 */
	@SuppressLint("UseSparseArrays")
	public static void saveSensorAndPrivacy(
			java.util.Map<Integer, Sensor> sensors,
			java.util.Map<String, UserApp> privacy) {
		android.content.Context context = de.unistuttgart.vis.wearable.os.service.GarmentOSService.getContext();
        if(sensors != null) {
            saveSensors(sensors, context);
        }
        if(privacy != null) {
            saveApps(privacy, context);
        }
	}
}
