package de.unistuttgart.vis.wearable.os.developmentModule;

import android.content.Context;
import android.util.AttributeSet;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

/**
 * @author Sophie Ogando
 */
public class ProximityModule extends LiveTextSensorModule {

	public ProximityModule(Context context, AttributeSet attrs) {
		super(context, attrs, SensorType.PROXIMITY, R.drawable.proximity, "Proximity");
	}

}
