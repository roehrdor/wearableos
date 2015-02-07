package de.unistuttgart.vis.wearable.os.sensors;

import java.util.Arrays;

import de.unistuttgart.vis.wearable.os.graph.GraphType;

/**
 * This enumeration describes and defines several different types of sensors
 * such types as
 * 
 * @author pfaehlfd
 */
public enum SensorType {
	HEARTRATE(1, new MeasurementSystems[]{}),
	ACCELEROMETER(3, new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.ANGLOSAXON}),
	MAGNETIC_FIELD(3, new MeasurementSystems[]{MeasurementSystems.TESLA}),
	GYROSCOPE(3, new MeasurementSystems[]{MeasurementSystems.RADIAN}),
	LIGHT(1, new MeasurementSystems[]{MeasurementSystems.LUX}),
	PRESSURE(1, new MeasurementSystems[]{MeasurementSystems.PASCAL}),
	PROXIMITY(1, new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.ANGLOSAXON}),
	GRAVITY(1, new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.ANGLOSAXON}),
	ROTATION_VECTOR(5, new MeasurementSystems[]{MeasurementSystems.RADIAN}),
	RELATIVE_HUMIDITY(1, new MeasurementSystems[]{MeasurementSystems.PERCENT}),
	TEMPERATURE(1, new MeasurementSystems[]{MeasurementSystems.TEMPERATURE}),
	GPS_SENSOR(4, new MeasurementSystems[]{MeasurementSystems.GPS});
	
	int dimension;
	MeasurementSystems[] measurementSystems;
	
	private SensorType(int dimension, MeasurementSystems[] measurementSystems) {
		this.dimension = dimension;
		this.measurementSystems = measurementSystems;
	}
	
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