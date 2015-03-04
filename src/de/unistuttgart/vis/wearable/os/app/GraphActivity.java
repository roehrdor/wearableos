package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.LinearLayout;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.ValueChangedCallback;
import de.unistuttgart.vis.wearable.os.graph.GraphRenderer;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;

public class GraphActivity extends Activity {

	private LinearLayout chart;
    PSensor sensor;
    IGarmentCallback igcb;
    private final int NUMBER_OF_VALUES = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		chart = (LinearLayout) findViewById(R.id.graphLayout);

        sensor = APIFunctions.getSensorById(getIntent().getExtras()
                .getInt("sensorId"));
		fillChart();
	}

	private void fillChart() {
        fillChartImage();

        // register callback
        igcb = new IGarmentCallback.Stub() {
            @Override
            public void callback(BaseCallbackObject value) throws RemoteException {
                if (value instanceof ValueChangedCallback) {
                    final SensorData data = ((ValueChangedCallback) value).toSensorData();

                    GraphActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillChartImage();
                        }
                    });
                }
            }
        };
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
	}

    private long lastUpdate = 0;
    private void fillChartImage() {
        if (lastUpdate + 100 >= new Date().getTime()) {
            return;
        }
        lastUpdate = new Date().getTime();

        GraphRenderer.ChartThreadTuple tuple =
                GraphRenderer.createGraph(sensor, this, NUMBER_OF_VALUES);
        chart.removeAllViews();
        chart.addView(tuple.getChart());
    }

    @Override
    protected void onPause() {
        super.onPause();
        APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }
}
