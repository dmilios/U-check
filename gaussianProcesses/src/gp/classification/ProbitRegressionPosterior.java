package gp.classification;

import gp.GpDataset;
import gp.GpPosterior;

public final class ProbitRegressionPosterior extends GpPosterior {

	final private double[] probabilities;
	final private double[] cached_denominator;

	public ProbitRegressionPosterior(GpDataset inputData, double[] mean,
			double[] var) {
		super(inputData, mean, var);

		cached_denominator = new double[var.length];
		for (int i = 0; i < cached_denominator.length; i++)
			cached_denominator[i] = 1 / Math.sqrt(1 + var[i]);

		probabilities = new double[mean.length];
		for (int i = 0; i < probabilities.length; i++)
			probabilities[i] = standardNormalCDF(mean[i]
					* cached_denominator[i]);
	}

	public double[] getClassProbabilities() {
		return probabilities;
	}

	@Override
	public double[] getLowerBound(double beta) {
		final double[] bounds = new double[probabilities.length];
		for (int i = 0; i < bounds.length; i++)
			bounds[i] = standardNormalCDF((getMean()[i] - beta
					* getStandardDeviation()[i])
					* cached_denominator[i]);
		return bounds;
	}

	@Override
	public double[] getUpperBound(double beta) {
		final double[] bounds = new double[probabilities.length];
		for (int i = 0; i < bounds.length; i++)
			bounds[i] = standardNormalCDF((getMean()[i] + beta
					* getStandardDeviation()[i])
					* cached_denominator[i]);
		return bounds;
	}

	final private static double invSqrt2 = 1 / Math.sqrt(2);

	static final double standardNormalCDF(double x) {
		return 0.5 + 0.5 * GPEP.erf(x * invSqrt2);
	}

}
