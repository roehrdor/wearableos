package de.unistuttgart.vis.wearable.os.api;

import android.os.Parcel;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;

/**
 * Callback object that is returned when a sensor has gotten new values
 *
 * @author roehrdor
 */
public class ValueChangedCallback extends BaseCallbackObject {
    public int date;
    public float[] data;

    /**
     * Create a new ValueChangedCallback object with the given date and data
     *
     * @param date the date of the sensor data
     * @param data the data of the sensor data
     */
    public ValueChangedCallback(int date, float[] data) {
        this.date = date;
        this.data = data;
    }

    /**
     * Create a new Value changed object from the parcel
     *
     * @param in the parcel to read the object from
     */
    public ValueChangedCallback(Parcel in) {
        this.date = in.readInt();
        this.data = in.createFloatArray();
    }

    /**
     * Create a sensor data object from the given value changed callback object
     *
     * @return a SensorData object with the data and date
     */
    public SensorData toSensorData() {
        return new SensorData(this.data, this.date);
    }

    public static final Creator<ValueChangedCallback> CREATOR = new Creator<ValueChangedCallback>() {
        @Override
        public ValueChangedCallback createFromParcel(Parcel source) {
            return new ValueChangedCallback(source);
        }

        @Override
        public ValueChangedCallback[] newArray(int size) {
            return new ValueChangedCallback[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Signal the object being send is type of call back object
        dest.writeInt(1);
        dest.writeInt(this.date);
        dest.writeFloatArray(this.data);
    }
}
