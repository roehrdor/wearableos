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
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Constants;

/**
 * Sensor object that can be used on the client side
 * 
 * @author roehrdor
 */
public class PSensor implements android.os.Parcelable {
	//
	// The ID, name and bluetooth id of the sensor
	//
	private final int ID;
	private String displayedSensorName = null;
	private String bluetoothID = "";
	
	//
	// Settings for the Sensor
	//		
	private boolean isInternalSensor = false;
	private boolean isEnabled = true;
    private int sampleRate = 0;
    private int savePeriod = 0;
    private float smoothness = 0.0f;
    private SensorType sensorType = null;    
    private GraphType graphType = null;
    private MeasurementUnits rawDataMeasurementUnit = null;
    private MeasurementSystems rawDataMeasurementSystem = null;
    private MeasurementUnits displayedMeasurementUnit = null;
    private MeasurementSystems displayedMeasurementSystem = null;
	
	//
	// The gained sensor data
	//
	private java.util.List<SensorData> rawData = new java.util.Vector<SensorData>();
		
	
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
	public PSensor(int ID, String displayedSensorName) {
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
	public PSensor(int ID, String displayedSensorName, String bluetoothID,
			int sampleRate, int savePeriod, float smoothness,
			SensorType sensorType, GraphType graphType,
			MeasurementUnits rawDataMeasurementUnits,
			MeasurementSystems rawDataMeasurementSystems,
			MeasurementUnits displayedMeasurementUnits,
			MeasurementSystems displayedMeasurementSystems) {
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
	
	
	//TODO Sensor Data Functions
	
	
		
	
	// =====================================================================
	//
	// Getter functions	
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
	 * @return the rawDataMeasurementUnit
	 */
	public MeasurementUnits getRawDataMeasurementUnit() {
		int ordinal = APIFunctions.SENSORS_SENSOR_getRawDataMeasurementUnit(this.ID);
		if(ordinal == Constants.ENUMERATION_NULL)
			this.rawDataMeasurementUnit = null;
		else
			this.rawDataMeasurementUnit = MeasurementUnits.values()[ordinal];
		return rawDataMeasurementUnit;
	}

	/**
	 * @return the rawDataMeasurementSystem
	 */
	public MeasurementSystems getRawDataMeasurementSystem() {
		int ordinal = APIFunctions.SENSORS_SENSOR_getRawDataMeasurementSystem(this.ID);
		if(ordinal == Constants.ENUMERATION_NULL)
			this.rawDataMeasurementSystem = null;
		else
			this.rawDataMeasurementSystem = MeasurementSystems.values()[ordinal];
		return rawDataMeasurementSystem;
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
