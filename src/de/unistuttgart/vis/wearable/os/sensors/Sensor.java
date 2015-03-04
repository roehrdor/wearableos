/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.sensors;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallBackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.ValueChangedCallback;
import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.sensorDriver.SensorDriver;
import de.unistuttgart.vis.wearable.os.service.GarmentOSService;
import de.unistuttgart.vis.wearable.os.storage.SensorDataDeSerializer;
import de.unistuttgart.vis.wearable.os.storage.SensorDataSerializer;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * @author pfaehlfd
 */
public class Sensor implements Externalizable {

    private static final long serialVersionUID = 8052438921578259544L;

    private transient Vector<SensorData> rawData = new Vector<SensorData>();

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
     * Empty constructor needed for serialization
     * This constructor shall never be called otherwise
     */
    public Sensor() {
        if(this.rawData == null)
            this.rawData = new Vector<SensorData>();
    }

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

        rawData.add(sensorData);

        GarmentOSService.callback(CallbackFlags.VALUE_CHANGED, new ValueChangedCallback(sensorData.getUnixDate(), sensorData.getData()));

        if (rawData.size() > savePeriod) {
            SensorDataSerializer serializer = new SensorDataSerializer(sensorID, rawData);
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
     * Returns the given number of the newest SensorData of the Sensor.
     * @param numberOfValues    the number of SensorData to be returned
     * @return  the newest SensorData of the Sensor
     */
    public synchronized Vector<SensorData> getRawData(int numberOfValues) {
        Vector<SensorData> returnData = new Vector<SensorData>();
        if (rawData.size() < numberOfValues) {
            SensorDataDeSerializer deSerializer =
                    new SensorDataDeSerializer(sensorID, returnData, numberOfValues - rawData.size());
            SensorDataDeSerializer.jobFinsihed(deSerializer.work());
            returnData.addAll(rawData);
        } else {
            for (int i = rawData.size() - numberOfValues; i < rawData.size(); i++) {
                returnData.add(rawData.get(i));
            }
        }
        return returnData;
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
     * Create a parcelable Sensor object from the given Sensor for internal use
     *
     * @return the parcelable object
     */
    public PSensor toParcelable() {
        return new PSensor(this.sensorID, this.displayedSensorName, this.bluetoothID, this.sampleRate,
                this.savePeriod, this.smoothness, this.sensorType, this.graphType,
                this.rawDataMeasurementUnit, this.rawDataMeasurementSystem,
                this.displayedMeasurementUnit, this.displayedMeasurementSystem, this.isInternalSensor);
    }

    /**
     * Create a parcelable Sensor object from the given Sensor
     *
     * @return the parcelable object
     */
    public de.unistuttgart.vis.wearable.os.api.PSensor toParcelableAPI() {
        return new de.unistuttgart.vis.wearable.os.api.PSensor(this.sensorID, this.displayedSensorName, this.bluetoothID, this.sampleRate,
                this.savePeriod, this.smoothness, this.sensorType, this.graphType,
                this.rawDataMeasurementUnit, this.rawDataMeasurementSystem,
                this.displayedMeasurementUnit, this.displayedMeasurementSystem, this.isInternalSensor);
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

    public MeasurementUnits getDisplayedMeasurementUnit() {
        return displayedMeasurementUnit;
    }

    public void setDisplayedMeasurementUnit(MeasurementUnits displayedMeasurementUnit) {
        this.displayedMeasurementUnit = displayedMeasurementUnit;
    }

    public MeasurementSystems getDisplayedMeasurementSystem() {
        return displayedMeasurementSystem;
    }

    public void setDisplayedMeasurementSystem(MeasurementSystems displayedMeasurementSystem) {
        this.displayedMeasurementSystem = displayedMeasurementSystem;
    }


    /**
     * Reads the next object from the ObjectInput <code>input</code>.
     *
     * @param input the ObjectInput from which the next object is read.
     * @throws java.io.IOException    if an error occurs attempting to read from {@code input}.
     * @throws ClassNotFoundException if the class of the instance being loaded cannot be found.
     */
    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        this.isInternalSensor = input.readBoolean();
        this.isEnabled = input.readBoolean();
        this.sensorID = input.readInt();
        this.bluetoothID = input.readUTF();
        this.sampleRate = input.readInt();
        this.smoothness = input.readFloat();
        this.savePeriod = input.readInt();
        this.displayedSensorName = input.readUTF();
        this.sensorType = (SensorType)input.readObject();
        this.graphType = (GraphType)input.readObject();
        this.rawDataMeasurementUnit = (MeasurementUnits)input.readObject();
        this.rawDataMeasurementSystem = (MeasurementSystems)input.readObject();
        this.displayedMeasurementUnit = (MeasurementUnits)input.readObject();
        this.displayedMeasurementSystem = (MeasurementSystems)input.readObject();
        this.rawData = new Vector<SensorData>();
    }

    /**
     * Writes the receiver to the ObjectOutput <code>output</code>.
     *
     * @param output the ObjectOutput to write the object to.
     * @throws java.io.IOException if an error occurs attempting to write to {@code output}.
     */
    @Override
    public void writeExternal(ObjectOutput output) throws IOException {
        output.writeBoolean(this.isInternalSensor);
        output.writeBoolean(this.isEnabled);
        output.writeInt(this.sensorID);
        output.writeUTF(this.bluetoothID);
        output.writeInt(this.sampleRate);
        output.writeFloat(this.smoothness);
        output.writeInt(this.savePeriod);
        output.writeUTF(this.displayedSensorName);
        output.writeObject(this.sensorType);
        output.writeObject(this.graphType);
        output.writeObject(this.rawDataMeasurementUnit);
        output.writeObject(this.rawDataMeasurementSystem);
        output.writeObject(this.displayedMeasurementUnit);
        output.writeObject(this.displayedMeasurementSystem);
    }
}
