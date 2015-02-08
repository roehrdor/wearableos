package de.unistuttgart.vis.wearable.os.sensors;

import java.util.Vector;

/**
 * @author pfaehlfd
 */
public final class SensorManager {
    private static Vector<Sensor> allSensors = new Vector<Sensor>();

    protected static void addNewSensor(Sensor sensor) {
        allSensors.add(sensor);
    }

    public static Vector<Sensor> getAllSensors () {
        return allSensors;
    }
}
