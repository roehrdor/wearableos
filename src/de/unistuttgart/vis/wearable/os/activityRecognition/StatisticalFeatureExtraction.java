package de.unistuttgart.vis.wearable.os.activityRecognition;

public class StatisticalFeatureExtraction {
	private double mean = 0;
	private double median = 0;
	private double varianceMean = 0;
	private double varianceMedian = 0;
	private double meanAbsoluteDeviation = 0;
	private double medianAbsoluteDeviation = 0;
	private double rootMeanSquare = 0;
	private double correlation = 0;
	private float[] values;
	
//	public StatisticalFeatureExtraction_New(double[] values) {
//		this.values = values;
//		compute();
//	}
	
	public StatisticalFeatureExtraction(float[] values) {
		this.values = values;
		compute();
	}
	
	private void compute() {
		for(double value : values) {
			mean += value;
			rootMeanSquare += Math.pow(value, 2);
		}
		mean = mean / values.length;
		for(double value : values) {
			varianceMean += Math.pow(value - mean, 2);
			varianceMedian += Math.pow(value - getMedian(), 2);
			meanAbsoluteDeviation += Math.abs(value - mean);
			medianAbsoluteDeviation += Math.abs(value - median);
		}
		varianceMean = varianceMean / (values.length - 1);
		varianceMedian = varianceMedian / (values.length - 1);
		meanAbsoluteDeviation = meanAbsoluteDeviation / values.length;
		medianAbsoluteDeviation = medianAbsoluteDeviation / values.length;
	}
	
	public double getMean() {
		return mean;
	}
	
	public double getMedian() {
		if(values.length % 2 == 0) {
			return (values[values.length / 2] + values[values.length / 2 - 1]) / 2;
		} else {
			return values[values.length / 2];
		}
	}
	
	public double getInterquartileRange() {
		return values[(int) Math.round((values.length / 4.0) * 3.0 - 1)] - 
		              values[(int) Math.round(values.length / 4.0 - 1)];
	}
	
	public double getRootMeanSquare() {
		return Math.sqrt(rootMeanSquare / values.length);
	}
	
	public double getVarianceMean() {
		return varianceMean;
	}
	
	public double getVarianceMedian() {
		return varianceMedian;
	}
	
	public double getMeanAbsoluteDeviation() {
		return meanAbsoluteDeviation;
	}
	
	public double getMedianAbsoluteDeviation() {
		return medianAbsoluteDeviation;
	}
	
	public double getStandardDeviationMean() {
		return Math.sqrt(getVarianceMean());
	}
	
	public double getStandardDeviationMedian() {
		return Math.sqrt(getVarianceMedian());
	}
	
	/**
	 * @param values2
	 * @return
	 * 			Returns the Pearson product-moment correlation coefficient.
	 */
//	public double getCorrelationMean(double[] values2) {
//		if(values.length != values2.length) {
//			throw new IllegalStateException("Length of the two arrays does not match");
//		}
//		double mean2 = 0;
//		double varianceMean2 = 0;
//		double standardDeviationMean2 = 0;
//		for(double value : values2) {
//			mean2 += value;
//		}
//		mean2 = mean2 / values2.length;
//		for(double value : values2) {
//			varianceMean2 += Math.pow(value - mean2, 2);
//		}
//		varianceMean2 = varianceMean2 / (values2.length - 1);
//		standardDeviationMean2 = Math.sqrt(varianceMean2);
//		for(int i = 0; i < values.length; i++) {
//			correlation += (values[i] - getMean()) * (values2[i] - mean2);
//		}
//		return correlation / (getStandardDeviationMean() * standardDeviationMean2);
//	}
	
	/**
	 * @param values2
	 * @return
	 * 			Returns the Pearson product-moment correlation coefficient.
	 */
//	public double getCorrelationMedian(double[] values2) {
//		if(values.length != values2.length) {
//			throw new IllegalStateException("Length of the two arrays does not match");
//		}
//		double median2 = 0;
//		double varianceMedian2 = 0;
//		double standardDeviationMedian2 = 0;
//		if(values2.length % 2 == 0) {
//			median2 = (values[values.length / 2] + values[values.length / 2 - 1]) / 2;
//		} else {
//			median2 = values[values.length / 2];
//		}
//		for(double value : values2) {
//			varianceMedian2 += Math.pow(value - median2, 2);
//		}
//		varianceMedian2 = varianceMedian2 / (values2.length - 1);
//		standardDeviationMedian2 = Math.sqrt(varianceMedian2);
//		for(int i = 0; i < values.length; i++) {
//			correlation += (values[i] - getMedian()) * (values2[i] - median2);
//		}
//		return correlation / (getStandardDeviationMedian() * standardDeviationMedian2);
//	}
	
	/**
	 * @param values2
	 * @return
	 * 			Returns the Pearson product-moment correlation coefficient.
	 */
	public double getCorrelationMean(float[] values2) {
		if(values.length != values2.length) {
			throw new IllegalStateException("Length of the two arrays does not match");
		}
		double mean2 = 0;
		double varianceMean2 = 0;
		double standardDeviationMean2 = 0;
		for(double value : values2) {
			mean2 += value;
		}
		mean2 = mean2 / values2.length;
		for(double value : values2) {
			varianceMean2 += Math.pow(value - mean2, 2);
		}
		varianceMean2 = varianceMean2 / (values2.length - 1);
		standardDeviationMean2 = Math.sqrt(varianceMean2);
		for(int i = 0; i < values.length; i++) {
			correlation += (values[i] - getMean()) * (values2[i] - mean2);
		}
		return correlation / (getStandardDeviationMean() * standardDeviationMean2);
	}
	
	/**
	 * @param values2
	 * @return
	 * 			Returns the Pearson product-moment correlation coefficient.
	 */
	public double getCorrelationMedian(float[] values2) {
		if(values.length != values2.length) {
			throw new IllegalStateException("Length of the two arrays does not match");
		}
		double median2 = 0;
		double varianceMedian2 = 0;
		double standardDeviationMedian2 = 0;
		if(values2.length % 2 == 0) {
			median2 = (values[values.length / 2] + values[values.length / 2 - 1]) / 2;
		} else {
			median2 = values[values.length / 2];
		}
		for(double value : values2) {
			varianceMedian2 += Math.pow(value - median2, 2);
		}
		varianceMedian2 = varianceMedian2 / (values2.length - 1);
		standardDeviationMedian2 = Math.sqrt(varianceMedian2);
		for(int i = 0; i < values.length; i++) {
			correlation += (values[i] - getMedian()) * (values2[i] - median2);
		}
		return correlation / (getStandardDeviationMedian() * standardDeviationMedian2);
	}
}
