package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.Vector;

import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.PSensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import android.util.Log;

/**
 * Calculates the transient features
 * 
 * @author Tobias
 *
 */
public class TransientFeatureExtraction_old {

	private Vector<SensorData> sensorData;
	private double[] trend;
	private double[] magnitude;
	private int sensorDimension;

	/**
	 * computes the transient features for every dimension separately
	 * 
	 * @param pSensor
	 *            virtual sensor where the data comes from
	 * @throws MissingSensorPropertyException
	 *             if no dimension is set
	 */
	@SuppressWarnings("deprecation")
	public TransientFeatureExtraction_old(PSensorData pSensorData) {
		sensorDimension = 0;
//		sensorDimension = pSensor.getSensorType().getDimension();
		trend = new double[sensorDimension];
		magnitude = new double[sensorDimension];
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			try {
//				this.sensorData = pSensor.getRawData();
			} catch (Exception e) {
				Log.e("har",
						"[TransientFeatureExtraction] exception catched: "
								+ e.toString());
			}
			computeFeatures(dimension);
		}
	}

	private void computeFeatures(int dimension) {
		trend[dimension] = trend(dimension);
		magnitude[dimension] = magnitudeOfChange(dimension);
	}

	public void recomputeFeatures() {
		computeFeatures(0);
	}

	/**
	 * uses linear regression to compute the trend of a given time series
	 */
	private double calculateLine(int dimension) {
		double yMean = 0, xMean = 0;
		int i = 0;
		for (SensorData data : sensorData) {
			yMean += data.getData()[dimension];
			xMean += i;
			i++;
		}
		yMean = yMean / sensorData.size();
		xMean = xMean / sensorData.size();
		double a = 0, c = 0;
		i = 0;
		for (SensorData data : sensorData) {
			a += (data.getData()[dimension] - yMean)
					* (i - xMean);
			c += Math.pow((i - xMean), 2);
			i++;
		}
		a = a / c;
		c = yMean - a * xMean;
		return a;
	}

	/**
	 * 
	 * @return 1 if the line goes up, -1 if the line goes down and 0 if the line
	 *         is horizontal
	 */
	public int trend(int dimension) {
		final double TREND_AREA = 0.1;
		double x = calculateLine(dimension);
		if (x >= TREND_AREA) {
			return 1;
		} else if (x <= TREND_AREA) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 
	 * @return the maximum deviation between the beginning and the and of a time
	 *         series/virtual sensor
	 */
	private double magnitudeOfChange(int dimension) {
		final double SUBSET_AREA = 0.1;
		int end = sensorData.size() - 1;
		int subset = (int) (end * SUBSET_AREA);

		double minStart = sensorData.get(0).getData()[dimension];
		double maxStart = minStart;
		double minEnd = sensorData.get(sensorData.size() - 1).getData()[dimension];
		double maxEnd = minEnd;

		for (int i = 0; i <= subset; i++) {
			double currentValue = sensorData.get(i).getData()[dimension];
			maxStart = Math.max(maxStart, currentValue);
			minStart = Math.min(minStart, currentValue);
		}

		for (int i = end; i >= end - subset; i--) {
			double currentValue = sensorData.get(i).getData()[dimension];
			maxEnd = Math.max(maxEnd, currentValue);
			minEnd = Math.min(minEnd, currentValue);
		}

		return Math.max(Math.abs(maxEnd - minStart),
				Math.abs(maxStart - minEnd));

	}

	public double[] getTrend() {
		return trend;
	}

	public double[] getMagnitude() {
		return magnitude;
	}

	public double[] getSignedMagnitude() {
		double[] signedMagnitude = new double[trend.length];
		for (int i = 0; i < trend.length; i++) {
			signedMagnitude[i] = trend[i] * magnitude[i];
		}
		return signedMagnitude;
	}
}
