package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.Vector;

import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import Jama.Matrix;
import Jama.QRDecomposition;
import android.util.Log;

/**
 * Calculates a equation polynomial of a give time series/virtual sensor
 * 
 * @author Tobias
 *
 */
public class StructuralFeatureExtraction_old {
	private Vector<SensorData> sensorData;
	private Matrix[] pMatrix;

	// degree of the polynomial
	private int degree = 3;

	// mean variation
	private double[] R2;

	private double[][] coefficient;
	private int sensorDimension;

	@SuppressWarnings("deprecation")
	public StructuralFeatureExtraction_old(PSensor pSensor) {
		sensorDimension = 0;
		sensorDimension = pSensor.getSensorType().getDimension();
		coefficient = new double[sensorDimension][degree + 1];
		pMatrix = new Matrix[sensorDimension];
		R2 = new double[sensorDimension];
		for (int dimension = 0; dimension < sensorDimension; dimension++) {
			try {
				sensorData = pSensor.getRawData();
			} catch (Exception e) {
				Log.e("har",
						"[StructuralFeatureExtraction] exception catched: "
								+ e.toString());
			}
			computeFeatures(dimension);
		}
	}

	private void computeFeatures(int dimension) {
		polynomialFit(dimension);
	}

	/**
	 * TODO
	 */
	public void recomputeFeatures() {
		computeFeatures(0);
	}

	private void polynomialFit(int dimension) {
		int n = sensorData.size();
		double vector[] = new double[sensorData.size()];
		double[][] vandermondeMatrix = new double[n][degree + 1];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j <= degree; j++) {
				vandermondeMatrix[i][j] = Math.pow(
						sensorData.get(i).getData()[dimension], j);
			}
			vector[i] = i;
		}
		Matrix A = new Matrix(vandermondeMatrix);
		Matrix B = new Matrix(vector, n);

		// Least square method
		QRDecomposition qr = new QRDecomposition(A);
		if(!qr.isFullRank()) {
			throw new IllegalStateException("Matrix is rank deficient!");
		}
		pMatrix[dimension] = qr.solve(B);

		double mean = 0.0;
		for (int i = 0; i < n; i++) {
			mean += vector[i];
		}
		mean = mean / n;
		double deviation;
		// total sum of squares
		double TSS = 0;
		for (int i = 0; i < n; i++) {
			deviation = vector[i] - mean;
			TSS += Math.pow(deviation, 2);
		}
		// error sum of squares
		double SSE = 0;
		Matrix residuals = A.times(pMatrix[dimension]).minus(B);
		SSE = residuals.norm2() * residuals.norm2();
		R2[dimension] = 1.0 - SSE / TSS;
		for (int i = degree; i >= 0; i--) {
			coefficient[dimension][i] = pMatrix[dimension].get(i, 0);
		}
	}

	/**
	 * 
	 * @return Residual standard error
	 */
	public double[] getR2() {
		return R2;
	}

	/**
	 * 
	 * @param i
	 * @return Coefficient at position i
	 */
	public double[][] getCoefficient() {
		return coefficient;
	}
}
