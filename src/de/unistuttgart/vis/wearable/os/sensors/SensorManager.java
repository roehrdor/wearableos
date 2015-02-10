package de.unistuttgart.vis.wearable.os.sensors;

import de.unistuttgart.vis.wearable.os.storage.SettingsStorage;

/**
 * The server manager keeps track of all sensors that are used by GarmentOS. New Sensors shall be
 * registered here. This class also offers the ability to get any sensor according to its ID.
 *
 * @author pfaehlfd, roehrdor
 */
@android.annotation.SuppressLint("UseSparseArrays")
public final class SensorManager {
    // Reserve the first 64 IDs for internal sensors only
    public static final int MAXIMUM_INTERNAL_SENSOR_ID = 0x40;

    //
    // Map to store the different sensors. The map allows us to get the sensor object
    // by knowing its ID
    //
    private static java.util.Map<Integer, Sensor> allSensors = null;

    //
    // Read the saved sensors from the file. If we have not saved any sensors yet create a map
    //
    static {
        allSensors = SettingsStorage.readSensors();
        if(allSensors == null)
            allSensors = new java.util.HashMap<Integer, Sensor>();
    }

    /**
     * We do not want anybody to create a object from this class
     */
    private SensorManager() {
        throw new IllegalAccessError("This constructor shall not be called");
    }


    /**
     * Add a new Sensor to GarmentOS. To call this function a Sensor object is required.
     *
     * @param sensor the sensor to add to the system
     */
    protected static void addNewSensor(Sensor sensor) {
        allSensors.put(sensor.getSensorID(), sensor);
    }

    /**
     * Save the current settings
     */
    public static void save() {
        SettingsStorage.saveSensorAndPrivacy(allSensors, null);
    }

    /**
     * This functions returns an array containing all the sensor names that are known by
     * GarmentOS. Note GarmentOS does not prohibit to give the same name to different sensors.
     * To uniquely identify a sensor consider using the ID and not the name.
     *
     * @return a String array containing all the sensor names that are known by GarmentOS
     */
    public static String[] getSensorNames() {
        String[] names = new String[allSensors.size()];
        int i = -1;
        for( Sensor s : allSensors.values() )
            names[++i] = s.getDisplayedSensorName();
        return names;
    }

    /**
     * Get the sensor that is registered with the given ID. If the ID is not known to GarmentOS
     * null will be returned by this function.
     *
     * @param id the id to search for.
     * @return the Sensor object for the ID or null if the ID is unknown
     */
    public static Sensor getSensorByID(int id) {
        return allSensors.get(id);
    }

    /**
     * Return a collection with all the registered Sensors by GarmentOS.
     *
     * @return a collection containing all sensors known by GarmentOS
     */
    public static java.util.Collection<Sensor> getAllSensors () {
        return allSensors.values();
    }
}
