package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.LinkedHashMap;

import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensorData;
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

			values = new float[sensorDimension][entry.getValue()
					.toSensorDataList().size()
					/ sensorDimension];

			if (SensorType.values()[APIFunctions
					.SENSORS_SENSOR_getSensorType(entry.getKey())]
					.equals(SensorType.ACCELEROMETER)) {

				for (int i = 0; i < sensorDimension; i++) {

					for (int j = 0; j < entry
							.getValue()
							.toSensorDataList()
							.subList(i * sensorDimension,
									(i + 1) * sensorDimension).size(); j++) {

						values[i] = entry
								.getValue()
								.toSensorDataList()
								.subList(i * sensorDimension,
										(i + 1) * sensorDimension).get(j)
								.getData();
					}

				}

				for (int i = 0; i < sensorDimension; i++) {
					stat = new StatisticalFeatureExtraction(values[i]);
					this.put(entry.getKey() + "_mean_" + i, stat.getMean());
					this.put(entry.getKey() + "_median_" + i, stat.getMean());
					this.put(entry.getKey() + "_interquartileRange_" + i,
							stat.getMean());
					this.put(entry.getKey() + "_rootMeanSquare_" + i,
							stat.getMean());
					this.put(entry.getKey() + "_varianceMean_" + i,
							stat.getMean());
					this.put(entry.getKey() + "_varianceMedian_" + i,
							stat.getMean());
					this.put(entry.getKey() + "_meanAbsoluteDeviation_" + i,
							stat.getMean());
					this.put(entry.getKey() + "_medianAbsoluteDeviation_" + i,
							stat.getMean());
					this.put(entry.getKey() + "_standardDeviationMean_" + i,
							stat.getMean());
					this.put(entry.getKey() + "_standardDeviationMedian_" + i,
							stat.getMean());
					try {
						if (i + 1 < sensorDimension) {
							this.put(entry.getKey() + "_correlationMean_" + i,
									stat.getCorrelationMean(values[i + 1]));
							this.put(entry.getKey() + "_correlationMean_" + i,
									stat.getCorrelationMedian(values[i + 1]));
						} else {
							this.put(entry.getKey() + "_correlationMean_" + i,
									stat.getCorrelationMean(values[0]));
							this.put(entry.getKey() + "_correlationMean_" + i,
									stat.getCorrelationMedian(values[0]));
						}
					} catch (IllegalStateException e) {
						timeWindow.setActivityLabel("broken (" + e.getMessage()
								+ ")");
						return;
					}
				}
			} else {

				for (int i = 0; i < sensorDimension; i++) {
					try {
						stru = new StructuralFeatureExtraction(values[i],
								polynomDegree);
					} catch (IllegalStateException e) {
						timeWindow.setActivityLabel("broken (" + e.getMessage()
								+ ")");
						return;
					}
					for (int j = 0; j < polynomDegree + 1; i++) {
						this.put(entry.getKey() + "_polynomCoefficient" + j
								+ "_" + i, stru.getCoefficient(j));
					}
					this.put(entry.getKey() + "_meanVariation_" + i,
							stru.getMeanVariation());

					tran = new TransientFeatureExtraction(values[i]);
					this.put(entry.getKey() + "_trend_" + i,
							(double) tran.getTrend());
					this.put(entry.getKey() + "_magnitudeOfChange_" + i,
							tran.getMagnitudeOfChange());
					this.put(entry.getKey() + "_signedMagnitudeOfChange_" + i,
							tran.getSignedMagnitudeOfChange());

				}
			}
		}
	}
	
//	private void calculateFeatures() {
//		
//	}

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
