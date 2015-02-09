package de.unistuttgart.vis.wearable.os.sensors;

import android.annotation.SuppressLint;

/**
 * @author pfaehlfd
 */
@SuppressLint("UseSparseArrays")
public final class SensorManager {

    public static final int MAXIMUM_INTERNAL_SENSOR_ID = 64;

    private static java.util.Map<Integer, Sensor> allSensors = new java.util.HashMap<Integer, Sensor>();

    protected static void addNewSensor(Sensor sensor) {
        allSensors.put(sensor.getSensorID(), sensor);
    }

    public static String[] getSensorNames() {
        String[] names = new String[allSensors.size()];
        int i = -1;
        for( Sensor s : allSensors.values() )
            names[++i] = s.getDisplayedSensorName();
        return names;
    }

    public static Sensor getSensorByID(int id) {
        return allSensors.get(id);
    }

    public static java.util.Collection<Sensor> getAllSensors () {
        return allSensors.values();
    }
}
