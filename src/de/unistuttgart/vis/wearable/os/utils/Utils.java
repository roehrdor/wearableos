package de.unistuttgart.vis.wearable.os.utils;

/**
 * 
 * @author roehrdor
 */
public class Utils {

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
