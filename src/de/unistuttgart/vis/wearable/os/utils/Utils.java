/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.utils;

/**
 * This class provides some basic utilities that can be used everywhere.
 * 
 * @author roehrdor
 */
public class Utils {
	private Utils() {
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
	 * Return the current unix time stamp as integer value.
	 * 
	 * @return the current unix time stamp
	 */
	public static int getCurrentUnixTimeStamp() {
		return (int) (System.currentTimeMillis() / 1000L);
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
}
