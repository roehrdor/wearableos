package de.unistuttgart.vis.wearable.os.developmentModule;

import java.util.List;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.graph.GraphData;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GpsModule extends PopupModuleDate {

	private final int NUMBER_OF_VALUES = 1;

	public GpsModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		TextView content = new TextView(context);

		// Button button = new Button(context);
		//
		// button.setText("show in map");
		// button.setBackgroundColor(Color.parseColor("#606060"));
		// button.setTextColor(Color.WHITE);
		//
		// button.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View view) {
		//
		// openMapsActivity();
		//
		// }
		// });
		//
		// content.addView(button);

		// PSensor[] gpsSensors = APIFunctions
		// .getAllSensors(SensorType.GPS_SENSOR);
		//
		//
		//
		// PSensor gpsSensor = gpsSensors[0];
		//
		// Vector<SensorData> gpsData = gpsSensor.getRawData(NUMBER_OF_VALUES);
		//
		// if (gpsData.isEmpty()) {
		// content.setText("no Data");
		// } else {
		// content.setText(gpsData.get(0).getData()[0] + "");
		// }

		List<SensorData> datas = APIFunctions.getGPS(1);
		if (datas != null && datas.size() > 0) {
			SensorData data = datas.get(0);
			
			if(data.getDimension() >= 2) {
				content.setText(String.format("(%.2f, %.2f, %.2f)", data.getData()[0],
						data.getData()[1], data.getData()[2]));

			}
		}

		super.createLayout(context, content, R.drawable.gps, "GPS");
	}

	public void openMapsActivity() {

		// Context context = getContext();
		//
		// Intent intent = new Intent(context, MapsActivity.class);
		//
		// context.startActivity(intent);

	}
}
