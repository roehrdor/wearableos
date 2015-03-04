package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
//import de.unistuttgart.vis.wearable.os.api.APIFunctions;
//import de.unistuttgart.vis.wearable.os.sensors.Sensor;
//import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
//import android.util.Log;
//import android.widget.TextView;

public class SittingModule extends PopupModuleDate {

	// String sensor;
	// private Sensor accelerometerSensor;

	public SittingModule(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		 TextView content = new TextView(context);
		//
		// Sensor[] sensors = APIFunctions.getSensors();
		// if (sensors != null) {
		// for (Sensor sensor : sensors) {
		//
		// if (sensor.getSensorType() == SensorType.ACCELEROMETER) {
		// accelerometerSensor = sensor;
		// Log.d("TEST", sensor
		// .getDisplayedSensorName());
		// content.setText(""
		// + sensor.getRawData().get(0).getData()[0]);
		//
		// }
		// }
		// }

		super.createLayout(context, content, R.drawable.sitting, "Sitting");
	}

}
