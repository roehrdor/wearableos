package de.unistuttgart.vis.wearable.os.sensors;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.storage.SensorDataDeSerializer;
import de.unistuttgart.vis.wearable.os.storage.SensorDataSerializer;

/**
 * @author pfaehlfd
 */
public class Sensor implements Serializable {

    private static final long serialVersionUID = 8052438921578259544L;
    private int sensorID = -1;
    private String bluetoothID = "";
    private Vector<SensorData> rawData = new Vector<SensorData>();
    private boolean isInternalSensor = false;

    private boolean isEnabled = true;

    private int sampleRate = 0;
    private int savePeriod = 0;
    private String displayedSensorName = "";
    private SensorType sensorType = null;
    private float smoothness = 0.0f;
    private GraphType graphType = null;

    private MeasurementUnits rawDataMeasurementUnit = null;
    private MeasurementSystems rawDataMeasurementSystem = null;
    private MeasurementUnits displayedMeasurementUnit = null;
    private MeasurementSystems displayedMeasurementSystem = null;

    // TODO: merge to this project
    // private SensorCommunication sensorCommunication;

    // TODO: merge to this class
    // public SensorProperties sensorProperties = new SensorProperties();

    /**
     * Use only for external Sensors
     */
    public Sensor() {
        int id = 100;
        for (Sensor sensor : SensorManager.getAllSensors()) {
            if (id <= sensor.getSensorID()) {
                id = sensor.getSensorID() + 1;
            }
        }
        sensorID = id;
        SensorManager.addNewSensor(this);
    }

    public boolean isInternalSensor() {
        return isInternalSensor;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean newValue) {
        this.isEnabled = newValue;
        if (newValue) {
            if (isInternalSensor) {
                //TODO
                /////InternalSensors.getInstance().enableInternalSensor(this);

                /////APIFunctions.updateSensor(this);

            } else {
                // TODO: enable external sensors
            }
        } else {
            if (isInternalSensor) {
                //TODO
                /////InternalSensors.getInstance().disableInternalSensor(this);

                ///// APIFunctions.updateSensor(this);

            } else {
                // TODO: disable external sensors
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
            SensorDataSerializer serializer = new SensorDataSerializer(sensorID, rawData);
            new Thread(serializer).start();
            rawData.clear();
        }
    }

    public Vector<SensorData> getRawData() {
        return rawData;
    }

    public Vector<SensorData> getRawData(Date time, boolean plusMinusOneSecond) {
        if (!plusMinusOneSecond) {
            return getRawData(time, time);
        } else {
            Date begin = time;
            begin.setSeconds(time.getSeconds() - 1);
            Date end = time;
            end.setSeconds(time.getSeconds() + 1);
            return getRawData(begin, end);
        }
    }

    public Vector<SensorData> getRawData(Date begin, Date end) {
        Date firstLocallyHoldDate;
        Vector<SensorData> data = new Vector<SensorData>();
        if (rawData.size() == 0) {
            firstLocallyHoldDate = new Date();
        } else {
            firstLocallyHoldDate = rawData.get(0).getDate();
        }
        if (begin.after(firstLocallyHoldDate) || begin.equals(firstLocallyHoldDate)) {
            getRawData(begin, end, data);
        } else {
            //TODO
            /////SensorDataDeSerializer deSerializer =
            /////        new SensorDataDeSerializer(sensorID, data, begin, end, null);
            /////deSerializer.work();
            if (!end.before(firstLocallyHoldDate)) {
                getRawData(begin, end, data);
            }
        }
        return data;
    }

    private void getRawData (Date begin, Date end, Vector<SensorData> data) {
        for (SensorData sensorData : rawData) {
            if ((sensorData.getDate().before(end) || sensorData.getDate()
                    .equals(end))
                    && (sensorData.getDate().after(begin) || sensorData
                    .getDate().equals(begin))) {
                data.add(sensorData);
            }
        }
    }

    public void setRawDataMeasurementSystemAndUnit(
            MeasurementSystems rawDataMeasurementSystem,
            MeasurementUnits rawDataMeasurementUnit) {
        if (!rawDataMeasurementUnit
                .containsMeasurementSystem(rawDataMeasurementSystem)) {
            throw new IllegalArgumentException();
        }
        this.rawDataMeasurementSystem = rawDataMeasurementSystem;
        this.rawDataMeasurementUnit = rawDataMeasurementUnit;
    }

    public void setDisplayedMeasurementSystemAndUnit(
            MeasurementSystems displayedMeasurementSystem,
            MeasurementUnits displayedMeasurementUnit) {
        if (!displayedMeasurementUnit
                .containsMeasurementSystem(displayedMeasurementSystem)) {
            throw new IllegalArgumentException();
        }
        this.displayedMeasurementSystem = displayedMeasurementSystem;
        this.displayedMeasurementUnit = displayedMeasurementUnit;
    }

    public int getSensorID() {
        return sensorID;
    }
}