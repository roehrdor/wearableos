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
        String stringOfByte = message.toString();
        String[] parts = stringOfByte.split(",");
        //float[6] for a senor with 6 values from 1 to 254
        float[] dataFloat = new float[6];
        for (int j = 0; j < parts.length; j++) {
            if (parts[j].equals("255")) {
                for (int i = 0; i < 6; i++) {
                    dataFloat[i] = Float.parseFloat(parts[j + 1]);
                }
            }
        }

        return dataFloat;
    }
}
