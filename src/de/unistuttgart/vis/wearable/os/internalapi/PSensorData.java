package de.unistuttgart.vis.wearable.os.internalapi;

import de.unistuttgart.vis.wearable.os.sensors.SensorData;

/**
 * Parcelable Sensor Data object
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
        if(sensorData == null || sensorData.size() < 1)
            return;

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
     *
     * @return A list containing sensor data objects
     */
    public java.util.Vector<SensorData> toSensorDataList() {
        java.util.Vector<SensorData> ret;
        if(this.time == null)
            return null;
        ret = new java.util.Vector<SensorData>();
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
    public static final android.os.Parcelable.Creator<PSensorData> CREATOR = new android.os.Parcelable.Creator<PSensorData>() {
        @Override
        public PSensorData createFromParcel(android.os.Parcel source) {
            PSensorData ret = new PSensorData();
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
        if(this.time == null || this.time.length == 0)
            dest.writeInt(0);
        else {
            dest.writeInt(this.time.length);
            dest.writeInt(this.dimension);
            dest.writeIntArray(this.time);
            dest.writeFloatArray(this.data);
        }
    }

}
