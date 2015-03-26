/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.LinkedHashMap;

public class FeatureSet extends LinkedHashMap<String, Double> {

	private static final long serialVersionUID = -1741609859493206564L;

	private int polynomDegree = 3;
	private float[][] values;

	public FeatureSet(TimeWindow timeWindow) {
		StatisticalFeatureExtraction stat;
		StructuralFeatureExtraction stru;
		TransientFeatureExtraction tran;

		for (Entry<String, float[][]> entry : timeWindow.entrySet()) {
			this.values = entry.getValue();

			if (values[0].length <= 2) {
				timeWindow
						.setActivityLabel("dead (time window does not hold enough data)");
				return;
			}

			if (values.length == 0) {
				timeWindow.setActivityLabel("dead (sensorDimension is 0)");
				return;
			}
			String key = entry.getKey().split("_")[0];

			if (entry.getKey().split("_")[1].equals("ACCELEROMETER")) {

				for (int dimension = 0; dimension < values.length; dimension++) {
					stat = new StatisticalFeatureExtraction(values[dimension]);
					this.put(key + "_mean_" + dimension, stat.getMean());
					this.put(key + "_median_" + dimension, stat.getMedian());
					this.put(key + "_interquartileRange_" + dimension,
							stat.getInterquartileRange());
					this.put(key + "_rootMeanSquare_" + dimension,
							stat.getRootMeanSquare());
					this.put(key + "_varianceMean_" + dimension,
							stat.getVarianceMean());
					this.put(key + "_varianceMedian_" + dimension,
							stat.getVarianceMedian());
					this.put(key + "_meanAbsoluteDeviation_" + dimension,
							stat.getMeanAbsoluteDeviation());
					this.put(key + "_medianAbsoluteDeviation_" + dimension,
							stat.getMedianAbsoluteDeviation());
					this.put(key + "_standardDeviationMean_" + dimension,
							stat.getStandardDeviationMean());
					this.put(key + "_standardDeviationMedian_" + dimension,
							stat.getStandardDeviationMedian());
//					try {
//						if (values.length > 1) {
//							if (dimension + 1 < values.length) {
//								this.put(
//										key + "_correlationMean_" + dimension,
//										stat.getCorrelationMean(values[dimension + 1]));
//								this.put(
//										key + "_correlationMedian_" + dimension,
//										stat.getCorrelationMedian(values[dimension + 1]));
//							} else {
//								this.put(key + "_correlationMean_" + dimension,
//										stat.getCorrelationMean(values[0]));
//								this.put(key + "_correlationMedian_"
//										+ dimension,
//										stat.getCorrelationMedian(values[0]));
//							}
//						}
//					} catch (IllegalStateException e) {
//						timeWindow.setActivityLabel("dead ("
//								+ e.getLocalizedMessage() + ")");
//						return;
//					}
				}
			} else {

				for (int dimension = 0; dimension < values.length; dimension++) {
					try {
						stru = new StructuralFeatureExtraction(
								values[dimension], polynomDegree);
					} catch (IllegalStateException e) {
						timeWindow.setActivityLabel("dead ("
								+ e.getLocalizedMessage() + ")");
						return;
					}
					for (int degree = 0; degree < polynomDegree + 1; degree++) {
						this.put(key + "_polynomCoefficient" + degree + "_"
								+ dimension, stru.getCoefficient(degree));
					}
					this.put(key + "_meanVariation_" + dimension,
							stru.getMeanVariation());

					tran = new TransientFeatureExtraction(values[dimension]);
					this.put(key + "_trend_" + dimension,
							(double) tran.getTrend());
					this.put(key + "_magnitudeOfChange_" + dimension,
							tran.getMagnitudeOfChange());
					this.put(key + "_signedMagnitudeOfChange_" + dimension,
							tran.getSignedMagnitudeOfChange());

				}
			}
		}
	}

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
