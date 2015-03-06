package de.unistuttgart.vis.wearable.os.developmentModule;

import java.util.List;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CyclingModule extends PopupModuleDate {

	public CyclingModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		TextView content = new TextView(context);
		

		List<SensorData> datas = APIFunctions.getAccelerometer(1);
		if (datas != null && datas.size() > 0) {
			SensorData data = datas.get(0);
			
			if(data.getDimension() >= 3) {
				content.setText(String.format("(%.2f, %.2f, %.2f)", data.getData()[0],
						data.getData()[1], data.getData()[2]));

			}
		}

	

		super.createLayout(context, content, R.drawable.bike, "Cycling");
	}

}
