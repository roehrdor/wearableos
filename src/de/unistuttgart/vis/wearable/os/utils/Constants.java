/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.utils;

/**
 * This file defines constants used in the Garment OS system
 *  
 * @author roehrdor
 */
public class Constants {
	private Constants(){}
	
	public static final int CALLBACK = 0x80000000;
	public static final int CALLBACK_DEBUG_VALUE = 0x80000001;
	
	public static final int ENUMERATION_NULL = 0x01000001;
	
	
	//
	// Permissions
	//
	public static final int BASE_PERMISSION = 0x00800000;
	public static final int PERMISSION_GPS_SENSOR = 0x00800001;
	public static final int PERMISSION_HEARTRATE = 0x00800002;
	public static final int PERMISSION_ACCELEROMETER = 0x00800004;
	public static final int PERMISSION_MAGNETIC_FIELD = 0x00800008;
	public static final int PERMISSION_GYROSCOPE = 0x00800010;
	public static final int PERMISSION_LIGHT = 0x00800020;
	public static final int PERMISSION_PRESSURE = 0x00800040;
	public static final int PERMISSION_PROXIMITY = 0x00800080;
	public static final int PERMISSION_GRAVITY = 0x00800100;	
	public static final int PERMISSION_ROTATION_VECTOR = 0x00800200;
	public static final int PERMISSION_RELATIVE_HUMIDITY = 0x00800400;
	public static final int PERMISSION_TEMPERATURE = 0x00800800;	
	
	
	//
	// Constant IDS for internal sensors
	//
	public static final int INTERNAL_HEARTRATE_SENSOR = 0x00000001;
	public static final int INTERNAL_ACCELEROMETER_SENSOR = 0x00000002;
	public static final int INTERNAL_MAGNETIC_FIELD_SENSOR = 0x00000003;
	public static final int INTERNAL_GYROSCOPE_SENSOR = 0x00000004;
	public static final int INTERNAL_LIGHT_SENSOR = 0x00000005;
	public static final int INTERNAL_PRESSURE_SENSOR = 0x00000006;
	public static final int INTERNAL_PROXIMITY_SENSOR = 0x00000007;
	public static final int INTERNAL_GRAVITY_SENSOR = 0x00000008;
	public static final int INTERNAL_ROTATION_VECTOR_SENSOR = 0x00000009;
	public static final int INTERNAL_RELATIVE_HUMIDITY_SENSOR = 0x0000000A;
	public static final int INTERNAL_TEMPERATURE_SENSOR = 0x0000000B;
	public static final int INTERNAL_GPS_SENSOR_SENSOR = 0x0000000C;

}
