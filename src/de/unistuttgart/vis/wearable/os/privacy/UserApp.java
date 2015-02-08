/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.privacy;

import de.unistuttgart.vis.wearable.os.utils.Constants;

/**
 * This class represents a Application that makes use of our Garment OS Service.
 * This class saves the package name of the application and the permissions the
 * user has given to this application.
 * 
 * @author roehrdor
 */
public class UserApp {
	private static final int BASE_PERMISSION_CHECK_FLAG = 0xFF800000;

	private final int ID;
	private String name;
	private java.util.Set<Integer> prohibitedSensors = new java.util.HashSet<Integer>();
	private int permissionFlags = Constants.BASE_PERMISSION;
	private boolean activityRecognitionGranted = false;

	/**
	 * Create a new UserApp object with the given name
	 * 
	 * @param name
	 *            the name of the application
	 */
	public UserApp(String name, int ID) {
		this.name = name;
		this.ID = ID;
	}

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
		boolean ret = prohibitedSensors.contains(id);
		PrivacyManager.instance.save();
		return ret;
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
		boolean ret = prohibitedSensors.remove(id);
		PrivacyManager.instance.save();
		return ret;
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
		boolean ret = prohibitedSensors.add(id);
		PrivacyManager.instance.save();
		return ret;
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
		this.permissionFlags |= flag;
		PrivacyManager.instance.save();
		return true;
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
		this.permissionFlags ^= (flag ^ Constants.BASE_PERMISSION);
		PrivacyManager.instance.save();
		return true;
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
		return (this.permissionFlags & flag) == Constants.BASE_PERMISSION;
	}

	/**
	 * Grant the application the usage of data gained based on the activity
	 * recognition process
	 */
	public void grantActivityRecognition() {
		this.activityRecognitionGranted = true;
		PrivacyManager.instance.save();
	}

	/**
	 * Revoke the permission to use data gained based on the activity
	 * recognition process
	 */
	public void denyActivityRecognition() {
		this.activityRecognitionGranted = false;
		PrivacyManager.instance.save();
	}

	/**
	 * Return whether the application is allowed to use data gained from the
	 * activity recognition process
	 * 
	 * @return true if the application is allowed to use data gained from the
	 *         activity recognition process
	 */
	public boolean activityRecognitionGranted() {
		return this.activityRecognitionGranted;
	}	
}
