package de.unistuttgart.vis.wearable.os.developmentModule;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.Date;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.ValueChangedCallback;
import de.unistuttgart.vis.wearable.os.graph.GraphRenderer;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;

public class GraphModuleSensors extends PopupModuleSensors {

	private LinearLayout chart;

	private PSensor sensor;
	private IGarmentCallback igcb;
	private final int NUMBER_OF_VALUES = 300;
    GraphRenderer graphRenderer = new GraphRenderer();

	public GraphModuleSensors(Context context, AttributeSet attrs) {
		super(context, attrs);
		chart = new LinearLayout(context);
		
		
		if (APIHandle.isServiceBound()) {
			PSensor[] sensors = APIFunctions.getAllSensors();
			if (sensors.length > 0) {
				sensor = sensors[0];
			}
		}

		super.createLayout(context, chart, R.drawable.graph, "Graphs");
	}

	@Override
	protected void onAttachedToWindow() {
		if (sensor != null)
			fillChart();
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		if (igcb != null)
			APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
		super.onDetachedFromWindow();
	}

    private void fillChart() {
        fillChartImage(true);

        // register callback
        igcb = new IGarmentCallback.Stub() {
            @Override
            public void callback(BaseCallbackObject value) throws RemoteException {
                if (value instanceof ValueChangedCallback) {
                    final SensorData data = ((ValueChangedCallback) value).toSensorData();

                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillChartImage(false);
                        }
                    });
                }
            }
        };
        APIFunctions.registerCallback(igcb, CallbackFlags.VALUE_CHANGED);
    }

    private long lastUpdate = 0;
    private void fillChartImage(boolean loadFromStorage) {
        if (lastUpdate + 100 >= new Date().getTime()) {
            return;
        }
        lastUpdate = new Date().getTime();

        GraphRenderer.ChartThreadTuple tuple =
                graphRenderer.createGraph(sensor, getContext(), NUMBER_OF_VALUES, loadFromStorage);
        chart.removeAllViews();
        chart.addView(tuple.getChart());
    }

	@Override
	protected void onSensorChanged(PSensor selecedSensor) {
		if (igcb != null)
			APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);

		sensor = selecedSensor;

		fillChart();

		super.onSensorChanged(selecedSensor);
	}

	
	@Override
	protected void OnPauseButton(State state) {
		super.OnPauseButton(state);
		
		if(state == State.PAUSE && igcb != null)
			APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
		else if(state == State.PLAYING && sensor != null) {
			fillChart();
		}
		
		
	}
}