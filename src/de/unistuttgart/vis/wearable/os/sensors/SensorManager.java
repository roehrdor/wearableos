package de.unistuttgart.vis.wearable.os.sensors;

/**
 * @author pfaehlfd
 */
public final class SensorManager {
	// roehrdor modified
    private static java.util.Map<Integer, Sensor> allSensors = new java.util.HashMap<Integer, Sensor>();

    protected static void addNewSensor(Sensor sensor) {
        allSensors.put(sensor.getSensorID(), sensor);
    }

    public static java.util.Collection<Sensor> getAllSensors () {
        return allSensors.values();
    }
}
