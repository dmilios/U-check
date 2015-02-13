package deprecated;

import java.util.Arrays;

import gp.AbstractGP;
import gp.GpDataset;
import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;
import gp.regression.RegressionPosterior;

public class RegressionGP extends AbstractGP<RegressionPosterior> {

	final protected LinAlgebraGP algebra = new LinAlgJBLAS();

	private double sigma2 = 1;
	private boolean heteroskedastic = false;
	private GpDataset trainingSet = new GpDataset(1);

	public RegressionGP(KernelFunction kernel) {
		super(kernel);
	}

	final private void setupPriorProcess() {
		final int n = trainingSet.getSize();
		final double[] noiseTerms;
		if (!heteroskedastic) {
			noiseTerms = new double[n];
			Arrays.fill(noiseTerms, sigma2);
		} else
			noiseTerms = trainingSet.getNoiseTerms();

		final double[][] nnK = trainingSet.calculateCovariances(getKernel());
		algebra.set(nnK, noiseTerms, trainingSet.getZeroMeanTargets());
	}

	@Override
	public RegressionPosterior getGpPosterior(GpDataset testSet) {
		if (trainingSet.getDimension() != testSet.getDimension())
			throw new IllegalArgumentException(
					"The training and test sets must have the same dimension!");

		setupPriorProcess();
		final double[] mmK = testSet.calculateVariances(getKernel());
		final double[][] mnK = testSet.calculateCovariances(getKernel(),
				trainingSet);
		algebra.set(mmK, mnK);

		final double[] mean = algebra.calculatePosteriorMean();
		final double[] var = algebra.calculatePosteriorVariance();

		final double meanSubtracted = trainingSet.getTargetMean();
		for (int i = 0; i < mean.length; i++)
			mean[i] += meanSubtracted;

		return new RegressionPosterior(testSet, mean, var);
	}

	@Override
	final public double getMarginalLikelihood() {
		setupPriorProcess();
		return algebra.calculateMarginalLikelihood();
	}

	public void setSigma2(double sigma2) {
		this.sigma2 = sigma2;
	}

	public void setHeteroskedastic(boolean heteroskedastic) {
		this.heteroskedastic = heteroskedastic;
	}

	public GpDataset getTrainingSet() {
		return trainingSet;
	}

	public void setTrainingSet(GpDataset trainingSet) {
		this.trainingSet = trainingSet;
	}

	@Deprecated
	public static void main(String[] args) {

		final int n = 1400;
		final int m = 1400;

		final double a = -2;
		final double b = 9;
		final double step_n = (b - a) / n;
		final double step_m = (b - a) / m;
		double current;

		current = a;
		double[][] X = new double[n][1];
		for (int i = 0; i < n; i++)
			X[i][0] = current += step_n;
		double[] Y = new double[n];
		for (int i = 0; i < n; i++)
			Y[i] = Math.sin(X[i][0]) + 2;

		current = a;
		double[][] Xt = new double[m][1];
		for (int i = 0; i < m; i++)
			Xt[i][0] = current += step_m;

		GpDataset xy = new GpDataset(1, 100);
		xy.set(X, Y);
		GpDataset xt = new GpDataset(1);
		xt.set(Xt);

		RegressionGP gp = new RegressionGP(new KernelRBF());
		gp.setTrainingSet(xy);
		long t0 = System.currentTimeMillis();
		final double lik = gp.getMarginalLikelihood();
		RegressionPosterior post = gp.getGpPosterior(xt);
		double elapsed = (System.currentTimeMillis() - t0) / 1000d;

		final double[] mu = post.getMean();
		final double[] lower = post.getLowerBound(2);
		final double[] upper = post.getUpperBound(2);

		System.out.println("elapsed: " + elapsed + " sec");
		System.out.println("Log_likelihood: " + lik);
		for (int i = 0; i < Math.min(m, 4); i++) {
			System.out.print(mu[i] + "\t");
			System.out.print(lower[i] + "\t");
			System.out.print(upper[i] + "\n");
		}
	}
}
