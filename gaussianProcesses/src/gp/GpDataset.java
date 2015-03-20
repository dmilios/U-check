package gp;

import java.util.Arrays;

import gp.kernels.KernelFunction;

final public class GpDataset {

	final private int dimension;
	private double[][] dataX;
	private double[] dataY;
	private double[] noise;
	private int size = 0;

	private boolean changedSinceGramCalculation = true;
	private Class<?> cachedKernel = null;
	private double[] cachedHyperparams = new double[0];
	private double[][] cachedGramMatrix = new double[0][];

	public GpDataset(int dimension) {
		this(dimension, 0);
	}

	public GpDataset(int dimension, int preemptedSize) {
		this.dimension = dimension;
		dataX = new double[preemptedSize][dimension];
		dataY = new double[preemptedSize];
		noise = new double[preemptedSize];
	}

	public GpDataset(final double[][] x) {
		this(x[0].length, x.length);
		set(x);
	}

	public GpDataset(final double[][] x, final double[] y) {
		this(x[0].length, x.length);
		set(x, y);
	}

	public GpDataset(final double[][] x, final double[] y, final double[] noise) {
		this(x[0].length, x.length);
		set(x, y, noise);
	}

	public final boolean isModified() {
		return changedSinceGramCalculation;
	}

	public int getSize() {
		return size;
	}

	public int getDimension() {
		return dimension;
	}

	public double[] getInstance(int i) {
		return dataX[i];
	}

	public double[] getNoiseTerms() {
		return noise;
	}

	public double getTargetMean() {
		double sum = 0;
		for (int i = 0; i < getSize(); i++)
			sum += dataY[i];
		return sum / getSize();
	}

	public double[] getTargets() {
		final double[] y = new double[getSize()];
		System.arraycopy(dataY, 0, y, 0, getSize());
		return y;
	}

	public double[] getZeroMeanTargets() {
		final double meanY = getTargetMean();
		final double[] zeroMeanY = new double[getSize()];
		System.arraycopy(dataY, 0, zeroMeanY, 0, getSize());
		for (int i = 0; i < getSize(); i++)
			zeroMeanY[i] -= meanY;
		return zeroMeanY;
	}

	private int getPreemptedSize() {
		return dataX.length;
	}

	public void set(final double[][] x) {
		size = 0;
		add(x);
	}

	public void set(final double[][] x, final double[] y) {
		size = 0;
		add(x, y);
	}

	public void set(final double[][] x, final double[] y, final double[] noise) {
		size = 0;
		add(x, y, noise);
	}

	public void add(final double[][] x2) {
		add(x2, new double[x2.length]);
	}

	public void add(final double[][] x2, final double[] y2) {
		add(x2, y2, new double[x2.length]);
	}

	public void add(final double[][] x2, final double[] y2,
			final double[] noise2) {
		if (x2.length != y2.length || x2.length != noise2.length)
			throw new IllegalArgumentException();

		final int size1 = getSize();
		final int size2 = x2.length;
		final int size12 = size1 + size2;
		if (size12 > getPreemptedSize()) {
			final double[][] x12 = new double[size12][dimension];
			final double[] y12 = new double[size12];
			final double[] noise12 = new double[size12];
			for (int i = 0; i < size1; i++)
				System.arraycopy(dataX[i], 0, x12[i], 0, dimension);
			System.arraycopy(dataY, 0, y12, 0, size1);
			System.arraycopy(noise, 0, noise12, 0, size1);
			dataX = x12;
			dataY = y12;
			noise = noise12;
		}
		for (int i = size1; i < size12; i++) {
			System.arraycopy(x2[i - size1], 0, dataX[i], 0, dimension);
		}
		System.arraycopy(y2, 0, dataY, size1, size2);
		System.arraycopy(noise2, 0, noise, size1, size2);
		size = size12;
		changedSinceGramCalculation = true;
	}

	public void set(final double[] x) {
		size = 0;
		add(x);
	}

	public void set(final double[] x, final double y) {
		size = 0;
		add(x, y);
	}

	public void set(final double[] x, final double y, final double noise) {
		size = 0;
		add(x, y, noise);
	}

	public void add(final double[] x2) {
		add(x2, 0);
	}

	public void add(final double[] x2, final double y2) {
		add(x2, y2, 0);
	}

