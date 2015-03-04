/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.sensors;

/**
 * SensorData holds the value of one sensor measurement and the timestamp when
 * the measurement was taken.
 *
 * @author pfaehlfd
 */
public class SensorData {
	private float[] data;
	private long date;
    private int dimension;

	/**
	 * Create a new SensorData Object
	 * 
	 * @param data
	 *            a data array. The size of this array determines the dimension
	 *            of the data set created by the sensor
	 * @param date
	 *            the unix time stamp the data set has been created
	 */
	public SensorData(float[] data, long date) {
		this.data = data;
		this.date = date;
        if(data != null)
            this.dimension = this.data.length;
	}

	/**
	 * Create a new SensorData Object
	 * 
	 * @param date
	 *            the unix time stamp the data set has been created
	 * @param data
	 *            the data. The number of data paremeters determines the
	 *            dimension of the data set created by the sensor
	 * 
	 */
	public SensorData(long date, int size, float... data) {
		this.date = date;
		this.data = new float[size];
		int pos = -1;
		for(float d : data) 
			this.data[++pos] = d;
        if(data != null)
            this.dimension = this.data.length;
	}

	/**
	 * Get the data array from the SensorData object
	 * 
	 * @return the data array
	 */
	public float[] getData() {
		return data;
	}

	/**
	 * Get the Date as Date object
	 * 
	 * @return the date object this data set has been taken on
	 */
	public java.util.Date getDate() {
		return new java.util.Date(this.date);
	}

	/**
	 * Get the date as unix time stamp
	 * 
	 * @return the unix time stamp this data set has been taken on
	 */
	public long getLongUnixDate() {
		return this.date;
	}

    /**
     * Get the dimension of the sensor data object
     *
     * @return the dimension
     */
    public int getDimension() {
        return this.dimension;
    }
}
