package de.unistuttgart.vis.wearable.os.parcel;

import de.unistuttgart.vis.wearable.os.sensors.SensorData;

/**
 * Created by Oliver on 2/11/2015.
 */
public class PSensorData {
    protected int time[] = null;
    protected float data[] = null;
    protected int dimension;

    /**
     * Get the dimension of the sensor data object
     *
     * @return the dimension
     */
    public int getDimension() {
        return this.dimension;
    }


    /**
     * Create a new PSensorData object from the given List of Sensor Data
     *
     * @param sensorData the sensor data list to be converted
     */
    protected PSensorData(java.util.List<SensorData> sensorData) {
        // In case we have no data we can skip constructing anything
        if(sensorData == null || sensorData.size() < 1)
            return;

        // Otherwise construct the parcelable object
        this.dimension = sensorData.get(0).getData().length;
        this.time = new int[sensorData.size()];
        this.data = new float[this.dimension * this.time.length];
        int c = 0, d;
        for(SensorData sd : sensorData) {
            this.time[c] = sd.getUnixDate();
            for(d = 0; d != this.dimension; ++d) {
                this.data[c + d] = sd.getData()[d];
            }
            ++c;
        }
    }

    /**
     * Private constructor to create a new object from parcel
     */
    protected PSensorData() {
    }

    /**
     * Convert the PSensorData object back to a list of Sensor Data objects
     * This function will return an empty vector if there is no data to be converted
     *
     * @return A list containing sensor data objects
     */
    public java.util.Vector<SensorData> toSensorDataList() {
        java.util.Vector<SensorData> ret = new java.util.Vector<SensorData>();

        // Check whether we have constructed anything, if not we have no data available
        // In this case return the empty vector
        if(this.time == null)
            return ret;

        // Otherwise fill the vector with the data
        int length = this.time.length;
        for(int i = 0; i != length; ++i) {
            float[] data = new float[this.dimension];
            for(int j = 0; j != dimension; ++j) {
                data[j] = this.data[i + j];
            }
            ret.add(new SensorData(data, time[i]));
        }
        return ret;
    }
}
