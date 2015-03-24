package de.unistuttgart.vis.wearable.os.sensorDriver;

import java.util.Vector;

/**
 * Created by lorenzma on 06.03.15.
 */
public class SensorDriverManager {

    static Vector<abstractSensorDriver> SensorDrivers = new Vector<abstractSensorDriver>();

    public static void getSensorDrivers(){

    }

    public static void addSensorDrivers(abstractSensorDriver newSensorDriver) {
       SensorDrivers.add(newSensorDriver);
    }

}
