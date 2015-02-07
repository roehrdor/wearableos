package de.unistuttgart.vis.wearable.os.sensors;

import java.util.Arrays;

import de.unistuttgart.vis.wearable.os.graph.GraphType;

/**
 * This enumeration describes and defines several different types of sensors
 * such types as heart rate or accelerometers or magnetic fields
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

	/**
	 * Create a new SensorType setting the given dimension and given measurement
	 * system
	 * 
	 * @param dimension
	 *            the dimension of the sensor type
	 * @param measurementSystems
	 *            the measurement system
	 */
	private SensorType(int dimension, MeasurementSystems[] measurementSystems) {
		this.dimension = dimension;
		this.measurementSystems = measurementSystems;
	}

	/**
	 * Get the dimension of the of the sensor type
	 * 
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Get the default graph type
	 * 
	 * @return the default graph type
	 */
	public GraphType getDefaultGraphType() {
		return GraphType.LINE;
	}

	/**
	 * Get the measurement system
	 * 
	 * @return the measurement system
	 */
	public MeasurementSystems[] getMeasurementSystems() {
		return measurementSystems;
	}

	/**
	 * Tests whether the given measurement system is contained in the current
	 * sensor type, if so the function will return true.
	 * 
	 * @param measurementSystem
	 *            the measurement system
	 * @return true if the measurement system is contained in the sensor type
	 */
	public boolean containsMeasurementSystem(
			MeasurementSystems measurementSystem) {
		return Arrays.asList(this.measurementSystems).contains(
				measurementSystem);
	}
}