package de.unistuttgart.vis.wearable.os.developmentModule;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.ValueChangedCallback;
import de.unistuttgart.vis.wearable.os.graph.GraphRenderer;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;

public class GraphModuleSensors extends PopupModuleSensors {

	private LinearLayout chart;

	private PSensor sensor;
	private IGarmentCallback igcb;
	private final int NUMBER_OF_VALUES = 300;

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
		fillChartImage();

		// register callback
		igcb = new IGarmentCallback.Stub() {
			@Override
			public void callback(BaseCallbackObject value)
					throws RemoteException {
				if (value instanceof ValueChangedCallback) {
					((Activity) getContext()).runOnUiThread(new Runnable() {
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

		GraphRenderer.ChartThreadTuple tuple = GraphRenderer.createGraph(
				sensor, getContext(), NUMBER_OF_VALUES, false);
		chart.removeAllViews();
		
		View graph = tuple.getChart();
		
		chart.addView(graph);
		
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) graph.getLayoutParams();
		layoutParams.width = 250;
		layoutParams.height = 130;

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