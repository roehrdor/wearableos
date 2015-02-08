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
	private final java.util.Map<String, UserApp> apps = new java.util.HashMap<String, UserApp>();

	/**
	 * Create a new Privacy manager object, since this is a singleton based
	 * class the constructor needs to be private
	 */
	private PrivacyManager() {
		this.init();
	}

	/**
	 * Initialize the privacy manager and read the saved file
	 */
	private void init() {
		
	}

	/**
	 * Register a new App to the privacy management
	 * 
	 * @param name
	 *            the name of the new app
	 */
	public void registerNewApp(String name) {
		if (!this.apps.containsKey(name))
			this.apps.put(name, new UserApp(name, name.hashCode()));
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
	 * Return all the applications names
	 * 
	 * @return all the applications names as array
	 */
	public String[] getAllAppNames() {
		return (String[]) this.apps.keySet().toArray();
	}

	/**
	 * Return all the user applications
	 * 
	 * @return all the user applications
	 */
	public UserApp[] getAllApps() {
		return (UserApp[]) this.apps.values().toArray();
	}

}
