package de.unistuttgart.vis.wearable.os.developmentModule;

import java.util.List;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TemperatureModule extends PopupModuleDate {

	private final int NUMBER_OF_VALUES = 1;

	public TemperatureModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		TextView content = new TextView(context);
//
//		PSensor[] temperatureSensors = APIFunctions
//				.getAllSensors(SensorType.TEMPERATURE);
//
//		PSensor temperatureSensor = temperatureSensors[0];
//
//		Vector<SensorData> temperatureData = temperatureSensor
//				.getRawData(NUMBER_OF_VALUES);
//
//		if (temperatureData.isEmpty()) {
//			content.setText("no Data");
//		} else {
//			content.setText(temperatureData.get(0).getData()[0] + "");
//		}
		
		List<SensorData> datas = APIFunctions.getTemperature(1);
		if (datas != null && datas.size() > 0) {
			SensorData data = datas.get(0);
			
			if(data.getDimension() >= 1) {
				content.setText(String.format("(%.2f, %.2f, %.2f)", data.getData()[0],
						data.getData()[1], data.getData()[2]));

			}
		}

		super.createLayout(context, content, R.drawable.temperature,
				"Temperature");
	}

}
