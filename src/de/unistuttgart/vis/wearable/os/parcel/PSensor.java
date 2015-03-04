/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.parcel;

import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.PSensorData;
import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementUnits;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * @author roehrdor
 */
public class PSensor {
    //
    // The ID, name and bluetooth id of the sensor
    //
    protected final int ID;
    protected String displayedSensorName = null;
    protected String bluetoothID = "";

    //
    // Settings for the Sensor
    //
    protected boolean isInternalSensor = false;
    protected boolean isEnabled = true;
    protected int sampleRate = 0;
    protected int savePeriod = 0;
    protected float smoothness = 0.0f;
    protected SensorType sensorType = null;
    protected GraphType graphType = null;
    protected MeasurementUnits rawDataMeasurementUnit = null;
    protected MeasurementSystems rawDataMeasurementSystem = null;
    protected MeasurementUnits displayedMeasurementUnit = null;
    protected MeasurementSystems displayedMeasurementSystem = null;

    //
    // The gained sensor data
    //
    protected java.util.Vector<SensorData> rawData = new java.util.Vector<SensorData>();



    // =============================================================
    //
    // Constructors
    //
    // =============================================================
    /**
     * Private constructor, this one shall never be used
     *
     * @throws IllegalAccessError
     *             if this constructor is called
     */
    @SuppressWarnings({"unused"})
    private PSensor() {
        throw new IllegalAccessError("This constructor shall never be used");
    }

    //<nsdk>
    /**
     * Create a new Sensor object with the given name and the given ID
     *
     * @param ID
     *            the given ID
     * @param displayedSensorName
     *            the displayed SensorName
     */
    protected PSensor(int ID, String displayedSensorName) {
        this.ID = ID;
        this.displayedSensorName = displayedSensorName;
    }

    /**
     * Create a new Sensor object with the given parameters
     *
     * @param ID
     *            sensor id
     * @param displayedSensorName
     *            the displayed sensor name
     * @param bluetoothID
     *            the bluetooth id
     * @param sampleRate
     *            the sample rate
     * @param savePeriod
     *            the save period
     * @param smoothness
     *            the smoothness
     * @param sensorType
     *            the sensor type
     * @param graphType
     *            the graph type
     * @param rawDataMeasurementUnits
     *            the raw data measurement unit
     * @param rawDataMeasurementSystems
     *            the raw data measurement system
     * @param displayedMeasurementUnits
     *            the displayed data measurement unit
     * @param displayedMeasurementSystems
     *            the displayed measurement system
     */
    protected PSensor(int ID, String displayedSensorName, String bluetoothID,
                   int sampleRate, int savePeriod, float smoothness,
                   SensorType sensorType, GraphType graphType,
                   MeasurementUnits rawDataMeasurementUnits,
                   MeasurementSystems rawDataMeasurementSystems,
                   MeasurementUnits displayedMeasurementUnits,
                   MeasurementSystems displayedMeasurementSystems, boolean isInternalSensor) {
        this.ID = ID;
        this.displayedSensorName = displayedSensorName;
        this.bluetoothID = bluetoothID;
        this.sampleRate = sampleRate;
        this.savePeriod = savePeriod;
        this.smoothness = smoothness;
        this.sensorType = sensorType;
        this.graphType = graphType;
        this.rawDataMeasurementUnit = rawDataMeasurementUnits;
        this.rawDataMeasurementSystem = rawDataMeasurementSystems;
        this.displayedMeasurementUnit = displayedMeasurementUnits;
        this.displayedMeasurementSystem = displayedMeasurementSystems;
        this.isInternalSensor = isInternalSensor;
    }
    //</nsdk>



    // =============================================================
    //
    // Functions
    //
    // =============================================================
    /**
     * Get the unique Sensor ID
     *
     * @return the unique Sensor ID
     */
    public int getID() {
        return this.ID;
    }

    /**
     * Check whether the given Sensor is an internal sensor
     *
     * @return true if the sensor is an internal sensor
     */
    public boolean isInternalSensor() {
        return this.isInternalSensor;
    }

    /**
     * Get the bluetooth ID tag from the sensor
     *
     * @return the bluetooth id
     */
    public String getBluetoothID() {
        return this.bluetoothID;
    }

    /**
     * Get the last requested raw data again. This function works without refreshing anything and
     * shall be used it the user wants to access the same data twice
     *
     * @return the cached list of sensor data
     */
    public java.util.Vector<SensorData> getLastRawData() {
        return this.rawData;
    }

    /**
     * Get the current available raw data from this Sensor
     *
     * @return the current available rawData
     */
    public java.util.Vector<SensorData> getRawData() {
        PSensorData pd = APIFunctions.SENSORS_SENSOR_getRawData(this.ID);
        if(pd != null) {
            this.rawData = pd.toSensorDataList();
            return this.rawData;
        } else
            return null;
    }

