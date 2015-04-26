/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides some basic utilities that can be used everywhere.
 *
 * @author roehrdor
 */
public class Utils {
    private Utils() {
    }

    /**
     * Sleep for the given amount of time without getting interrupted.
     *
     * @param timeInMSecs the time in mseconds to sleep
     */
    public static void sleepUninterrupted(int timeInMSecs) {
        // compute the end time to stop sleeping at
        long end = System.currentTimeMillis() + timeInMSecs;

        // While we have not reached that time yet
        while(System.currentTimeMillis() < end) {
            try {
                // go for sleep for the remaining time
                long dur = end-System.currentTimeMillis();
                dur = dur < 0 ? 0 : dur;
                Thread.sleep(dur);
            } catch(InterruptedException ie) {
                // do nothing here, we just got woken up, check whether we have
                // any time left to sleep. If so we want to sleep for the
                // remaining time
            }
        }
    }

    /**
     * <p>
     * Convert the given int value into a float value. Note that this function does not cast the
     * value but rather reinterprets the byte representation of the given number and returns the
     * result as floating point value.
     * </p>
     * <p>
     * Note that this function is equivalent to the C / C++ code:
     * float r = *(float*)&v;
     * </p>
     * @param v the integer that is to be reinterpreted
     * @return the float value for the byte representation of the integer value
     */
    public static float getFloatFromIntByte(int v) {
        byte[] b = new byte[4];
        getByteFromInt(b, v);
        return getFloatFromByte(b);
    }

    /**
     * <p>
     * Convert the given float value into a int value. Note that this function does not cast the
     * value but rather reinterprets the byte representation of the given float number and returns
     * the result as integer value
     * </p>
     * <p>
     * Note that this function is equivalent to the C / C++ code:
     * int r = *(int*)&f;
     * </p>
     *
     * @param f the float value that is to be reinterpreted
     * @return the int value for the byte representation of the float value
     */
    public static int getIntFromFloatByte(float f) {
        byte[] b = new byte[4];
        getByteFromFloat(b, f);
        return getIntFromByte(b);
    }

    /**
     * Convert the given float value to the given byte array
     *
     * @param b the byte destination array to store the byte representation fot the float value into
     * @param f the float value to be stored as byte representation in the byte array
     */
    public static void getByteFromFloat(byte[] b, float f) {
        if(b.length < 4)
            return;
        getByteFromInt(b, Float.floatToIntBits(f));
    }

    /**
     * Convert the given byte array to a float number
     *
     * @param b the byte array to convert
     * @return the float number
     */
    public static float getFloatFromByte(byte[] b) {
        return Float.intBitsToFloat(getIntFromByte(b));
    }

    /**
     * Store the byte wise representation of thg given integer value i in the given byte array
     *
     * @param b the byte array to store the byte representation into
     * @param i the integer value to store as byte representation
     */
    public static void getByteFromInt(byte[] b, int i) {
        if(b.length < 4)
            return;
        b[3] = (byte)(i & 0xFF);
        b[2] = (byte)((i >> 0x8) & 0xFF);
        b[1] = (byte)((i >> 0x10) & 0xFF);
        b[0] = (byte)((i >> 0x18) & 0xFF);
    }

    /**
     * Return the integer value to the byte representation in the given array
     *
     * @param b the byte representation of an integer
     * @return the integer
     */
    public static int getIntFromByte(byte[] b) {
        if (b.length < 4)
            return 0;
        return (b[3]) | ((b[2] & 0xFF) << 0x8) | ((b[1] & 0xFF) << 0x10) | ((b[0] & 0xFF) << 0x18);
    }

    /**
     * Create the unix time stamp from the given {@link java.util.Date} object
     *
     * @param date
     *            the date object
     * @return the unix time stamp for the date object
     */
    public static int dateToUnix(java.util.Date date) {
        return (int) (date.getTime() / 1000L);
    }

    /**
     * Create the long unix time stamp from the given {@link java.util.Date} object
     *
     * @param date
     *            the date object
     * @return the long unix time stamp for the date object
     */
    public static long dateToLongUnix(java.util.Date date) {
        return date.getTime();
    }

    /**
     * Create a new {@link java.util.Date} object from the given unix time stamp
     *
     * @param unixTime
     *            the unix time stamp to create a date object for
     * @return the date object for the time stamp
     */
    public static java.util.Date unixToDate(int unixTime) {
        return new java.util.Date(1000L * unixTime);
    }

    /**
     * Create a new {@link java.util.Date} object from the given long unix time stamp
     *
     * @param unixTime
     *            the long unix time stamp to create a date object for
     * @return the date object for the time stamp
     */
    public static java.util.Date longUnixToDate(long unixTime) {
        return new java.util.Date(unixTime);
    }

