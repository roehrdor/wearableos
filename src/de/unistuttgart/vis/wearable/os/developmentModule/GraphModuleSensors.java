package de.unistuttgart.vis.wearable.os.developmentModule;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Date;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.api.ValueChangedCallback;
import de.unistuttgart.vis.wearable.os.developmentModule.SelectSensorPopupMenu.SelectedSensorChangedListener;
import de.unistuttgart.vis.wearable.os.graph.GraphRenderer;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;


//TODO use AbstractLiveGraph

/**
 * @author Sophie Ogando
 */
public class GraphModuleSensors extends BasisModule implements SelectedSensorChangedListener {

	private LinearLayout chart;

	private PSensor sensor;
	private IGarmentCallback igcb;
	private final int NUMBER_OF_VALUES = 300;
	private GraphRenderer graphRenderer = new GraphRenderer();

	//for xml or code
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
		
		
		//user can select sensor
		SelectSensorPopupMenu sensorPopup = new SelectSensorPopupMenu();
		sensorPopup.addSelectedSensorChangedListener(this);
		super.setPopupWindow(sensorPopup);
	}
	
	//for code
	public GraphModuleSensors(Context context, AttributeSet attrs, PSensor sensor) {
		super(context, attrs);
		chart = new LinearLayout(context);
		
		this.sensor = sensor;

		super.createLayout(context, chart, R.drawable.graph, "Graphs");
	}
	
	public void setSensorStatic(PSensor sensor) {
		if(getPopupWindow() != null) {
			((SelectSensorPopupMenu)getPopupWindow()).removeSelectedSensorChangedListener(this);
			setPopupWindow(null);
		}
		
		setSensor(sensor);
	}
	
	@Override
	public void onSelectedSensorChanged(PSensor selecedSensor) {
		setSensor(selecedSensor);
	}
	
	private void setSensor(PSensor sensor) {
		if (igcb != null)
			APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
		this.sensor = sensor;
		fillChart();
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
			public void callback(BaseCallbackObject value)
					throws RemoteException {
				if (value instanceof ValueChangedCallback) {
//					final SensorData data = ((ValueChangedCallback) value)
//							.toSensorData();

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
	
		chart.removeAllViews();
		
		View view = graphRenderer.createGraph(
				sensor, getContext(), NUMBER_OF_VALUES, loadFromStorage);
		
		chart.addView(view);
		
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view
				.getLayoutParams();
		layoutParams.width = 250;
		layoutParams.height = 140;
	}
	

	@Override
	protected void OnPauseButton(State state) {
		super.OnPauseButton(state);

		if (state == State.PAUSE && igcb != null)
			APIFunctions.unregisterCallback(igcb, CallbackFlags.VALUE_CHANGED);
		else if (state == State.PLAYING && sensor != null) {
			fillChart();
		}

	}
}