package de.unistuttgart.vis.wearable.os.sensors;

import android.annotation.SuppressLint;

/**
 * @author pfaehlfd
 */
@SuppressLint("UseSparseArrays")
public final class SensorManager {
	// roehrdor modified
    private static java.util.Map<Integer, Sensor> allSensors = new java.util.HashMap<Integer, Sensor>();

    protected static void addNewSensor(Sensor sensor) {
        allSensors.put(sensor.getSensorID(), sensor);
    }

    public static Sensor getSensorByID(int id) {
        return allSensors.get(id);
    }

    public static java.util.Collection<Sensor> getAllSensors () {
        return allSensors.values();
    }
}
