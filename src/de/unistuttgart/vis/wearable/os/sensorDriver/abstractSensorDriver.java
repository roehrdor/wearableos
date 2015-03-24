package de.unistuttgart.vis.wearable.os.sensorDriver;

import de.unistuttgart.vis.wearable.os.sensors.Sensor;

/**
 * Created by lorenzma on 06.03.15.
 */
public abstract class abstractSensorDriver {

    public abstract void encodeData(Sensor saveSensor, byte[] sensorData);

    private static abstractSensorDriver Instance;

    public static abstractSensorDriver getInstance() {
        return Instance;
    }

    public abstractSensorDriver(){
        if (Instance == null) {
            SensorDriverManager.addSensorDrivers(this);
            Instance =  this;
        }
    }
}
