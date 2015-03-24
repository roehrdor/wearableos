package de.unistuttgart.vis.wearable.os.sensorDriver;

import android.util.Log;

/**
 * Created by lorenzma on 06.03.15.
 */
public class heartbeatDriver implements SensorDriver{
    @Override
    public void SensorDriver() {

    }

    @Override
    public float[] encodeData(byte[] message) {
        String test = new String(message);
        Log.i("testByte", message.toString());
        String[] parts = test.split(",");
        //float[6] for a senor with 6 values from 1 to 254
        float[] dataFloat = new float[1];
            Log.i("test", parts[0]);
            dataFloat[0] = Float.parseFloat(parts[0]);

        return dataFloat;
    }
}
