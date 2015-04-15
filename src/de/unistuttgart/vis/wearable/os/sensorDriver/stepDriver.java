package de.unistuttgart.vis.wearable.os.sensorDriver;

import android.util.Log;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by Manuel on 05.03.2015.
 */
public class stepDriver implements SensorDriver {

    @Override
    public void SensorDriver() {

    }

    @Override
    public float[] encodeData(byte[] message) {
        String test = new String(message);
        Log.i("testByte", message.toString());
        String[] parts = test.split(",");
        //float[6] for a senor with 6 values from 1 to 254
        float[] dataFloat = new float[6];
        for (int j = 0; j < parts.length; j++) {
            Log.i("test", parts[j]);
            dataFloat[j] = Float.parseFloat(parts[j]);

        }
        return dataFloat;
    }
}
