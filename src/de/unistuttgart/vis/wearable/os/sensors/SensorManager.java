package de.unistuttgart.vis.wearable.os.sensors;

import android.annotation.SuppressLint;

/**
 * @author pfaehlfd
 */
@SuppressLint("UseSparseArrays")
public final class SensorManager {

    public static final int MAXIMUM_INTERNAL_SENSOR_ID = 64;
	// roehrdor modified
    private static java.util.Map<Integer, Sensor> allSensors = new java.util.HashMap<Integer, Sensor>();

    protected static void addNewSensor(Sensor sensor) {
        allSensors.put(sensor.getSensorID(), sensor);
    }

    public static java.util.Collection<Sensor> getAllSensors () {
        return allSensors.values();
    }
}
