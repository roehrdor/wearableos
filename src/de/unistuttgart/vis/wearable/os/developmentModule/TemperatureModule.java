package de.unistuttgart.vis.wearable.os.developmentModule;

import android.content.Context;
import android.util.AttributeSet;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

/**
 * @author Sophie Ogando
 */
public class TemperatureModule extends LiveTextSensorModule {
	
	public TemperatureModule(Context context, AttributeSet attrs) {
		super(context, attrs, SensorType.TEMPERATURE, R.drawable.temperature, "Temperature");

	}

}
