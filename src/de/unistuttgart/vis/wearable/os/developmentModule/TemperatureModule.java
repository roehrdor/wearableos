package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TemperatureModule extends PopupModule1 {

	public TemperatureModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		TextView content = new TextView(context);
		content.setText("20 degree right now...");

		// try {
		// SettingsModule.getInstance().getSensorData(sensorName, raw);
		// } catch (MissingSensorPropertyException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		super.createLayout(context, content, R.drawable.temperature,
				"Temperature");
	}

}
