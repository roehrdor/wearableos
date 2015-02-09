package de.unistuttgart.vis.wearable.os.sensors;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.sensorDriver.SensorDriver;
import de.unistuttgart.vis.wearable.os.storage.SensorDataDeSerializer;
import de.unistuttgart.vis.wearable.os.storage.SensorDataSerializer;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * @author pfaehlfd
 */
public class Sensor implements Serializable {

    private static final long serialVersionUID = 8052438921578259544L;

    private Vector<SensorData> rawData = new Vector<SensorData>();

    private boolean isInternalSensor = false;
    SensorDriver sensorDriver = null;

    private boolean isEnabled = false;

    private int sensorID = -1;
    private String bluetoothID = "";
    private int sampleRate = 0;
    private float smoothness = 0.0f;
    private int savePeriod = 0;
    private String displayedSensorName = "";
    private SensorType sensorType = null;
    private GraphType graphType = null;

    private MeasurementUnits rawDataMeasurementUnit = null;
    private MeasurementSystems rawDataMeasurementSystem = null;
    private MeasurementUnits displayedMeasurementUnit = null;
    private MeasurementSystems displayedMeasurementSystem = null;

    // TODO: merge to this project
    // private SensorCommunication sensorCommunication;

    /**
     * Creates a new Sensor, assigns the given values
     * and adds the Sensor to the SensorManagers sensor list.
     * The new Sensor will have the lowest external sensorID,
     * which is not forgiven yet.
     * Use only for external Sensors
     */
    public Sensor(SensorDriver sensorDriver, int sampleRate, int savePeriod, int smoothness,
                  String displayedSensorName, SensorType sensorType, String bluetoothID,
                  MeasurementSystems rawDataMeasurementSystem, MeasurementUnits rawDataMeasurementUnit,
                  MeasurementSystems displayedMeasurementSystem, MeasurementUnits displayedMeasurementUnit) {
        isInternalSensor = false;

        // compute the lowest external sensorID which is not forgiven yet
        int id = 100;
        for (Sensor sensor : SensorManager.getAllSensors()) {
            if (id <= sensor.getSensorID()) {
                id = sensor.getSensorID() + 1;
            }
        }

        sensorID = id;

        this.sensorDriver = sensorDriver;
        this.sampleRate = sampleRate;
        this.savePeriod = savePeriod;
        this.smoothness = smoothness;
        this.displayedSensorName = displayedSensorName;
        this.sensorType = sensorType;
        this.bluetoothID = bluetoothID;
        this.rawDataMeasurementSystem = rawDataMeasurementSystem;
        this.rawDataMeasurementUnit = rawDataMeasurementUnit;
        this.displayedMeasurementSystem = displayedMeasurementSystem;
        this.displayedMeasurementUnit = displayedMeasurementUnit;
        this.graphType = GraphType.LINE;
        this.isEnabled = true;

        SensorManager.addNewSensor(this);
    }

    /**
     * Creates a new Sensor, assigns the given values
     * and adds the Sensor to the SensorManagers sensor list.
     * The new Sensor will have the same sensorID as in Android.
     * (see android.hardware.Sensor
     * http://developer.android.com/reference/android/hardware/Sensor.html)
     * Use only for internal Sensors
     */
    protected Sensor(android.hardware.Sensor sensor, int sampleRate, int savePeriod,
                     float smoothness, String displayedSensorName, SensorType sensorType,
                     MeasurementSystems rawDataMeasurementSystem,
                     MeasurementUnits rawDataMeasurementUnit) {
        this.isInternalSensor = true;

        this.sensorID = sensor.getType();
        this.sampleRate = sampleRate;
        this.savePeriod = savePeriod;
        this.smoothness = smoothness;
        this.displayedSensorName = displayedSensorName;
        this.sensorType = sensorType;
        this.rawDataMeasurementSystem = rawDataMeasurementSystem;
        this.displayedMeasurementSystem = rawDataMeasurementSystem;
        this.rawDataMeasurementUnit = rawDataMeasurementUnit;
        this.displayedMeasurementUnit = rawDataMeasurementUnit;
        this.graphType = GraphType.LINE;

        SensorManager.addNewSensor(this);
    }

    /**
     * Creates a new GPS Sensor, assigns the given values
     * and adds the Sensor to the SensorManagers sensor list.
     * Use only for internal GPS Sensors
     */
    protected Sensor(int gpsSensorID, int sampleRate, int savePeriod,
                     float smoothness, String displayedSensorName, SensorType sensorType,
                     MeasurementSystems rawDataMeasurementSystem,
                     MeasurementUnits rawDataMeasurementUnit) {
        this.isInternalSensor = true;

        this.sensorID = gpsSensorID;
        this.sampleRate = sampleRate;
        this.savePeriod = savePeriod;
        this.smoothness = smoothness;
        this. displayedSensorName = displayedSensorName;
        this.sensorType = sensorType;
        this.rawDataMeasurementSystem = rawDataMeasurementSystem;
        this.displayedMeasurementSystem = rawDataMeasurementSystem;
        this.rawDataMeasurementUnit = rawDataMeasurementUnit;
        this.displayedMeasurementUnit = rawDataMeasurementUnit;
        this.graphType = GraphType.LINE;

        SensorManager.addNewSensor(this);
    }

    /**
     * returns if the sensor is an internal Sensor.
     */
    public boolean isInternalSensor() {
        return isInternalSensor;
    }

