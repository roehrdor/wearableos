package de.unistuttgart.vis.wearable.os.privacy;

import de.unistuttgart.vis.wearable.os.storage.SettingsStorage;

/**
 * The Privacy Manager manages the user application and the permissions granted.
 * 
 * @author roehrdor
 */
public class PrivacyManager {
	// Singleton, we do not allow any other instance. This instance is final so
	// we can allow accessing it directly
	public static final PrivacyManager instance = new PrivacyManager();

	//
	// A map that maps the application names to the user application objects and
	// a lock to allow concurrent processing
	//
	private java.util.Map<String, UserApp> apps = null;	
	private android.util.SparseArray<UserApp> spapps = new android.util.SparseArray<UserApp>();

	/**
	 * Create a new Privacy manager object, since this is a singleton based
	 * class the constructor needs to be private
	 */
	private PrivacyManager() {
		this.apps = SettingsStorage.readApps();
		if(this.apps == null)
			 this.apps = new java.util.HashMap<String, UserApp>();
		for(UserApp ua : this.apps.values())
			this.spapps.put(ua.getID(), ua);
	}

	/**
	 * Register a new App to the privacy management
	 * 
	 * @param name
	 *            the name of the new app
	 */
	public void registerNewApp(String name) {
		if (!this.apps.containsKey(name)) {
			UserApp ua = new UserApp(name, name.hashCode());
			this.apps.put(name, ua);
			this.spapps.put(name.hashCode(), ua);
		}
		this.save();
	}

	/**
	 * Save the current settings to file
	 */
	public void save() {
		SettingsStorage.saveSensorAndPrivacy(null, this.apps);
	}

	/**
	 * Get the application by the name, returns null if application unknown
	 * 
	 * @param name
	 *            the name of the application
	 * @return the application object or null if not known
	 */
	public UserApp getApp(String name) {
		return this.apps.get(name);
	}
	
	/**
	 * Get the application by the id, returns null if application unknown
	 * 
	 * @param id
	 *            the id of the application
	 * @return the application object or null if not known
	 */
	public UserApp getApp(int id) {
		return this.spapps.get(id);
	}

	/**
	 * Return all the applications names
	 * 
	 * @return all the applications names as array
	 */
	public String[] getAllAppNames() {
		String[] s = new String[this.apps.keySet().size()];
		this.apps.keySet().toArray(s);
		return s;
	}

	/**
	 * Return all the user applications
	 * 
	 * @return all the user applications
	 */
	public UserApp[] getAllApps() {
		UserApp[] ua = new UserApp[this.apps.keySet().size()];
		this.apps.keySet().toArray(ua);
		return ua;
	}

}