    /**
     * Return the current unix time stamp as integer value.
     *
     * @return the current unix time stamp
     */
    public static int getCurrentUnixTimeStamp() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    /**
     * Get the current unix date with milli seconds. This functions returns the value of the call to the
     * function {@link System#currentTimeMillis()}
     *
     * @return the current time in unix time with milli seconds
     */
    public static long getCurrentLongUnixTimeStamp() {
        return System.currentTimeMillis();
    }

    private static Map<Integer, Integer> permissionFlags = new HashMap<Integer, Integer>();
    static {
        permissionFlags.put(SensorType.HEARTRATE.ordinal(), Constants.PERMISSION_HEARTRATE);
        permissionFlags.put(SensorType.ACCELEROMETER.ordinal(), Constants.PERMISSION_ACCELEROMETER);
        permissionFlags.put(SensorType.MAGNETIC_FIELD.ordinal(), Constants.PERMISSION_MAGNETIC_FIELD);
        permissionFlags.put(SensorType.GYROSCOPE.ordinal(), Constants.PERMISSION_GYROSCOPE);
        permissionFlags.put(SensorType.LIGHT.ordinal(), Constants.PERMISSION_LIGHT);
        permissionFlags.put(SensorType.PRESSURE.ordinal(), Constants.PERMISSION_PRESSURE);
        permissionFlags.put(SensorType.PROXIMITY.ordinal(), Constants.PERMISSION_PROXIMITY);
        permissionFlags.put(SensorType.GRAVITY.ordinal(), Constants.PERMISSION_GRAVITY);
        permissionFlags.put(SensorType.ROTATION_VECTOR.ordinal(), Constants.PERMISSION_ROTATION_VECTOR);
        permissionFlags.put(SensorType.TEMPERATURE.ordinal(), Constants.PERMISSION_TEMPERATURE);
        permissionFlags.put(SensorType.GPS_SENSOR.ordinal(), Constants.PERMISSION_GPS_SENSOR);
        for(SensorType s : SensorType.values())
            if(permissionFlags.containsKey(s.ordinal()))
                android.util.Log.e("orDEBUG", "Missing " + s.name());
    }

    /**
     * Get the permission flag based on the given sensor type
     *
     * @param sensorType the sensor type to get the permission flag for
     * @return the permission flag for the given value
     */
    public static int permissionFlagFromSensorType(SensorType sensorType) {
        if(sensorType == null)
            return Constants.ILLEGAL_VALUE;
        else
            return permissionFlags.get(sensorType.ordinal());
    }

    /**
     * This function can be used to create an explicit intent from the given
     * implicit one. Android API 21 needs an explicit Intent to start a service
     * from
     *
     * @param context
     *            the context of the application
     * @param implicitIntent
     *            the implicit created Intent
     * @return the corresponding explicit content for the given implicit one
     */
    public static android.content.Intent explicitFromImplicit(
            android.content.Context context,
            android.content.Intent implicitIntent) {
        android.content.pm.PackageManager pm = context.getPackageManager();
        java.util.List<android.content.pm.ResolveInfo> resolveInfo = pm
                .queryIntentServices(implicitIntent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        android.content.pm.ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        android.content.ComponentName component = new android.content.ComponentName(
                packageName, className);
        android.content.Intent explicitIntent = new android.content.Intent(
                implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /**
     * Checks for free space at the specified directory to give feedback whether an export to that path is possible
     * @param path The target directory for the archive
     * @param context The context of the activity that calls this method
     * @return the possibility to save the sensor archive in the desired directory
     */
    public static boolean enoughExternalSpaceAvailable(File path, Context context){
        long requiredSpace = 0;
        for(int i=0; i<context.getFilesDir().getAbsoluteFile().listFiles().length;i++){
            requiredSpace+=context.getFilesDir().getAbsoluteFile().listFiles()[i].length();
        }
        StatFs internalStorageStat = new StatFs(path.getAbsolutePath());
        long targetDirectorySize;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            targetDirectorySize = internalStorageStat.getAvailableBlocksLong()*internalStorageStat.getBlockSizeLong();
        } else {
            targetDirectorySize = (long)internalStorageStat.getAvailableBlocks()*(long)internalStorageStat.getBlockSize();
        }
        return targetDirectorySize>requiredSpace;
    }

    /**
     * Checks the free space that is available at the internal storage
     * @return the internal space available for the archive import in byte size
     */
    public static long getAvailableInternalSpace(){
        long targetDirectorySize;
        StatFs internalStorageStat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            targetDirectorySize = internalStorageStat.getAvailableBlocksLong()*internalStorageStat.getBlockSizeLong();
        } else {
            targetDirectorySize = (long)internalStorageStat.getAvailableBlocks()*(long)internalStorageStat.getBlockSize();
        }
        return targetDirectorySize;
    }
}
