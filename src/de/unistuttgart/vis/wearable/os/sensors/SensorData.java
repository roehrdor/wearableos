package de.unistuttgart.vis.wearable.os.sensors;

/**
 * SensorData holds the value of one sensor measurement and the timestamp when
 * the measurement was taken.
 *
 * @author pfaehlfd
 */
public class SensorData {
	private float[] data;
	private int date;

	/**
	 * Create a new SensorData Object
	 * 
	 * @param data
	 *            a data array. The size of this array determines the dimension
	 *            of the data set created by the sensor
	 * @param date
	 *            the unix time stamp the data set has been created
	 */
	public SensorData(float[] data, int date) {
		this.data = data;
		this.date = date;
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
	public SensorData(int date, int size, float... data) {
		this.date = date;
		this.data = new float[size];
		int pos = -1;
		for(float d : data) 
			this.data[++pos] = d;
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
		return new java.util.Date(1000L * (long) this.date);
	}

	/**
	 * Get the date as unix time stamp
	 * 
	 * @return the unix time stamp this data set has been taken on
	 */
	public int getUnixDate() {
		return this.date;
	}
}
