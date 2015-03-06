package de.unistuttgart.vis.wearable.os.sensorDriver;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Manuel on 05.03.2015.
 */
public class stepDriver implements SensorDriver {

    @Override
    public void SensorDriver() {

    }

    @Override
    public float[] encodeData(byte[] message) {
        float[] dataFloat = new float[6];
                byte[] sendBuffer = new byte[6];
                sendBuffer = Arrays.copyOfRange(message, 1, 6);
                String readMessage = new String(sendBuffer, 0, sendBuffer.length);
                Log.i("stepDriver_encoding", "Encode stream: " + readMessage + " Streamlength: " + sendBuffer.length);
                for (int i = 0; i < sendBuffer.length; i++) {
                    String save = new String(sendBuffer, i, 1);
                    dataFloat[i] = Float.parseFloat(save);
                    Log.i("stepDriver_encoding", "float" + i + ": " + save);
                }

        return dataFloat;
    }
}
