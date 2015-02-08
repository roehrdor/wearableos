/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.utils;

/**
 * This class provides some basic utilities that can be used everywhere.
 * 
 * @author roehrdor
 */
public class Utils {
	private Utils() {}
	
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
