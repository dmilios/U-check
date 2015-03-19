package gp.regression;

import java.util.Arrays;

import linalg.IAlgebra;
import linalg.IMatrix;
import linalg.NonPosDefMatrixException;
import gp.AbstractGP;
import gp.GpDataset;
import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;

public class RegressionGP extends AbstractGP<RegressionPosterior> {

	private double sigma2 = 1;
	private boolean heteroskedastic = false;
	private boolean preferInversion = false;

	public RegressionGP(KernelFunction kernel) {
		super(kernel);
	}

	public RegressionGP(IAlgebra algebra, KernelFunction kernelFunction) {
		super(algebra, kernelFunction);
	}

	public void setSigma2(double sigma2) {
		this.sigma2 = sigma2;
	}

	public void setHeteroskedastic(boolean heteroskedastic) {
		this.heteroskedastic = heteroskedastic;
	}

	/**
	 * Set whether matrix inversion will be preferred to solving a system of
	 * equations.<br>
	 * Sometimes, matrix inversion can be preferable, as the inverted matrix can
	 * be reused.
	 */
	public void setPreferInversion(boolean preferInversion) {
		this.preferInversion = preferInversion;
	}

	private IMatrix y = null;
	private IMatrix C = null;
	private IMatrix U = null;
	private IMatrix invC_y = null;
	private IMatrix invC = null;

	final private void setupPriorProcess() throws NonPosDefMatrixException {
		final double[] noiseTerms;
		if (!heteroskedastic) {
			noiseTerms = new double[trainingSet.getSize()];
			Arrays.fill(noiseTerms, sigma2);
		} else
			noiseTerms = trainingSet.getNoiseTerms();
		final double[][] data = trainingSet.calculateCovariances(getKernel());

		final IMatrix gram = algebra.createMatrix(data);
		final IMatrix sigma2_diag = algebra.createDiag(noiseTerms);
		y = algebra.createMatrix(trainingSet.getZeroMeanTargets());
		C = gram.add(sigma2_diag);
		U = C.duplicate();
		invC_y = y.duplicate();
		algebra.solvePositiveInPlace(U, invC_y);
		invC = null;
	}

	/**
	 * The product {@code C^(-1) * y} is often reported as auxiliary in the
	 * literature.
	 */
	public double[] getAux() throws NonPosDefMatrixException {
		if (trainingSet.isModified())
			setupPriorProcess();
		return invC_y.getData();
	}

	@Override
	public RegressionPosterior getGpPosterior(GpDataset testSet)
			throws NonPosDefMatrixException {
		if (trainingSet.getDimension() != testSet.getDimension())
			throw new IllegalArgumentException(
					"The training and test sets must have the same dimension!");

		final boolean invalidCache = trainingSet.isModified();
		if (invalidCache)
			setupPriorProcess();

		final double[] dataMM = testSet.calculateVariances(getKernel());
		final double[][] dataMN = testSet.calculateCovariances(getKernel(),
				trainingSet);

		IMatrix mmK = algebra.createMatrix(dataMM);
		IMatrix mnK = algebra.createMatrix(dataMN);
		IMatrix nmK = mnK.transpose();

		final double meanSubtracted = trainingSet.getTargetMean();
		final double[] mean = mnK.mmul(invC_y).add(meanSubtracted).getData();

		if (invC == null)
			if (preferInversion || testSet.getSize() >= trainingSet.getSize())
				invC = algebra.invert(C);

		final IMatrix invC_nmK;
		if (invC == null)
			invC_nmK = algebra.solvePositive(C, nmK);
		else
			invC_nmK = invC.mmul(nmK);

		final IMatrix mnK_invC_nmK__diag = (mnK.mmul(invC_nmK)).diag();
		final IMatrix sumMatrix_diag = mmK.sub(mnK_invC_nmK__diag);
		for (int i = 0; i < sumMatrix_diag.getRows(); i++)
			if (sumMatrix_diag.get(i) < 0)
				sumMatrix_diag.put(i, 0.001);
		final double[] var = sumMatrix_diag.getData();
		return new RegressionPosterior(testSet, mean, var);
	}

	@Override
	final public double getMarginalLikelihood() throws NonPosDefMatrixException {
		setupPriorProcess();
		double sum = 0;
		final double[] diag = U.diag().getData();
		for (double val : diag)
			sum += Math.log(val);
		final double logDetC = sum + sum;
		final double term1 = -0.5 * logDetC;
		final double term2 = -0.5 * y.transpose().dot(invC_y);
		final double term3 = -y.getRows() * 0.5 * Math.log(2 * Math.PI);
		return term1 + term2 + term3;
	}

	@Override
	public double[] getMarginalLikelihoodGradient()
			throws NonPosDefMatrixException {
		setupPriorProcess();
		final int hyperparams = getKernel().getHypeparameters().length;
		final double[] gradient = new double[hyperparams];
		if (invC == null)
			invC = algebra.invert(C);
		if (invC_y == null)
			invC_y = invC.mmul(y);

		for (int h = 0; h < hyperparams; h++) {
			final double[][] data = trainingSet.calculateCovarianceDerivatives(
					getKernel(), 0);
			final IMatrix gramDeriv = algebra.createMatrix(data);
			final IMatrix arg = invC_y.mmul(invC_y.transpose()).sub(invC)
					.mmul(gramDeriv);
			double trace = 0;
			for (int i = 0; i < arg.getRows(); i++)
				trace += arg.get(i, i);
			gradient[h] = 0.5 * trace;
		}
		return gradient;
	}

	@Deprecated
	public static void main(String[] args) throws NonPosDefMatrixException {

		final int n = 1000;
		final int m = 1000;

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

		AbstractGP<RegressionPosterior> gp = new RegressionGP(new KernelRBF());
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