    /**
     * Get raw data fro the given time stamp. If data one second before and after the given time
     * shall be included as well the plusMinusOneSecond flag shall be set. Note to easily convert
     * between a unix time stamp and the {@link java.util.Date} object one can use the provided
     * {@link de.unistuttgart.vis.wearable.os.utils.Utils#unixToDate(int)} function.
     *
     * @param time               the time to search raw data for
     * @param plusMinusOneSecond in case this flag is set the data recorded one second earlier and
     *                           later will be included as well
     * @return a vector containing the data sets that meet the requirements
     */
    public java.util.Vector<SensorData> getRawData(java.util.Date time, boolean plusMinusOneSecond) {
        PSensorData pd = APIFunctions.SENSORS_SENSOR_getRawDataIB(this.ID, Utils.dateToLongUnix(time), plusMinusOneSecond);
        if(pd != null) {
            this.rawData = pd.toSensorDataList();
            return this.rawData;
        } else
            return null;
    }

    /**
     * Get all data that has been recorded from the begin time stamp on but before the end time stamp
     *
     * @param begin the begin time stamp
     * @param end   the end time stamp
     * @return a vector containing the data sets that meet the requirements
     */
    public java.util.Vector<SensorData> getRawData(java.util.Date begin, java.util.Date end) {
        PSensorData pd = APIFunctions.SENSORS_SENSOR_getRawDataII(this.ID, Utils.dateToLongUnix(begin), Utils.dateToLongUnix(end));
        if(pd != null) {
            this.rawData = pd.toSensorDataList();
            return this.rawData;
        } else
            return null;
    }

    /**
     * Get the last numberOfValues Sensor data
     *
     * @param numberOfValues the number of values we want to get
     * @return the values
     */
    public java.util.Vector<SensorData> getRawData(int numberOfValues) {
        PSensorData pd = APIFunctions.SENSORS_SENSOR_getRawDataN(this.ID, numberOfValues);
        if(pd != null) {
            this.rawData = pd.toSensorDataList();
            return this.rawData;
        } else
            return null;
    }


    // =====================================================================
    //
    // Getter and Setter functions
    //
    // =====================================================================
    /**
     * @return the isEnabled
     */
    public boolean isEnabled() {
        this.isEnabled = APIFunctions.SENSORS_SENSOR_isEnabled(this.ID);
        return this.isEnabled;
    }

    /**
     * @return the displayedSensorName
     */
    public String getDisplayedSensorName() {
        this.displayedSensorName = APIFunctions.SENSORS_SENSOR_getDisplayedSensorName(this.ID);
        return this.displayedSensorName;
    }

    /**
     * @return the sampleRate
     */
    public int getSampleRate() {
        this.sampleRate = APIFunctions.SENSORS_SENSOR_getSampleRate(this.ID);
        return this.sampleRate;
    }

    /**
     * @return the savePeriod
     */
    public int getSavePeriod() {
        this.savePeriod = APIFunctions.SENSORS_SENSOR_getSavePeriod(this.ID);
        return savePeriod;
    }

    /**
     * @return the smoothness
     */
    public float getSmoothness() {
        this.smoothness = APIFunctions.SENSORS_SENSOR_getSmoothness(this.ID);
        return smoothness;
    }

    /**
     * @return the sensorType
     */
    public SensorType getSensorType() {
        int ordinal = APIFunctions.SENSORS_SENSOR_getSensorType(this.ID);
        if(ordinal == Constants.ENUMERATION_NULL)
            this.sensorType = null;
        else
            this.sensorType = SensorType.values()[ordinal];
        return sensorType;
    }

    /**
     * @return the graphType
     */
    public GraphType getGraphType() {
        int ordinal = APIFunctions.SENSORS_SENSOR_getGraphType(this.ID);
        if(ordinal == Constants.ENUMERATION_NULL)
            this.graphType = null;
        else
            this.graphType = GraphType.values()[ordinal];
        return graphType;
    }

    /**
     * @return the displayedMeasurementUnit
     */
    public MeasurementUnits getDisplayedMeasurementUnit() {
        int ordinal = APIFunctions.SENSORS_SENSOR_getDisplayedMeasurementUnit(this.ID);
        if(ordinal == Constants.ENUMERATION_NULL)
            this.displayedMeasurementUnit = null;
        else
            this.displayedMeasurementUnit = MeasurementUnits.values()[ordinal];
        return displayedMeasurementUnit;
    }

    /**
     * @return the displayedMeasurementSystem
     */
    public MeasurementSystems getDisplayedMeasurementSystem() {
        int ordinal = APIFunctions.SENSORS_SENSOR_getDisplayedMeasurementSystem(this.ID);
        if(ordinal == Constants.ENUMERATION_NULL)
            this.displayedMeasurementSystem = null;
        else
            this.displayedMeasurementSystem = MeasurementSystems.values()[ordinal];
        return displayedMeasurementSystem;
    }

    @Override
    public String toString() {
        return this.displayedSensorName;
    }
}
