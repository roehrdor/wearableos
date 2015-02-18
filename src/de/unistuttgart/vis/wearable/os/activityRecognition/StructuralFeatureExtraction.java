package de.unistuttgart.vis.wearable.os.activityRecognition;

import Jama.Matrix;
import Jama.QRDecomposition;

public class StructuralFeatureExtraction {
	
	private float[] values;
	private Matrix pMatrix;
	private int polynomDegree = 0;
	private double meanVariation = 0;
	private double[] coefficients;
	
//	public StructuralFeatureExtraction_new(double[] values, int polynomDegree) {
//		this.values = values;
//		this.polynomDegree = polynomDegree;
//		coefficients = new double[this.polynomDegree + 1];
//		compute();
//	}
	
	public StructuralFeatureExtraction(float[] values, int polynomDegree) {
		this.values = values;
		this.polynomDegree = polynomDegree;
		coefficients = new double[this.polynomDegree + 1];
		compute();
	}
	
	private void compute() {
		double[] vector = new double[values.length];
		double[][] vandermondeMatrix = new double[values.length][polynomDegree + 1];
		for(int i = 0; i < values.length; i++) {
			for(int j = 0; j < polynomDegree; j++) {
				vandermondeMatrix[i][j] = Math.pow(values[i], j);
			}
			vector[i] = i;
		}
		Matrix A = new Matrix(vandermondeMatrix);
		Matrix B = new Matrix(vector, values.length);
		
		// Least square method
		QRDecomposition qr = new QRDecomposition(A);
		if (!qr.isFullRank()) {
			throw new IllegalStateException("Matrix is rank deficient!");
		}
		pMatrix = qr.solve(B);
		
		double mean = 0;
		for(double value : vector) {
			mean += value;
		}
		mean = mean / values.length;
		double deviation = 0;
		
		// Total sum of squares
		double TSS = 0;
		for(double value : vector) {
			deviation = value - mean;
			TSS += Math.pow(deviation, 2);
		}
		
		// Error sum of squares
		double SSE = 0;
		Matrix residuals = A.times(pMatrix.minus(B));
		SSE = residuals.norm2() * residuals.norm2();
		meanVariation = 1.0 - SSE / TSS;
		for(int i = polynomDegree; i >= 0; i--) {
			coefficients[i] = pMatrix.get(i, 0);
		}
	}
	
	public double[] getCoefficients() {
		return coefficients;
	}
	
	public double getCoefficient(int i) {
		return coefficients[i];
	}
	
	public double getMeanVariation() {
		return meanVariation;
	}
}
