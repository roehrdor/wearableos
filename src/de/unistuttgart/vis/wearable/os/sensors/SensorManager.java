/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.sensors;

import de.unistuttgart.vis.wearable.os.storage.SettingsStorage;

import java.util.*;

/**
 * The server manager keeps track of all sensors that are used by GarmentOS. New Sensors shall be
 * registered here. This class also offers the ability to get any sensor according to its ID.
 *
 * @author pfaehlfd, roehrdor
 */
@android.annotation.SuppressLint("UseSparseArrays")
public final class SensorManager {


    /**
     * Sort the Map by the sensor names so the order they will be returned in is alphabetical
     * This function shall usually be called after inserting any new sensor into the map
     *
     * @param map the map to be sorted
     * @return the sorted map
     */
    protected static Map<Integer, Sensor> sortByValues(java.util.Map<Integer, Sensor> map) {
        List<Map.Entry<Integer, Sensor>> entries = new LinkedList<Map.Entry<Integer, Sensor>>((map.entrySet()));
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Sensor>>() {
            @Override
            public int compare(Map.Entry<Integer, Sensor> lhs, Map.Entry<Integer, Sensor> rhs) {
                return lhs.getValue().getDisplayedSensorName().compareTo(rhs.getValue().getDisplayedSensorName());
            }
        });
        Map<Integer, Sensor> sortedMap = new LinkedHashMap<Integer, Sensor>();
        for(Map.Entry<Integer, Sensor> entry : entries)
            sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }

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
            allSensors = new java.util.LinkedHashMap<Integer, Sensor>();
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
    public static void addNewSensor(Sensor sensor) {
        // Insert the sensor into the map and afterwards sort it
        // this is actually better than sorting the map every time before returning it
        allSensors.put(sensor.getSensorID(), sensor);
        allSensors = sortByValues(allSensors);
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

    /**
     * Return a collection with all the registered Sensors with thge given sensorTypeby GarmentOS.
     *
     * @return a collection containing all sensors with the given SensorType known by GarmentOS
     */
    public static java.util.Collection<Sensor> getAllSensors (SensorType sensorType) {
        java.util.Map<Integer, Sensor> sensorsToReturn =
                new java.util.HashMap<Integer, Sensor>();
        for (Sensor sensor : allSensors.values()) {
            if (sensor.getSensorType() == sensorType) {
                sensorsToReturn.put(sensor.getSensorID(), sensor);
            }
        }
        return sensorsToReturn.values();
    }

    public static void removeSensor(int id) {
        allSensors.remove(id);
    }
}
