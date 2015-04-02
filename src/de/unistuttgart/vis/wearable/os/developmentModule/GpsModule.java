package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Sophie Ogando
 */
public class GpsModule extends LiveTextSensorModule {
	public GpsModule(Context context, AttributeSet attrs) {
		super(context, attrs, SensorType.GPS_SENSOR, R.drawable.gps, "GPS");
	}
}
