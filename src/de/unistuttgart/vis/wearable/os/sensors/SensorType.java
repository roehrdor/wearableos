/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.sensors;

import android.graphics.Bitmap;

import java.util.Arrays;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.graph.GraphType;

/**
 * This enumeration describes and defines several different types of sensors
 * such types as
 * 
 * @author pfaehlfd
 */
public enum SensorType {
	HEARTRATE(1, new MeasurementSystems[]{}, R.drawable.heartrate),
	ACCELEROMETER(3, new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.ANGLOSAXON}, R.drawable.accelerometer),
	MAGNETIC_FIELD(3, new MeasurementSystems[]{MeasurementSystems.TESLA}, R.drawable.magnetic_field),
	GYROSCOPE(3, new MeasurementSystems[]{MeasurementSystems.RADIAN}, R.drawable.ic_launcher),
	LIGHT(1, new MeasurementSystems[]{MeasurementSystems.LUX}, R.drawable.ic_launcher),
	PRESSURE(1, new MeasurementSystems[]{MeasurementSystems.PASCAL}, R.drawable.pressure),
	PROXIMITY(1, new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.ANGLOSAXON}, R.drawable.proximity),
	GRAVITY(1, new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.ANGLOSAXON}, R.drawable.ic_launcher),
	ROTATION_VECTOR(3, new MeasurementSystems[]{MeasurementSystems.RADIAN}, R.drawable.rotation_vector),
	RELATIVE_HUMIDITY(1, new MeasurementSystems[]{MeasurementSystems.PERCENT}, R.drawable.ic_launcher),
	TEMPERATURE(1, new MeasurementSystems[]{MeasurementSystems.TEMPERATURE}, R.drawable.temperature),
	GPS_SENSOR(4, new MeasurementSystems[]{MeasurementSystems.GPS}, R.drawable.gps);
	
	int dimension;
	MeasurementSystems[] measurementSystems;
    int iconID;
	
	private SensorType(int dimension, MeasurementSystems[] measurementSystems, int iconID) {
		this.dimension = dimension;
		this.measurementSystems = measurementSystems;
        this.iconID = iconID;
	}

    public int getIconID() {
        return iconID;
    }

    @Deprecated
	public int getDimension() {
		return dimension;
	}
	
	public GraphType getDefaultGraphType() {
		return GraphType.LINE;
	}
	
	public MeasurementSystems[] getMeasurementSystems() {
		return measurementSystems;
	}
	
	public boolean containsMeasurementSystem(MeasurementSystems measurementSystem){
		return Arrays.asList(this.measurementSystems).contains(measurementSystem);
	}
}