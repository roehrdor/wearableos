package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.Arrays;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.PSensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import android.util.Log;

/**
 * This class extracts the statistical features of a given time series
 * 
 * @author Tobias
 *
 */
public class StatisticalFeatureExtraction_old {
	private double[] mean;
	private double[] median;
	private double[] interquartileRange;
	private double[] variance;
	private double[] meanAbsoluteDeviation;
	private double[] rootMeanSquare;
	private double[] correlation;
	private double[] standardDeviation;
	private int sensorDimension;
	private double[][] sensorData;
	private Vector<SensorData> newSensorData;

	/**
	 * prepares a new array with sorted data and computes the features
	 * 
	 * @param pSensor
	 *            virtual sensor which includes the sensor data
	 */
	public StatisticalFeatureExtraction_old(PSensorData pSensorData) {
		newSensorData = pSensorData.toSensorDataList();
		sensorDimension = 0;
//		sensorDimension = pSensor.getSensorType().getDimension();
					
		mean = new double[sensorDimension];
		median = new double[sensorDimension];
		interquartileRange = new double[sensorDimension];
		variance = new double[sensorDimension];
		meanAbsoluteDeviation = new double[sensorDimension];
		rootMeanSquare = new double[sensorDimension];
		correlation = new double[sensorDimension];
		standardDeviation = new double[sensorDimension];
//		sensorData = new double[sensorDimension][pSensor.getRawData().size()];
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			for (int i = 0; i < sensorData[0].length; i++) {
				try {
//					sensorData[dimension][i] = pSensor.getRawData().get(i)
//							.getData()[dimension];
				} catch (Exception e) {
					Log.e("har",
							"[StatisticalFeatureExtraction] exception catched: "
									+ e.toString() + " - " + e.getLocalizedMessage());
					
				}
			}
			Arrays.sort(sensorData[dimension]);
			computeFeatures(dimension);
		}
		computeCorrelation();
	}

	/**
	 * Prepares the statistical features of a given time series
	 */
	private void computeFeatures(int dimension) {
		mean[dimension] = 0;
		rootMeanSquare[dimension] = 0;

		for (double data : sensorData[dimension]) {
			mean[dimension] += data;
			rootMeanSquare[dimension] += Math.pow(data, 2);
		}
		mean[dimension] = mean[dimension] / sensorData.length;

		for (int i = 0; i < sensorData[0].length; i++) {
			variance[dimension] += Math.pow(sensorData[dimension][i]
					- mean[dimension], 2);
			meanAbsoluteDeviation[dimension] += Math
					.abs(sensorData[dimension][i] - mean[dimension]);
		}

		variance[dimension] = variance[dimension] / (sensorData.length - 1);
		meanAbsoluteDeviation[dimension] = meanAbsoluteDeviation[dimension]
				/ sensorData.length;
	}
	
	private void computeCorrelation() {
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			for (int i = 0; i < sensorData[0].length; i++) {

				if (dimension < sensorDimension - 1) {
					correlation[dimension] += (sensorData[dimension][i] - mean[dimension])
							* (sensorData[dimension + 1][i] - mean[dimension + 1]);
				} else {
					correlation[dimension] += (sensorData[dimension][i] - mean[dimension])
							* (sensorData[0][i] - mean[0]);
				}
			}
		}
	}

	/**
	 * recalculates the features TODO
	 */
	@SuppressWarnings("unused")
	private void recomputeFeatures() {
		computeFeatures(0);
	}

	/**
	 * 
	 * @return the x-axe means of a given 2D array
	 */
	public double[] getMean() {
		return mean;
	}

	/**
	 * 
	 * @return the x-axe medians of a given 2D array
	 */
	public double[] getMedian() {
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			if (sensorData.length % 2 == 0) {
				median[dimension] = (sensorData[dimension][sensorData.length / 2] + sensorData[dimension][sensorData.length / 2 - 1]) / 2;
			} else {
				median[dimension] = sensorData[dimension][sensorData.length / 2];
			}
		}
		return median;
	}

	/**
	 * interquartile range is defined by Q.75 - Q.25 where Q is Quantile
	 * 
	 * @return the x-axe interquartile ranges of a given 2D array
	 */
	public double[] getInterquartileRange() {
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			interquartileRange[dimension] = sensorData[dimension][(int) Math
					.round(sensorData.length / 4.0) * 3 - 1]

					- sensorData[dimension][(int) Math
							.round(sensorData.length / 4.0) - 1];
		}
		return interquartileRange;
	}

	/**
	 * 
	 * @return the x-axe root mean squares of a given 2D array
	 */
	public double[] getRootMeanSquare() {
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			rootMeanSquare[dimension] = Math.sqrt(rootMeanSquare[dimension]
					/ sensorData.length);
		}
		return rootMeanSquare;
	}

	/**
	 * 
	 * @return the x-axe variances of a given 2D array
	 */
	public double[] getVariance() {
		return variance;
	}

	/**
	 * 
	 * @return the x-axe mean absolute deviations of a given 2D array
	 */
	public double[] getMeanAbsoluteDeviation() {
		return meanAbsoluteDeviation;
	}

	/**
	 * 
	 * @return the x-axe standard deviations of a given 2D array
	 */
	public double[] getStandardDeviation() {
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			standardDeviation[dimension] = Math.sqrt(variance[dimension]);
		}
		return standardDeviation;
	}

	/**
	 * some calculation from Wikipedia
	 * 
	 * @return the correlations between the x-axes of a given 2D array
	 */
	public double[] getCorrelation() {
		double[] tempCorrelation = correlation.clone();
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			if (dimension < sensorDimension - 1) {
				correlation[dimension] = tempCorrelation[dimension]
						/ (getStandardDeviation()[dimension]
						* getStandardDeviation()[dimension + 1]);
			} else {
				correlation[dimension] = tempCorrelation[dimension]
						/ (getStandardDeviation()[dimension]
						* getStandardDeviation()[0]);
			}
		}
		return correlation;
	}
}