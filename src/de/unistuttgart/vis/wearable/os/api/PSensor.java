/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementUnits;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Constants;

/**
 * Sensor object that can be used on the client side
 *
 * @author roehrdor
 */
public class PSensor extends de.unistuttgart.vis.wearable.os.parcel.PSensor implements android.os.Parcelable {
    //<nsdk>
    /**
     * Create a new Sensor object with the given name and the given ID
     *
     * @param ID
     *            the given ID
     * @param displayedSensorName
     *            the displayed SensorName
     */
    public PSensor(int ID, String displayedSensorName) {
        super(ID, displayedSensorName);
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
    public PSensor(int ID, String displayedSensorName, String bluetoothID,
                    int sampleRate, int savePeriod, float smoothness,
                    SensorType sensorType, GraphType graphType,
                    MeasurementUnits rawDataMeasurementUnits,
                    MeasurementSystems rawDataMeasurementSystems,
                    MeasurementUnits displayedMeasurementUnits,
                    MeasurementSystems displayedMeasurementSystems, boolean isInternalSensor) {
        super(ID, displayedSensorName, bluetoothID, sampleRate, savePeriod, smoothness,
                sensorType, graphType, rawDataMeasurementUnits, rawDataMeasurementSystems,
                displayedMeasurementUnits, displayedMeasurementSystems, isInternalSensor);
    }
    //</nsdk>



    // =====================================================================
    //
    // Parcel procedures
    //
    // =====================================================================
    //
    // Creator Object that is used to transmit objects
    //
    public static final android.os.Parcelable.Creator<PSensor> CREATOR = new android.os.Parcelable.Creator<PSensor>() {
        @Override
        public PSensor createFromParcel(android.os.Parcel source) {
            PSensor ret = new PSensor(source.readInt(), source.readString());
            byte b = source.readByte();
            int o = 0;;
            ret.isInternalSensor = (b & 0x1) != 0x0;
            ret.isEnabled = (b & 0x2) != 0x0;
            ret.sampleRate = source.readInt();
            ret.savePeriod = source.readInt();
            ret.smoothness = source.readInt();
            ret.sensorType = (o = source.readInt()) != Constants.ENUMERATION_NULL ? SensorType.values()[o] : null;
            ret.graphType = (o = source.readInt()) != Constants.ENUMERATION_NULL ? GraphType.values()[o] : null;
            ret.rawDataMeasurementUnit = (o = source.readInt()) != Constants.ENUMERATION_NULL ? MeasurementUnits.values()[o] : null;
            ret.rawDataMeasurementSystem = (o = source.readInt()) != Constants.ENUMERATION_NULL ? MeasurementSystems.values()[o] : null;
            ret.displayedMeasurementUnit = (o = source.readInt()) != Constants.ENUMERATION_NULL ? MeasurementUnits.values()[o] : null;
            ret.displayedMeasurementSystem = (o = source.readInt()) != Constants.ENUMERATION_NULL ? MeasurementSystems.values()[o] : null;
            ret.bluetoothID = source.readString();
            return ret;
        }

        @Override
        public PSensor[] newArray(int size) {
            return new PSensor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.displayedSensorName);
        byte compressedInternalEnabled = 0x0;
        compressedInternalEnabled |= this.isInternalSensor ? (byte)0x1 : (byte)0x0;
        compressedInternalEnabled |= this.isEnabled ? (byte)0x2 : (byte)0x0;
        dest.writeByte(compressedInternalEnabled);
        dest.writeInt(this.sampleRate);
        dest.writeInt(this.savePeriod);
        dest.writeFloat(this.smoothness);
        dest.writeInt(this.sensorType == null ? Constants.ENUMERATION_NULL : this.sensorType.ordinal());
        dest.writeInt(this.graphType == null ? Constants.ENUMERATION_NULL : this.graphType.ordinal());
        dest.writeInt(this.rawDataMeasurementUnit == null ? Constants.ENUMERATION_NULL : this.rawDataMeasurementUnit.ordinal());
        dest.writeInt(this.rawDataMeasurementSystem == null ? Constants.ENUMERATION_NULL : this.rawDataMeasurementSystem.ordinal());
        dest.writeInt(this.displayedMeasurementUnit == null ? Constants.ENUMERATION_NULL : this.displayedMeasurementUnit.ordinal());
        dest.writeInt(this.displayedMeasurementSystem == null ? Constants.ENUMERATION_NULL : this.displayedMeasurementSystem.ordinal());
        dest.writeString(this.bluetoothID);
    }
}
