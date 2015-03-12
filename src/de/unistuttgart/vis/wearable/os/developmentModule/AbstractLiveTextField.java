package de.unistuttgart.vis.wearable.os.developmentModule;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;

import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.ValueChangedCallback;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;

/**
 * @author pfaehlfd
 */
public abstract class AbstractLiveTextField {
    PSensor sensor;
    IGarmentCallback igcb;
    private long lastUpdate = 0;
    Context context;


    public AbstractLiveTextField(Context context, PSensor pSensor) {
        this.sensor = pSensor;
        this.context = context;
        registerCallback();
    }

    private void registerCallback() {
        getText(!sensor.isEnabled(), null);

        // register callback
        igcb = new IGarmentCallback.Stub() {
            @Override
            public void callback(BaseCallbackObject value) throws RemoteException {
                if (value instanceof ValueChangedCallback) {
                    final SensorData data = ((ValueChangedCallback) value).toSensorData();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getText(false, data);
                        }
                    });
                }
            }
        };
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }

    private void getText(boolean loadFromStorage, SensorData data) {
//        if (lastUpdate != 0 && lastUpdate + (1000 / graphsPerSecond) >= new Date().getTime()) {
//            return;
//        }
//        lastUpdate = new Date().getTime();

        try {
            acceptText(data.getData()[0] + "");
        } catch (Exception e) {
            unRegisterCallback();
        }

    }

    public abstract void acceptText(String text);

    public void unRegisterCallback() {
        APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }

    public void reregisterCallback() {
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }
}
