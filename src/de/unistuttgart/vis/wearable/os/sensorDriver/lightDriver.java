package de.unistuttgart.vis.wearable.os.sensorDriver;

import android.util.Log;

/**
 * Created by Manuel on 05.03.2015.
 */
public class lightDriver implements SensorDriver {
    @Override
    public void SensorDriver() {

    }

    @Override
    public float[] encodeData(byte[] message) {
        String save = new String(message, 0, message.length);
        float[] dataFloat = new float[1];
        dataFloat[0] = Float.parseFloat(save);
        Log.i("lightDriver_encoding", "float : " + dataFloat[0]);
        return dataFloat;
    }
}
