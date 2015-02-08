/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.storage;

import android.annotation.SuppressLint;
import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.service.GarmentOSSerivce;

/**
 * This class is used store the sensor properties and the privacy settings.
 * 
 * @author roehrdor
 */
public class SettingsStorage {
	private static final String FILE_NAME = "storage";

	/**
	 * Read the sensor settings. This function will return null if they have not
	 * yet been saved
	 * 
	 * @return the sensor settings
	 */
	@SuppressWarnings({ "unchecked" })
	public static java.util.HashMap<Integer, Sensor> readSensors() {
		android.content.Context context = GarmentOSSerivce.getContext();
		java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME);
		java.io.ObjectInputStream ois = null;
		java.io.FileInputStream fis = null;
		Object ret = null;

		try {
			//
			// If the file does exist we read the file, otherwise not
			//
			if (file.exists()) {
				//
				// Read the contents of the file and save the
				//
				fis = new java.io.FileInputStream(file);
				ois = new java.io.ObjectInputStream(fis);
				ret = ois.readObject();
				ois.close();
				fis.close();
			}
		} catch (java.io.IOException ioe) {
		} catch (java.lang.ClassNotFoundException cnfe) {
		}
		return (java.util.HashMap<Integer, Sensor>) ret;
	}

	/**
	 * Read the Application settings from the file. This function will return
	 * null if they have not yet been saved.
	 * 
	 * @return the application settings
	 */
	@SuppressWarnings("unchecked")
	public static java.util.HashMap<String, UserApp> readApps() {
		android.content.Context context = GarmentOSSerivce.getContext();
		java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME);
		java.io.ObjectInputStream ois = null;
		java.io.FileInputStream fis = null;
		Object ret = null;

		try {
			//
			// If the file does exist we read the file, otherwise not
			//
			if (file.exists()) {
				//
				// Read the contents of the file and save the
				//
				fis = new java.io.FileInputStream(file);
				ois = new java.io.ObjectInputStream(fis);
				ois.readObject();
				ret = ois.readObject();
				ois.close();
				fis.close();
			}
		} catch (java.io.IOException ioe) {
		} catch (java.lang.ClassNotFoundException cnfe) {
		}
		return (java.util.HashMap<String, UserApp>) ret;
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
		android.content.Context context = GarmentOSSerivce.getContext();
		java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME);
		java.io.ObjectOutputStream oos = null;
		java.io.ObjectInputStream ois = null;
		java.io.FileInputStream fis = null;
		java.io.FileOutputStream fos = null;

		try {
			//
			// If the file does already exist
			//
			if (file.exists()) {
				//
				// we must make sure to keep all the contents
				// Therefore we start by reading all the content from the file
				//
				fis = new java.io.FileInputStream(file);
				ois = new java.io.ObjectInputStream(fis);
				Object sensorsRead = ois.readObject();
				Object privacyRead = ois.readObject();
				ois.close();
				fis.close();

				//
				// When we have read the content we can create a new file and
				// write the content again
				//
				fos = new java.io.FileOutputStream(file);
				oos = new java.io.ObjectOutputStream(fos);

				//
				// If sensors is null we want to write the read value to keep
				// our settings
				//
				if (sensors == null)
					oos.writeObject(sensorsRead);
				else
					oos.writeObject(sensors);

				//
				// If privacy is null we want to write the read value to keep
				// our settings
				//
				if (privacy == null)
					oos.writeObject(privacyRead);
				else
					oos.writeObject(privacy);

				fos.close();
				oos.close();
			}

			else {
				//
				// In case this is a new file we can start writing without
				// reading anything
				//
				fos = new java.io.FileOutputStream(file);
				oos = new java.io.ObjectOutputStream(fos);

				//
				// If valid sensors have not been passed insert a dummy one to
				// maintain the file structure. Otherwise write the provided
				// sensors settings
				//
				if (sensors == null)
					oos.writeObject(new java.util.HashMap<Integer, Sensor>());
				else
					oos.writeObject(sensors);

				//
				// If valid privacy settings have not been passed insert a dummy
				// one to maintain the file structure. Otherwise write the
				// provided privacy settings
				//
				if (privacy == null)
					oos.writeObject(new java.util.HashMap<String, UserApp>());
				else
					oos.writeObject(privacy);

				fos.close();
				oos.close();
			}
		} catch (java.io.IOException ioe) {
		} catch (java.lang.ClassNotFoundException cnfe) {
		}
	}
}
