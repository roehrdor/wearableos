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
public class PSensorData extends de.unistuttgart.vis.wearable.os.parcel.PSensorData implements android.os.Parcelable {
    /**
     * Create a new PSensorData object from the given List of Sensor Data
     *
     * @param sensorData the sensor data list to be converted
     */
    public PSensorData(java.util.List<SensorData> sensorData) {
        super(sensorData);
    }

    /**
     * Private constructor to create a new object from parcel
     */
    protected PSensorData() {
    }

    //
    // Creator Object that is used to transmit objects
    //
    public static final android.os.Parcelable.Creator<PSensorData> CREATOR = new android.os.Parcelable.Creator<PSensorData>() {
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