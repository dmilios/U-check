package lff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import modelChecking.MitlModelChecker;
import gpoptim.NoisyObjectiveFunction;

public class LFFRobustness implements NoisyObjectiveFunction {

	private MitlModelChecker modelChecker;
	final private Parameter[] params;
	private LFFOptions options = new LFFOptions();

	private int propertyIndex = 0;
	private boolean useAvergageOfPositives = false;
	private boolean useAvergageOfNegatives = false;

	private double[] cachedPoint = new double[0];
	private double cachedVariance = 0;

	public LFFRobustness(MitlModelChecker modelChecker, Parameter[] params) {
		this.modelChecker = modelChecker;
		this.params = params;
	}

	public void setOptions(LFFOptions options) {
		this.options = options;
	}

	public void setPropertyIndex(int propertyIndex) {
		this.propertyIndex = propertyIndex;
	}

	public void useGlobalAvergage() {
		this.useAvergageOfPositives = false;
		this.useAvergageOfNegatives = false;
	}

	public void useAvergageOfPositives() {
		this.useAvergageOfPositives = true;
		this.useAvergageOfNegatives = false;
	}

	public void useAvergageOfNegatives() {
		this.useAvergageOfNegatives = true;
		this.useAvergageOfPositives = false;
	}

	@Override
	public double getValueAt(double... point) {

		for (final double p : point)
			if (p < 0)
				throw new IllegalArgumentException("Negative kinetic parameter");

		final int dim = point.length;
		String[] names = new String[dim];
		for (int i = 0; i < dim; i++)
			names[i] = params[i].getName();

		modelChecker.getModel().setParameters(names, point);
		final double tf = options.getSimulationEndTime();
		final int runs = options.getSimulationRuns();
		final int timepoints = options.getSimulationTimepoints();

		double[][] result = modelChecker.performMCRobust(tf, runs, timepoints);
		double[] rho = new double[result.length];
		for (int i = 0; i < rho.length; i++)
			rho[i] = result[i][propertyIndex];

		if (useAvergageOfPositives)
			rho = keepPositive(rho);
		if (useAvergageOfNegatives)
			rho = keepNegative(rho);

		final double average = average(rho);
		cachedPoint = point.clone();
		if (runs > 1)
			cachedVariance = varianceBootstrap(rho, 100);
		else
			cachedVariance = 1e-4;
		return average;
	}

	@Override
	public double getVarianceAt(double... point) {
		if (!Arrays.equals(cachedPoint, point))
			getValueAt(point);
		return cachedVariance;
	}

	private static double average(double[] values) {
		double mean = 0;
		for (int i = 0; i < values.length; i++)
			mean += values[i];
		if (values.length != 0)
			return mean / values.length;
		return 0;
	}

	private static double[] keepPositive(double[] values) {
		final ArrayList<Double> list = new ArrayList<Double>();
		for (final double val : values)
			if (val > 0)
				list.add(val);
		final double[] positive = new double[list.size()];
		for (int i = 0; i < positive.length; i++)
			positive[i] = list.get(i);
		return positive;
	}

	private static double[] keepNegative(double[] values) {
		final ArrayList<Double> list = new ArrayList<Double>();
		for (final double val : values)
			if (val < 0)
				list.add(val);
		final double[] negative = new double[list.size()];
		for (int i = 0; i < negative.length; i++)
			negative[i] = list.get(i);
		return negative;
	}

	final private Random rand = new Random();

	/**
	 * Returns k observations sampled uniformly at random, with replacement,
	 * from data.
	 * */
	final private double[] dataSample(double[] data, int k) {
		final int n = data.length;
		final double[] sample = new double[k];
		for (int i = 0; i < k; i++) {
			final int j = rand.nextInt(n);
			sample[i] = data[j];
		}
		return sample;
	}

	private final double varianceBootstrap(double[] values, int bootstrapSize) {
		final double[] expectations = new double[bootstrapSize];
		for (int b = 0; b < bootstrapSize; b++) {
			final double[] resampled = dataSample(values, values.length);
			expectations[b] = average(resampled);
		}
		return variance(expectations);
	}

	final static private double variance(double[] vector) {
		double mean = 0;
		for (double v : vector)
			mean += v;
		mean /= (double) vector.length;
		double sum = 0;
		for (int i = 0; i < vector.length; i++) {
			final double aux = vector[i] - mean;
			sum += aux * aux;
		}
		if (vector.length > 1)
			return sum / (double) (vector.length - 1);
		return 0;
	}

}
