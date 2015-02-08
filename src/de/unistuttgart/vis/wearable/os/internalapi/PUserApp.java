/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.utils.Constants;

/**
 * UserApp object that can be used on the client side.
 * 
 * @author roehrdor
 */
public class PUserApp implements android.os.Parcelable {
	private static final int BASE_PERMISSION_CHECK_FLAG = 0xFF800000;

	private final int ID;
	private String name;
	private java.util.Set<Integer> prohibitedSensors = new java.util.HashSet<Integer>();

	/**
	 * Create a new Object
	 * 
	 * @param name
	 *            the name of the application
	 * @param ID
	 *            the unique id of the application
	 */
	public PUserApp(String name, int ID) {
		this.ID = ID;
		this.name = name;
	}

	// <NSDK>	
	/**
	 * Create a new Object. 
	 * 
	 * @param name
	 *            the name of the application
	 * @param ID
	 *            the unique id of the application
	 * @param prohibitedSensors
	 *            the prohibited sensors
	 */
	public PUserApp(String name, int ID,
			java.util.Set<Integer> prohibitedSensors) {
		this(name, ID);
		this.prohibitedSensors = prohibitedSensors;
	}
	// </NSDK>

	/**
	 * Get the ID of the user app
	 * 
	 * @return the id of the user app
	 */
	public int getID() {
		return this.ID;
	}

	/**
	 * Get the name of the application
	 * 
	 * @return the name of the application
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Check whether the current user settings prohibit the use of the given
	 * sensor for the underlying application
	 * 
	 * @param id
	 *            the sensor id
	 * @return true if the application is not allowed to use data from the given
	 *         sensor
	 */
	public boolean sensorProhibited(int id) {
		// if we already cached this sensor return true
		if (this.prohibitedSensors.contains(id))
			return true;
		
		else {
			// Otherwise check whether the permission is denied in the service
			// if so add it to the local set
			// return accordingly
			boolean is = APIFunctions.PRIVACY_USERAPP_sensorProhibited(this.ID, id);
			if(is) {
				this.prohibitedSensors.add(id);
				return true;
			} else
				return false;			
		}
	}

	/**
	 * Allow the application to use data from the given sensor. This permission
	 * is given until it is revoked again by calling
	 * {@link UserApp#revokePermission(int)}.
	 * 
	 * Note this function will return false if the permission for the sensor is
	 * already granted.
	 * 
	 * @param id
	 *            the sensor id
	 * @return true if the permission has not been granted yet and the
	 *         permission has been changed
	 */
	public boolean grantPermission(int id) {
		prohibitedSensors.remove(id);
		return APIFunctions.PRIVACY_USERAPP_grantPermission(this.ID, id);
	}

	/**
	 * Revoke the permission for the application to use data from the given
	 * sensor. This permission is revoked until it is granted again by calling
	 * {@link UserApp#grantPermission(int)}.
	 * 
	 * Note this function will return false if the permission for the given
	 * sensor has already been revoked and never been granted again.
	 * 
	 * @param id
	 * @return
	 */
	public boolean revokePermission(int id) {
		prohibitedSensors.add(id);
		return APIFunctions.PRIVACY_USERAPP_revokePermission(this.ID, id);
	}

	/**
	 * Allow the user application to use data from the sensor type. If this
	 * settings prohibits the use of a sensor type all data from corresponding
	 * sensors all requests will be automatically denied. To restrict only a
	 * single sensor from multiple sensors for the same type use the function
	 * {@link UserApp#revokePermission(int)}. The permission for the sensor type
	 * is denied until it is granted again by calling the
	 * {@link UserApp#allowSensorType(int)} method.
	 * 
	 * @param flag
	 *            the sensor type permission flag to deny usage of
	 * @return true if the usage is now denied
	 */
	public boolean denySensorType(int flag) {
		// Basic check whether a possibly wrong flag has been set
		if ((BASE_PERMISSION_CHECK_FLAG & flag) != Constants.BASE_PERMISSION)
			return false;		
		return APIFunctions.PRIVACY_USERAPP_denySensorType(this.ID, flag);
	}

	/**
	 * Allow the user application to use data from the given sensor type. Only
	 * in this state the use of {@link UserApp#grantPermission(int)} makes sense
	 * for sensors of the given sensor type. The permission of this sensor type
	 * is granted until {@link UserApp#denySensorType(int)} is called with the
	 * same argument.
	 * 
	 * @param flag
	 *            the sensor type permission flag to allow usage of
	 * @return true if the usage is now permitted
	 */
	public boolean allowSensorType(int flag) {
		// Basic check whether a possibly wrong flag has been set
		if ((BASE_PERMISSION_CHECK_FLAG & flag) != Constants.BASE_PERMISSION)
			return false;		
		return APIFunctions.PRIVACY_USERAPP_allowSensorType(this.ID, flag);
	}

	/**
	 * Tests whether the application is allowed to use this type of sensor.
	 * 
	 * @param flag
	 *            the sensor type permission flag
	 * @return true if the permission is granted
	 */
	public boolean sensorTypeGranted(int flag) {
		if ((BASE_PERMISSION_CHECK_FLAG & flag) != Constants.BASE_PERMISSION)
			return false;		
		return APIFunctions.PRIVACY_USERAPP_sensorTypeGranted(this.ID, flag);
	}

	/**
	 * Grant the application the usage of data gained based on the activity
	 * recognition process
	 */
	public void grantActivityRecognition() {
		APIFunctions.PRIVACY_USERAPP_grantActivityRecognition(this.ID);
	}

	/**
	 * Revoke the permission to use data gained based on the activity
	 * recognition process
	 */
	public void denyActivityRecognition() {
		APIFunctions.PRIVACY_USERAPP_denyActivityRecognition(this.ID);
	}

	/**
	 * Return whether the application is allowed to use data gained from the
	 * activity recognition process
	 * 
	 * @return true if the application is allowed to use data gained from the
	 *         activity recognition process
	 */
	public boolean activityRecognitionGranted() {
		return APIFunctions.PRIVACY_USERAPP_activityRecognitionGranted(this.ID);
	}

	//
	// Creator Object that is used to transmit objects
	//
	public static final android.os.Parcelable.Creator<PUserApp> CREATOR = new android.os.Parcelable.Creator<PUserApp>() {
		@Override
		public PUserApp createFromParcel(android.os.Parcel source) {
			// read the id, the name and the list of prohibited sensors from
			// parcel
			int ID = source.readInt();
			String name = source.readString();
			PUserApp ret = new PUserApp(name, ID);
			int id[] = source.createIntArray();
			for (int i : id)
				ret.prohibitedSensors.add(i);
			return ret;
		}

		@Override
		public PUserApp[] newArray(int size) {
			return new PUserApp[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(android.os.Parcel dest, int flags) {
		// write the ID, the name and the list of prohibited sensors to parcel
		dest.writeInt(this.ID);
		dest.writeString(this.name);
		int[] ar = new int[this.prohibitedSensors.size()];
		int i = -1;
		for(int j : this.prohibitedSensors)
			ar[++i] = j;
		dest.writeIntArray(ar);
	}

}
