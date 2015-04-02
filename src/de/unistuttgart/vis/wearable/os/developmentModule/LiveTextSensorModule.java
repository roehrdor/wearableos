package de.unistuttgart.vis.wearable.os.developmentModule;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.developmentModule.SelectSensorPopupMenu.SelectedSensorChangedListener;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;


/**
 * @author Sophie Ogando
 */
//abstract as it has no constructor that is used by tools
public class LiveTextSensorModule extends BasisModule implements SelectedSensorChangedListener {
	
	private class LiveTextField extends AbstractLiveTextField {
		
		public LiveTextField() {
			super(getContext(), sensor);
		}
		
		@Override
		public void acceptText(String text) {
			content.setText(text);
		}
	}
	
	private TextView content;
	private PSensor sensor;
	private AbstractLiveTextField liveTextField;
	
	public LiveTextSensorModule(Context context, AttributeSet attrs, SensorType type, int icon, String title) {
		super(context, attrs);

		content = new TextView(context);
		content.setText("no sensor");

		if(APIHandle.isServiceBound()) {
			PSensor[] sensors = APIFunctions.getAllSensors(type);
		
			if(sensors.length > 0) {
				sensor = sensors[0];  //use first sensor
			}
		}

		super.createLayout(context, content, icon, title);
	}
	
	public LiveTextSensorModule(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		content = new TextView(context);
		content.setText("no sensor");
		
		super.createLayout(context, content, R.drawable.graph, "Sensor Value");
		
		TypedArray a = context.getTheme().obtainStyledAttributes
				(attrs, R.styleable.Module, 0, 0);
		
		try{
			//look if sensor type is defined
			int enumId = a.getInt(R.styleable.Module_sensor, -1);
			if(enumId != -1) {
				SensorType type = SensorType.values()[enumId];
				
				//use sensor type
				if (APIHandle.isServiceBound()) {
					PSensor[] sensors = APIFunctions.getAllSensors(type);
					if (sensors.length > 0) {
						sensor = sensors[0];
					}
				}
			} else {
				if (APIHandle.isServiceBound()) {
					PSensor[] sensors = APIFunctions.getAllSensors();
					if (sensors.length > 0) {
						sensor = sensors[0];
					}
				}
				//user can select sensor
				SelectSensorPopupMenu sensorPopup = new SelectSensorPopupMenu();
				sensorPopup.addSelectedSensorChangedListener(this);
				super.setPopupWindow(sensorPopup);
			}
		} finally {
			a.recycle();
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		if(sensor != null) {
			liveTextField = new LiveTextField();
		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		liveTextField.unRegisterCallback();
		super.onDetachedFromWindow();
	}
	
	@Override
	protected void OnPauseButton(State state) {
		if(liveTextField != null) {
			if(state == State.PAUSE)
				liveTextField.unRegisterCallback();
			else
				liveTextField.reregisterCallback();
		}
		super.OnPauseButton(state);
	}

	@Override
	public void onSelectedSensorChanged(PSensor sensor) {
		this.sensor = sensor;
		if(liveTextField != null) {
			liveTextField.unRegisterCallback();
			liveTextField = new LiveTextField();
		}
	}
}
