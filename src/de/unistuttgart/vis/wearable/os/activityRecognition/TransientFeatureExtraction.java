package de.unistuttgart.vis.wearable.os.activityRecognition;

public class TransientFeatureExtraction {

	private float[] values;
	private double trendArea = 0;
	private double subsetArea = 0;
	
//	/**
//	 * Use to calculate transient features with standard trend area and subset area.
//	 * @param values
//	 * 			to calculate the features
//	 */
//	public TransientFeatureExtraction_new(double[] values) {
//		this.values = values;
//		this.trendArea = 0.1;
//		this.subsetArea = 0.1;
//	}
//	
//	/**
//	 * Use to calculate transient features with custom trend area and subset area.
//	 * @param values
//	 * 			to calculate the features
//	 * @param trendArea
//	 * 			standard 0.1
//	 * @param subsetArea
//	 * 			standard 0.1
//	 */
//	public TransientFeatureExtraction_new(double[] values, double trendArea, double subsetArea) {
//		this.values = values;
//		this.trendArea = trendArea;
//		this.subsetArea = subsetArea;
//	}
	
	/**
	 * Use to calculate transient features with standard trend area and subset area.
	 * @param values
	 * 			to calculate the features
	 */
	public TransientFeatureExtraction(float[] values) {
		this.values = values;
		this.trendArea = 0.1;
		this.subsetArea = 0.1;
	}
	
	/**
	 * Use to calculate transient features with custom trend area and subset area.
	 * @param values
	 * 			to calculate the features
	 * @param trendArea
	 * 			standard 0.1
	 * @param subsetArea
	 * 			standard 0.1
	 */
	public TransientFeatureExtraction(float[] values, double trendArea, double subsetArea) {
		this.values = values;
		this.trendArea = trendArea;
		this.subsetArea = subsetArea;
	}
	
	/**
	 * Uses linear regression to compute the trend of a given time series
	 */
	private double calculationLine() {
		double xMean = 0;
		double yMean = values.length / 2.0;
		for(int i = 0; i < values.length; i++) {
			xMean += values[i];
		}
		xMean = xMean / values.length;
		double a = 0, c = 0;
		for(int i = 0; i < values.length; i++) {
			c += (i - yMean)  * (i - yMean);
			a += (values[i] - xMean) * (i - yMean);
		}
		return a / c;
	}
	
	/**
	 * @return 
	 * 		1 if the line goes up 
	 * 		-1 if the line goes down  
	 * 		0 if the line is horizontal
	 */
	public int getTrend() {
		double x = calculationLine();
		if(x > trendArea) {
			return 1;
		} else if(x < trendArea) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * @return the maximum deviation between the beginning and the and of a time
	 *         series/virtual sensor
	 */
	public double getMagnitudeOfChange() {
		int end = values.length - 1;
		int subset = (int) (end * subsetArea);
		
		double minStart = values[0];
		double maxStart = minStart;
		double minEnd = values[values.length - 1];
		double maxEnd = minEnd;
		
		for (int i = 0; i <= subset; i++) {
			double currentValue = values[i];
			maxStart = Math.max(maxStart, currentValue);
			minStart = Math.min(minStart, currentValue);
		}
		
		for (int i = end; i >= end - subset; i--) {
			double currentValue = values[i];
			maxEnd = Math.max(maxEnd, currentValue);
			minEnd = Math.min(minEnd, currentValue);
		}
		
		return Math.max(Math.abs(maxEnd - minStart),
				Math.abs(maxStart - minEnd));
	}
	
	public double getSignedMagnitudeOfChange() {
		return getTrend() * getMagnitudeOfChange();
	}
}
