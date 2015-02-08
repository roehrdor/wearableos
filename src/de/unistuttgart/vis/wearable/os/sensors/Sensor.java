package de.unistuttgart.vis.wearable.os.sensors;

import android.os.Parcelable;
import java.io.Serializable;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;

/**
 * @author pfaehlfd
 */
public class Sensor implements Parcelable, Serializable {
    private static Vector<Sensor> allSensors = new Vector<Sensor>();

    private static final long serialVersionUID = 8052438921578259544L;
    private int sensorID = -1;
    private String bluetoothID = "";
    private Vector<SensorData> rawData = new Vector<SensorData>();
    private boolean isInternalSensor = false;
    private boolean isEnabled = true;

    int sampleRate = 0;
    int savePeriod = 0;
    String displayedSensorName = "";
    SensorType sensorType = null;
    float smoothness = 0.0f;
    GraphType graphType = null;

    private MeasurementUnits measurementUnit = null;
    private MeasurementSystems sensorMeasurementSystem = null;
    MeasurementSystems displayedMeasurementSystem = null;

    // TODO: merge to this project
    // private SensorCommunication sensorCommunication;

    // TODO: merge to this class
    // public SensorProperties sensorProperties = new SensorProperties();

    /**
     * Use only for external Sensors
     */
    public Sensor() {
        int id = 100;
        for (Sensor sensor : allSensors) {
            if (id < sensor.getSensorID()) {
                id = sensor.getSensorID() + 1;
            }
        }
        sensorID = id;
        allSensors.add(this);
    }

    public int getSensorID() {
        return sensorID;
    }

    public String getBluetoothID() {
        return bluetoothID;
    }

    public void setBluetoothID(String bluetoothID) {
        this.bluetoothID = bluetoothID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean newValue) {
        this.isEnabled = newValue;
        if (newValue) {
            if (isInternalSensor) {
                InternalSensors.getInstance().enableInternalSensor(this);

                APIFunctions.updateSensor(this);

            } else {
                // TODO enable external sensors
            }
        } else {
            if (isInternalSensor) {
                InternalSensors.getInstance().disableInternalSensor(this);

                APIFunctions.updateSensor(this);

            } else {
                // TODO disable external sensors
            }
        }
    }

    public void addRawData (SensorData sensorData) {
        if (!isEnabled) {
            return;
        }
        if (isInternalSensor
                && rawData.size() > 0
                && sensorData.getDate().getTime() < (rawData
                .get(rawData.size() - 1).getDate()
                .getTime() + 1000 / (sampleRate - 1))) {
            return;
        }
        if (rawData.size() > savePeriod) {
            saveRawDataToDatabase();
        }
    }

}
