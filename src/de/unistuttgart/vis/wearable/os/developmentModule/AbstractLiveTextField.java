package de.unistuttgart.vis.wearable.os.developmentModule;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;

import java.text.DecimalFormat;
import java.util.List;

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
    private PSensor sensor;
    private IGarmentCallback igcb;
    private Context context;

    /**
     * Registers a value changed callback for the given PSensor.
     * If the value of the Sensor changes the acceptText method will be called.
     * @param pSensor   the sensor to listen at.
     * @param context   the android context.
     */
    public AbstractLiveTextField(Context context, PSensor pSensor) {
        this.sensor = pSensor;
        this.context = context;
        registerCallback();
    }

    /**
     * Registers a value changed callback for the in the constructor given PSensor,
     * so the SensorData gets updated if the sensor value changes.
     * When the SensorData changes, the acceptText Method will be called.
     */
    private void registerCallback() {
        List<SensorData> _data = sensor.getRawData(1, true);
        if (_data.size() > 0) {
            getText(_data.get(_data.size() - 1));
        }

        // register callback
        igcb = new IGarmentCallback.Stub() {
            @Override
            public void callback(BaseCallbackObject value) throws RemoteException {
                if (value instanceof ValueChangedCallback) {
                    //final SensorData data = ((ValueChangedCallback) value).toSensorData();
                 
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //getText(data);
                        
                        	//TODO workaround
                        	List<SensorData> _data = sensor.getRawData(1, true);
                            if (_data.size() > 0) {
                                getText(_data.get(_data.size() - 1));
                            }
                        }
                    });
                }
            }
        };
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }

    /**
     * Creates the Displaytext of the given SensorData and calls the acceptText Method.
     * @param data the SensorData to create the text of
     */
    private void getText(SensorData data) {
//        if (lastUpdate != 0 && lastUpdate + (1000 / graphsPerSecond) >= new Date().getTime()) {
//            return;
//        }
//        lastUpdate = new Date().getTime();
        try {
            String text = "";
            for (float value : data.getData()) {
                if (text != "") {
                    text += " - ";
                }
                DecimalFormat format = new DecimalFormat("#0.00");
                text += format.format(value);
            }
            acceptText(text);
        } catch (Exception e) {
            unRegisterCallback();
        }

    }

    /**
     * Has to be implemented by the subclass.
     * This method gets called when a new SensorData is available.
     * @param text the new graph
     */
    public abstract void acceptText(String text);

    /**
     * Unregisters the value changed callback,
     * so the SensorData wont get updated anymore.
     */
    public void unRegisterCallback() {
        APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }

    /**
     * Reregisters a value changed callback for the in the constructor given PSensor,
     * so the SensorData gets updated if the sensor value changes.
     */
    public void reregisterCallback() {
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }
}
