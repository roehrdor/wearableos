//package de.unistuttgart.vis.wearable.os.activityRecognition;
//
//import java.util.LinkedHashMap;
//
//import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
//import de.unistuttgart.vis.wearable.os.sensors.SensorType;
//import android.util.Log;
//
///**
// * contains all data of a given time window and the extracted features of this
// * data
// *
// */
//public class FeatureSet_old extends LinkedHashMap<String, Double> {
//	private static final long serialVersionUID = 3821011652609898640L;
//
//	@SuppressWarnings("deprecation")
//	public FeatureSet_old(TimeWindow timeWindow) {
//		StatisticalFeatureExtraction stat;
//		StructuralFeatureExtraction stru;
//		TransientFeatureExtraction tran;
//		int sensorDimension = 0;
//
//		// calculates all features (statistical for accelerometer and
//		// structural/transient for the rest from all virtual sensors).
//		for (Entry<String, PSensor> entry : timeWindow.entrySet()) {
//
//			try {
//				sensorDimension = entry.getValue()
//						.getSensorType().getDimension();
//				
//				if (entry.getValue().getSensorType().equals(
//						SensorType.ACCELEROMETER)) {
//					stat = new StatisticalFeatureExtraction(entry.getValue());
//
//					for (int dimension = 0; dimension < sensorDimension; dimension++) {
//						this.put(entry.getKey() + "_mean_dimension_"
//								+ dimension, stat.getMean()[dimension]);
//						this.put(entry.getKey() + "_median_dimension_"
//								+ dimension, stat.getMedian()[dimension]);
//						this.put(entry.getKey()
//								+ "_interquartileRange_dimension_" + dimension,
//								stat.getInterquartileRange()[dimension]);
//						this.put(entry.getKey() + "_rootMeanSquare_dimension_"
//								+ dimension,
//								stat.getRootMeanSquare()[dimension]);
//						this.put(entry.getKey() + "_variance_dimension_"
//								+ dimension, stat.getVariance()[dimension]);
//						this.put(entry.getKey()
//								+ "_meanAbsoluteDeviation_dimension_"
//								+ dimension,
//								stat.getMeanAbsoluteDeviation()[dimension]);
//						this.put(entry.getKey()
//								+ "_standardDeviation_dimension_" + dimension,
//								stat.getStandardDeviation()[dimension]);
//						this.put(entry.getKey() + "_correlation_dimension_"
//								+ dimension, stat.getCorrelation()[dimension]);
//					}
//
//				} else {
//					
//					try {
//						stru = new StructuralFeatureExtraction(entry.getValue());
//						
//					} catch (IllegalStateException e) {
//						timeWindow.setActivityLabel("broken (" + e.toString() +")");
//						return;
//					}
//					
//					tran = new TransientFeatureExtraction(entry.getValue());
//
//					for (int dimension = 0; dimension < sensorDimension; dimension++) {
//						this.put(entry.getKey() + "_trend_dimension_"
//								+ dimension, tran.getTrend()[dimension]);
//						this.put(entry.getKey() + "_magnitude_dimension_"
//								+ dimension, tran.getMagnitude()[dimension]);
//						this.put(entry.getKey() + "_signedMagnitude_dimension_"
//								+ dimension,
//								tran.getSignedMagnitude()[dimension]);
//
//						// polynomial degree
//						for (int degree = 3; degree >= 0; degree--) {
//							this.put(entry.getKey() + "_polynomCoefficient_"
//									+ degree + "_dimension_" + dimension,
//									stru.getCoefficient()[dimension][degree]);
//						}
//						this.put(entry.getKey() + "_R2_dimension_" + dimension,
//								stru.getR2()[dimension]);
//					}
//
//				}
//			} catch (NullPointerException e) {
//				Log.e("har", "[FeatureSet] skipped training caused by NullPointerException");
//			}
//		}
//	}
//}