    /**
     * returns if the sensor is enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * sets the enabled status to the given value.
     * => enables or disables the sensor
     */
    public void setEnabled(boolean newValue) {
        this.isEnabled = newValue;
        if (newValue) {
            if (isInternalSensor) {
                InternalSensors.getInstance().enableInternalSensor(this);
            } else {
                // TODO: enable external sensors
            }
        } else {
            if (isInternalSensor) {
                InternalSensors.getInstance().disableInternalSensor(this);
            } else {
                // TODO: disable external sensors
            }
        }
    }

    /**
     * adds tje given sensorData to rawData.
     * If rawData > savePeriod the data will be saved to the Storage
     * and rawData will be cleared.
     */
    public synchronized void addRawData (SensorData sensorData) {
        if (!isEnabled) {
            return;
        }
        // TODO needed?
//        if (isInternalSensor
//                && rawData.size() > 0
//                && sensorData.getDate().getTime() < (rawData
//                .get(rawData.size() - 1).getDate()
//                .getTime() + 1000 / (sampleRate - 1))) {
//            return;
//        }

        rawData.add(sensorData);

        if (rawData.size() > savePeriod) {
            SensorDataSerializer serializer = new SensorDataSerializer(sensorID, rawData);
            new Thread(serializer).start();
            rawData.clear();
        }
    }

    /**
     * returns the actual rawData of the Sensor,
     * which is not saved to the storage yet.
     */
    public synchronized Vector<SensorData> getRawData() {
        return rawData;
    }

    /**
     * returns the rawData from the given timestamp to the millisecond exact.
     * Or, if plusMinusOneSecond = true the rawData from the given timestamp plus minus 1 second.
     */
    public synchronized Vector<SensorData> getRawData(Date time, boolean plusMinusOneSecond) {
        if (!plusMinusOneSecond) {
            return getRawData(time, time);
        } else {
            return getRawData(new Date(time.getTime() - 1000), new Date(time.getTime() + 1000));
        }
    }

    /**
     * returns the rawData between the two given timestamps including the given times themselves,
     * either from the database or from the actual rawData or both.
     * Depending of the given timestamps.
     */
    public synchronized Vector<SensorData> getRawData(Date begin, Date end) {
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
            SensorDataDeSerializer deSerializer =
                    new SensorDataDeSerializer(sensorID, data,
                            Utils.dateToUnix(begin), Utils.dateToUnix(end), 0);
            deSerializer.work();
            if (!end.before(firstLocallyHoldDate)) {
                getRawData(begin, end, data);
            }
        }
        return data;
    }

    /**
     * only an internal helper method to prevent multiple code.
     */
    private synchronized void getRawData (Date begin, Date end, Vector<SensorData> data) {
        for (SensorData sensorData : rawData) {
            if ((sensorData.getDate().before(end) || sensorData.getDate()
                    .equals(end))
                    && (sensorData.getDate().after(begin) || sensorData
                    .getDate().equals(begin))) {
                data.add(sensorData);
            }
        }
    }

    /**
     * sets the rawDataMeasurementSystem [e.g. METRICAL] and the
     * rawDataMeasurementUnit [e.g. KILO] of the Sensor
     *
     * The rawDataMeasurementUnit has to fit to the rawDataMeasurementSystem! so
     * KILO and METRICAL is legal! and KILO and ANGLOSAXON is illegal!
     *
     * @param rawDataMeasurementSystem
     *            a MeasurementSystems element
     * @param rawDataMeasurementUnit
     *            a MeasurementUnits element
     */
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

    /**
     * sets the displayedMeasurementSystem [e.g. METRICAL] and the
     * displayedMeasurementUnit [e.g. KILO] of the Sensor
     *
     * The displayedMeasurementUnit has to fit to the displayedMeasurementSystem! so
     * KILO and METRICAL is legal! and KILO and ANGLOSAXON is illegal!
     *
     * @param displayedMeasurementSystem
     *            a MeasurementSystems element
     * @param displayedMeasurementUnit
     *            a MeasurementUnits element
     */
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

    /**
     * Create a parcelable Sensor object from the given Sensor
     *
     * @return the parcelable object
     */
    public PSensor toParcelable() {
        return new PSensor(this.sensorID, this.displayedSensorName, this.bluetoothID, this.sampleRate,
                this.savePeriod, this.smoothness, this.sensorType, this.graphType,
                this.rawDataMeasurementUnit, this.rawDataMeasurementSystem,
                this.displayedMeasurementUnit, this.displayedMeasurementSystem);
    }

      /////////////////////////////////////////////////////////
     ////////////////// GETTERS AND SETTERS //////////////////
    /////////////////////////////////////////////////////////
    public int getSensorID() {
        return sensorID;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public String getDisplayedSensorName() {
        return displayedSensorName;
    }

    public void setDisplayedSensorName(String displayedSensorName) {
        this.displayedSensorName = displayedSensorName;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getBluetoothID() {
        return bluetoothID;
    }

    public void setBluetoothID(String bluetoothID) {
        this.bluetoothID = bluetoothID;
    }

    public float getSmoothness() {
        return smoothness;
    }

    public void setSmoothness(float smoothness) {
        this.smoothness = smoothness;
    }

    public int getSavePeriod() {
        return savePeriod;
    }

    public void setSavePeriod(int savePeriod) {
        this.savePeriod = savePeriod;
    }

    public GraphType getGraphType() {
        return graphType;
    }

    public void setGraphType(GraphType graphType) {
        this.graphType = graphType;
    }


}
