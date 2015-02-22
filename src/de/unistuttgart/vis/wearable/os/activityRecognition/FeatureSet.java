package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.LinkedHashMap;

import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

public class FeatureSet extends LinkedHashMap<String, Double> {

	private static final long serialVersionUID = -1741609859493206564L;

	private int polynomDegree = 3;
	private float[][] values;

	public FeatureSet(TimeWindow timeWindow) {
		StatisticalFeatureExtraction stat;
		StructuralFeatureExtraction stru;
		TransientFeatureExtraction tran;

		for (Entry<Integer, PSensorData> entry : timeWindow.entrySet()) {
			int sensorDimension = entry.getValue().getDimension();
			int dataListSize = entry.getValue().toSensorDataList().size();
			
			if(dataListSize <= 2) {
				timeWindow.setActivityLabel("dead (time window does not hold enough data)");
				return;
			}

			if (sensorDimension == 0) {
				timeWindow.setActivityLabel("dead (sensorDimension is 0)");
				return;
			}
			values = new float[sensorDimension][dataListSize];
			int counter = 0;
			for (SensorData sensorData : entry.getValue().toSensorDataList()) {

				for (int dimension = 0; dimension < sensorDimension; dimension++) {

					values[dimension][counter] = sensorData.getData()[dimension];
				}
				counter++;
			}

			if (SensorType.values()[APIFunctions
					.SENSORS_SENSOR_getSensorType(entry.getKey())]
					.equals(SensorType.ACCELEROMETER)) {

				for (int dimension = 0; dimension < sensorDimension; dimension++) {
					stat = new StatisticalFeatureExtraction(values[dimension]);
					this.put(entry.getKey() + "_mean_" + dimension,
							stat.getMean());
					this.put(entry.getKey() + "_median_" + dimension,
							stat.getMedian());
					this.put(entry.getKey() + "_interquartileRange_"
							+ dimension, stat.getInterquartileRange());
					this.put(entry.getKey() + "_rootMeanSquare_" + dimension,
							stat.getRootMeanSquare());
					this.put(entry.getKey() + "_varianceMean_" + dimension,
							stat.getVarianceMean());
					this.put(entry.getKey() + "_varianceMedian_" + dimension,
							stat.getVarianceMedian());
					this.put(entry.getKey() + "_meanAbsoluteDeviation_"
							+ dimension, stat.getMeanAbsoluteDeviation());
					this.put(entry.getKey() + "_medianAbsoluteDeviation_"
							+ dimension, stat.getMedianAbsoluteDeviation());
					this.put(entry.getKey() + "_standardDeviationMean_"
							+ dimension, stat.getStandardDeviationMean());
					this.put(entry.getKey() + "_standardDeviationMedian_"
							+ dimension, stat.getStandardDeviationMedian());
					try {
						if (sensorDimension > 1) {
							if (dimension + 1 < sensorDimension) {
								this.put(
										entry.getKey() + "_correlationMean_"
												+ dimension,
										stat.getCorrelationMean(values[dimension + 1]));
								this.put(
										entry.getKey() + "_correlationMedian_"
												+ dimension,
										stat.getCorrelationMedian(values[dimension + 1]));
							} else {
								this.put(entry.getKey() + "_correlationMean_"
										+ dimension,
										stat.getCorrelationMean(values[0]));
								this.put(entry.getKey() + "_correlationMedian_"
										+ dimension,
										stat.getCorrelationMedian(values[0]));
							}
						}
					} catch (IllegalStateException e) {
						timeWindow.setActivityLabel("dead ("
								+ e.getLocalizedMessage() + ")");
						return;
					}
				}
			} else {

				for (int dimension = 0; dimension < sensorDimension; dimension++) {
					try {
						stru = new StructuralFeatureExtraction(
								values[dimension], polynomDegree);
					} catch (IllegalStateException e) {
						timeWindow.setActivityLabel("dead ("
								+ e.getLocalizedMessage() + ")");
						return;
					}
					for (int degree = 0; degree < polynomDegree + 1; degree++) {
						this.put(entry.getKey() + "_polynomCoefficient"
								+ degree + "_" + dimension,
								stru.getCoefficient(degree));
					}
					this.put(entry.getKey() + "_meanVariation_" + dimension,
							stru.getMeanVariation());

					tran = new TransientFeatureExtraction(values[dimension]);
					this.put(entry.getKey() + "_trend_" + dimension,
							(double) tran.getTrend());
					this.put(
							entry.getKey() + "_magnitudeOfChange_" + dimension,
							tran.getMagnitudeOfChange());
					this.put(entry.getKey() + "_signedMagnitudeOfChange_"
							+ dimension, tran.getSignedMagnitudeOfChange());

				}
			}
		}
	}

	// private void calculateFeatures() {
	//
	// }

	/**
	 * @return the polynomDegree
	 */
	public int getPolynomDegree() {
		return polynomDegree;
	}

	/**
	 * @param polynomDegree
	 *            the polynomDegree to set
	 */
	public void setPolynomDegree(int polynomDegree) {
		this.polynomDegree = polynomDegree;
	}

}
