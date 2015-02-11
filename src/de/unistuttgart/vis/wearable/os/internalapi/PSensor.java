/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.graph.GraphType;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementUnits;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * Sensor object that can be used in the settings app
 * 
 * @author roehrdor
 */
public class PSensor extends de.unistuttgart.vis.wearable.os.parcel.PSensor implements android.os.Parcelable {
	// =============================================================
	//
	// Constructors
	//
	// =============================================================
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
			MeasurementSystems displayedMeasurementSystems) {
	    super(ID, displayedSensorName, bluetoothID, sampleRate, savePeriod, smoothness, sensorType,
                graphType, rawDataMeasurementUnits, rawDataMeasurementSystems,
                displayedMeasurementUnits, displayedMeasurementSystems);
	}
	//</nsdk>


	
	// =====================================================================
	//
	// Getter and Setter functions	
	// 
	// =====================================================================
	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {		
		this.isEnabled = isEnabled;
		APIFunctions.SENSORS_SENSOR_setEnabled(this.ID, isEnabled);
	}

	/**
	 * @param displayedSensorName the displayedSensorName to set
	 */
	public void setDisplayedSensorName(String displayedSensorName) {
		this.displayedSensorName = displayedSensorName;
		APIFunctions.SENSORS_SENSOR_setDisplayedSensorName(this.ID, displayedSensorName);
	}

	/**
	 * @param sampleRate the sampleRate to set
	 */
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
		APIFunctions.SENSORS_SENSOR_setSampleRate(this.ID, sampleRate);
	}

	/**
	 * @param savePeriod the savePeriod to set
	 */
	public void setSavePeriod(int savePeriod) {
		this.savePeriod = savePeriod;
		APIFunctions.SENSORS_SENSOR_setSavePeriod(this.ID, savePeriod);
	}

	/**
	 * @param smoothness the smoothness to set
	 */
	public void setSmoothness(float smoothness) {		
		this.smoothness = smoothness;
		APIFunctions.SENSORS_SENSOR_setSmoothness(this.ID, smoothness);
	}	
	
	/**
	 * @param sensorType the sensorType to set
	 */
	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
		int ordinal = sensorType != null ? sensorType.ordinal() : Constants.ENUMERATION_NULL;
		APIFunctions.SENSORS_SENSOR_setSensorType(this.ID, ordinal);
	}

	/**
	 * @param graphType the graphType to set
	 */
	public void setGraphType(GraphType graphType) {
		this.graphType = graphType;
		int ordinal = graphType != null ? graphType.ordinal() : Constants.ENUMERATION_NULL;
		APIFunctions.SENSORS_SENSOR_setGraphType(this.ID, ordinal);
	}

	/**
	 * @param rawDataMeasurementUnit the rawDataMeasurementUnit to set
	 */
	public void setRawDataMeasurementUnit(MeasurementUnits rawDataMeasurementUnit) {
		this.rawDataMeasurementUnit = rawDataMeasurementUnit;
		int ordinal = rawDataMeasurementUnit != null ? rawDataMeasurementUnit.ordinal() : Constants.ENUMERATION_NULL;
		APIFunctions.SENSORS_SENSOR_setRawDataMeasurementUnit(this.ID, ordinal);
	}

	/**
	 * @param rawDataMeasurementSystem the rawDataMeasurementSystem to set
	 */
	public void setRawDataMeasurementSystem(MeasurementSystems rawDataMeasurementSystem) {
		this.rawDataMeasurementSystem = rawDataMeasurementSystem;
		int ordinal = rawDataMeasurementSystem != null ? rawDataMeasurementSystem.ordinal() : Constants.ENUMERATION_NULL;
		APIFunctions.SENSORS_SENSOR_setRawDataMeasurementSystem(this.ID, ordinal);
	}

	/**
	 * @param displayedMeasurementUnit the displayedMeasurementUnit to set
	 */
	public void setDisplayedMeasurementUnit(MeasurementUnits displayedMeasurementUnit) {
		this.displayedMeasurementUnit = displayedMeasurementUnit;
		int ordinal = displayedMeasurementUnit != null ? displayedMeasurementUnit.ordinal() : Constants.ENUMERATION_NULL;
		APIFunctions.SENSORS_SENSOR_setDisplayedMeasurementUnit(this.ID, ordinal);
	}

	/**
	 * @param displayedMeasurementSystem the displayedMeasurementSystem to set
	 */
	public void setDisplayedMeasurementSystem(MeasurementSystems displayedMeasurementSystem) {
		this.displayedMeasurementSystem = displayedMeasurementSystem;
		int ordinal = displayedMeasurementSystem != null ? displayedMeasurementSystem.ordinal() : Constants.ENUMERATION_NULL;
		APIFunctions.SENSORS_SENSOR_setDisplayedMeasurementSystem(this.ID, ordinal);
	}



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
    }
}

