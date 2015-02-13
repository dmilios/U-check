package gp.regression;

import linalg.IAlgebra;
import linalg.IMatrix;
import gp.AbstractGP;
import gp.GpDataset;
import gp.kernels.KernelFunction;

public class ApproximateGPR extends AbstractGP<RegressionPosterior> {

	private GpDataset inducingData;
	private double sigma2 = 1;

	public ApproximateGPR(KernelFunction kernelFunction) {
		super(kernelFunction);
	}

	public ApproximateGPR(IAlgebra algebra, KernelFunction kernelFunction) {
		super(algebra, kernelFunction);
	}

	public void setInducingData(GpDataset inducingData) {
		this.inducingData = inducingData;
	}

	public void setSigma2(double sigma2) {
		this.sigma2 = sigma2;
	}

	private IMatrix y = null;
	private IMatrix Kuu = null;
	private IMatrix Kun = null;

	final private void setupPriorProcess() {
		
		if (inducingData == null) {
			// final int n = getTrainingSet().getSize();
			
			// TODO: randomly choose an inducing set here!
		}
		
		
		final double[][] data = inducingData.calculateCovariances(getKernel());
		final double[][] dataUN = inducingData.calculateCovariances(
				getKernel(), trainingSet);
		Kuu = algebra.createMatrix(data);
		Kun = algebra.createMatrix(dataUN);
	}

	@Override
	public RegressionPosterior getGpPosterior(GpDataset testSet) {
		setupPriorProcess();

		final double[][] dataUM = inducingData.calculateCovariances(
				getKernel(), testSet);
		final IMatrix Kum = algebra.createMatrix(dataUM);

		final IMatrix terms = Kun.mmul(Kun.transpose()).mul(1.0 / sigma2)
				.add(Kuu);
		final IMatrix sigmaMatrix = algebra.invertPositive(terms);
		final IMatrix Kmu_Sigma = Kum.transpose().mmul(sigmaMatrix);

		final double[] mu = Kmu_Sigma.mmul(Kun).mmul(y).mul(1.0 / sigma2)
				.getData();
		final double[] var = Kmu_Sigma.mmul(Kum).diag().getData();

		return new RegressionPosterior(testSet, mu, var);
	}

	final private static double log2pi_x_05 = 0.5 * Math.log(2 * Math.PI);

	@Override
	public double getMarginalLikelihood() {
		setupPriorProcess();
		final IMatrix invKuu = algebra.invertPositive(Kuu);
		final IMatrix Qnn = Kun.transpose().mmul(invKuu).mmul(Kun);

		final IMatrix lambda = algebra.createEye(y.getLength()).mul(sigma2);
		final IMatrix QnnLambda = Qnn.add(lambda);

		final IMatrix invQnnLambda_y = y.duplicate();
		algebra.solvePositiveInPlace(QnnLambda, invQnnLambda_y);
		final double[] diag = QnnLambda.diag().getData();
		double logDet = 0;
		for (final double val : diag)
			logDet += Math.log(val);
		logDet += logDet;

		final double term1 = -0.5 * logDet;
		final double term2 = -0.5 * y.transpose().dot(invQnnLambda_y);
		final double term3 = -y.getLength() * log2pi_x_05;
		return term1 + term2 + term3;
	}

}
