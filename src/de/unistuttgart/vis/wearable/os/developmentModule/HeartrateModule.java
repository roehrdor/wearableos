package de.unistuttgart.vis.wearable.os.developmentModule;

import java.util.List;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
//import de.unistuttgart.vis.wearable.os.api.APIFunctions;
//import de.unistuttgart.vis.wearable.os.sensors.Sensor;
//import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class HeartrateModule extends PopupModuleDate {


	public HeartrateModule(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		 TextView content = new TextView(context);
		 
		 List<SensorData> datas = APIFunctions.getHeartRate(1);
			if (datas != null && datas.size() > 0) {
				SensorData data = datas.get(0);
				
				if(data.getDimension() >= 1) {
					content.setText(String.format("(%.2f, %.2f, %.2f)", data.getData()[0],
							data.getData()[1], data.getData()[2]));

				}
			}
		 
		super.createLayout(context, content, R.drawable.graph, "Heartrate");
	}

}
