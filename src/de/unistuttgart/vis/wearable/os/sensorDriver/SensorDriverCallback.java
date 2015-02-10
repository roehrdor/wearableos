/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.sensorDriver;

import de.unistuttgart.vis.wearable.os.sensors.SensorData;

/**
 * This handle is called from the {@link de.unistuttgart.vis.wearable.os.sensorDriver.GenericSensorDriver}
 * after the {@link GenericSensorDriver#execute()} function is executed.
 *
 * @author roehrdor
 */
public interface SensorDriverCallback {

    /**
     * This handle is called from the {@link de.unistuttgart.vis.wearable.os.sensorDriver.GenericSensorDriver}
     * after the {@link GenericSensorDriver#execute()} function is executed. The parameter is the
     * newly read sensor data object
     *
     * @param sensorData the newly read sensor data
     */
    public void callback(SensorData sensorData);
}