	public void add(final double[] x2, final double y2, final double noise2) {
		final int size1 = getSize();
		final int size2 = 1;
		final int size12 = size1 + size2;
		if (size12 > getPreemptedSize()) {
			final double[][] x12 = new double[size12][dimension];
			final double[] y12 = new double[size12];
			final double[] noise12 = new double[size12];
			for (int i = 0; i < size1; i++)
				System.arraycopy(dataX[i], 0, x12[i], 0, dimension);
			System.arraycopy(dataY, 0, y12, 0, size1);
			System.arraycopy(noise, 0, noise12, 0, size1);
			dataX = x12;
			dataY = y12;
			noise = noise12;
		}
		System.arraycopy(x2, 0, dataX[size12 - 1], 0, dimension);
		dataY[size12 - 1] = y2;
		noise[size12 - 1] = noise2;
		size = size12;
		changedSinceGramCalculation = true;
	}

	private double[][] allocateGramMatrix(int n) {
		if (cachedGramMatrix != null && cachedGramMatrix.length == n
				&& cachedGramMatrix[0].length == n)
			return cachedGramMatrix;
		else
			return new double[n][n];
	}

	private boolean canUsedCachedResults(final KernelFunction func) {
		return cachedKernel == func.getClass()
				&& Arrays.equals(cachedHyperparams, func.getHypeparameters());
	}

	public final double[] calculateVariances(final KernelFunction func) {
		final int n = this.getSize();
		final double[][] x = this.dataX;
		final double[] vars = new double[n];
		for (int i = 0; i < n; i++)
			vars[i] = func.calculate(x[i], x[i]);
		return vars;
	}

	public final double[][] calculateCovariances(final KernelFunction func) {

		if (canUsedCachedResults(func))
			changedSinceGramCalculation = false;
		else
			changedSinceGramCalculation = true;

		final double[][] x = this.dataX;
		final int n = this.getSize();
		final double[][] result = allocateGramMatrix(n);

		if (canUsedCachedResults(func)) {
			final int m = cachedGramMatrix.length;
			for (int i = 0; i < n; i++) {
				if (i < m)
					result[i][i] = cachedGramMatrix[i][i];
				else
					result[i][i] = func.calculate(x[i], x[i]);
				for (int j = i + 1; j < n; j++)
					if (i < m && j < m)
						result[j][i] = result[i][j] = cachedGramMatrix[i][j];
					else
						result[j][i] = result[i][j] = func
								.calculate(x[i], x[j]);
			}
		}

		else
			for (int i = 0; i < n; i++) {
				result[i][i] = func.calculate(x[i], x[i]);
				for (int j = i + 1; j < n; j++)
					result[j][i] = result[i][j] = func.calculate(x[i], x[j]);
			}

		cachedGramMatrix = result;
		cachedKernel = func.getClass();
		cachedHyperparams = func.getHypeparameters();
		cachedHyperparams = Arrays.copyOf(cachedHyperparams,
				cachedHyperparams.length);
		return result;
	}

	public final double[][] calculateCovariances(final KernelFunction func,
			GpDataset set) {
		final double[][] x1 = this.dataX;
		final double[][] x2 = set.dataX;
		final int n1 = this.getSize();
		final int n2 = set.getSize();
		final double[][] result = new double[n1][n2];
		for (int i = 0; i < n1; i++)
			for (int j = 0; j < n2; j++)
				result[i][j] = func.calculate(x1[i], x2[j]);
		return result;
	}

	public final double[][] calculateCovarianceDerivatives(
			final KernelFunction func, final int hyperparamIndex) {
		final double[][] x = this.dataX;
		final int n = this.getSize();
		final double[][] result = new double[n][n];
		for (int i = 0; i < n; i++) {
			result[i][i] = func.calculateHyperparamDerivative(x[i], x[i],
					hyperparamIndex);
			for (int j = i + 1; j < n; j++)
				result[j][i] = result[i][j] = func
						.calculateHyperparamDerivative(x[i], x[j],
								hyperparamIndex);
		}
		return result;
	}

	public final String toCSV() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < getSize(); i++) {
			for (int d = 0; d < getDimension(); d++)
				str.append(dataX[i][d] + ",\t");
			str.append(dataY[i] + "\n");
		}
		return str.toString();
	}

}
