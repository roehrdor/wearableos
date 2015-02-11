package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.sensors.SensorData;

/**
 * Parcelable Sensor Data object. This class represents a list of
 * {@link de.unistuttgart.vis.wearable.os.sensors.SensorData} objects but optimizes them
 * considering their data layout to be sent through the parcel.
 *
 * The typical use of this class will be to create a new
 * {@link de.unistuttgart.vis.wearable.os.api.PSensorData} object from a
 * {@link java.util.List} of {@link de.unistuttgart.vis.wearable.os.sensors.SensorData} objects
 * and after this object has been sent to the client call the
 * {@link de.unistuttgart.vis.wearable.os.api.PSensorData#toSensorDataList()} method to create the initial List of
 * {@link de.unistuttgart.vis.wearable.os.sensors.SensorData} objects.
 *
 * Note that this class should only be used in the
 * {@link de.unistuttgart.vis.wearable.os.internalservice.APIInternalBinder} class as well as in
 * the {@link de.unistuttgart.vis.wearable.os.internalapi.APIFunctions} class.
 *
 * @author roehrdor
 */
public class PSensorData implements android.os.Parcelable {
    private int time[] = null;
    private float data[] = null;
    private int dimension;

    /**
     * Create a new PSensorData object from the given List of Sensor Data
     *
     * @param sensorData the sensor data list to be converted
     */
    public PSensorData(java.util.List<SensorData> sensorData) {
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
    private PSensorData() {
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

    //
    // Creator Object that is used to transmit objects
    //
    public static final Creator<PSensorData> CREATOR = new Creator<PSensorData>() {
        @Override
        public PSensorData createFromParcel(android.os.Parcel source) {
            PSensorData ret = new PSensorData();
            // Read the number of data sets, in case this is set to 0 there is no data to be read
            int size = source.readInt();
            if(size > 0) {
                ret.time = new int[size];
                ret.dimension = source.readInt();
                ret.data = new float[ret.dimension * ret.time.length];
                source.readIntArray(ret.time);
                source.readFloatArray(ret.data);
            }
            return ret;
        }

        @Override
        public PSensorData[] newArray(int size) {
            return new PSensorData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        // If we don't have any data write a 0
        if(this.time == null || this.time.length == 0)
            dest.writeInt(0);
        else {
            // otherwise write the data
            dest.writeInt(this.time.length);
            dest.writeInt(this.dimension);
            dest.writeIntArray(this.time);
            dest.writeFloatArray(this.data);
        }
    }

}
