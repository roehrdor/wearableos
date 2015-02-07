package de.unistuttgart.vis.wearable.os.utils;

public class Constants {
	private Constants(){}
	
	public static final int CALLBACK = 0x80000000;
	public static final int CALLBACK_DEBUG_VALUE = 0x80000001; 
	
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
	
	
}
