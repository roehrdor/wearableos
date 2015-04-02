package de.unistuttgart.vis.wearable.os.graph;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.view.View;

import java.util.Date;

import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.ValueChangedCallback;

/**
 * @author pfaehlfd
 */
public abstract class AbstractLiveGraph {

    PSensor sensor;
    IGarmentCallback igcb;
    private int numberOfValuesToShow = 300;
    GraphRenderer graphRenderer = new GraphRenderer();;
    private long lastUpdate = 0;
    Context context;
    int graphsPerSecond = 10;

    /**
     * Registers a value changed callback for the given PSensor.
     * If the value of the Sensor changes the acceptGraph method will be called.
     * @param pSensor   the sensor to listen at.
     * @param numberOfValuesToShow  the maximum number of values to show in the graph.
     * @param context   the android context.
     * @param graphsPerSecond the maximum number of graphs per second you want.
     */
    public AbstractLiveGraph(PSensor pSensor, int numberOfValuesToShow,
                             Context context, int graphsPerSecond) {
        this.sensor = pSensor;
        this.numberOfValuesToShow = numberOfValuesToShow;
        this.context = context;
        this.graphsPerSecond = graphsPerSecond;
        registerCallback();
    }

    /**
     * Registers a value changed callback for the in the constructor given PSensor,
     * so the graph gets updated if the sensor value changes.
     * When the graph changes, the acceptGraph Method will be called.
     */
    private void registerCallback() {
        getTuple(!sensor.isEnabled());

        // register callback
        igcb = new IGarmentCallback.Stub() {
            @Override
            public void callback(BaseCallbackObject value) throws RemoteException {
                if (value instanceof ValueChangedCallback) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getTuple(false);
                        }
                    });
                }
            }
        };
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }

    /**
     * Calls the graphRenderer to create the Tuple.
     * If the graph wasnt updated for 1000/graphsPerSecond milliseconds
     * the acceptGraph method will be called.
     * @param loadFromStorage if the data shall also be loaded from the storage
     */
    private void getTuple(boolean loadFromStorage) {
        if (lastUpdate != 0 && lastUpdate + (1000 / graphsPerSecond) >= new Date().getTime()) {
            return;
        }
        lastUpdate = new Date().getTime();

        try {
            acceptGraph(
                    graphRenderer.createGraph(sensor, context, numberOfValuesToShow, loadFromStorage));
        } catch (Exception e) {
            unRegisterCallback();
        }

    }

    /**
     * Has to be implemented by the subclass.
     * This method gets called when a new graph is available.
     * @param graph the new graph
     *              e.g. implementation for LinearLayout chart:
     *              chart.removeAllViews();
     *              chart.addView(graph);
     */
    public abstract void acceptGraph(View graph);

    /**
     * Unregisters the value changed callback,
     * so the graph wont get updated anymore.
     */
    public void unRegisterCallback() {
        APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }

    /**
     * Reregisters a value changed callback for the in the constructor given PSensor,
     * so the graph gets updated if the sensor value changes.
     */
    public void reregisterCallback() {
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }
}
