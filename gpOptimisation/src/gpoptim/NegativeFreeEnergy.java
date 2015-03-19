package gpoptim;

import linalg.IAlgebra;
import linalg.IMatrix;
import linalg.NonPosDefMatrixException;
import gp.kernels.KernelRBF;
import gp.kernels.KernelRbfARD;
import gp.regression.RegressionGP;
import optim.ObjectiveFunction;

public class NegativeFreeEnergy implements ObjectiveFunction {

	private final RegressionGP gp;
	private final IMatrix mu;
	private final IAlgebra algebra;

	public NegativeFreeEnergy(IAlgebra algebra, double[] mu, RegressionGP gp) {
		this.algebra = algebra;
		this.gp = gp;
		this.mu = algebra.createMatrix(mu);
	}

	final static public double[][] vector2symmetricMatrix(double[] vector,
			int dim) {
		final double[][] matrix = new double[dim][dim];
		int k = 0;
		for (int i = 0; i < dim; i++)
			matrix[i][i] = vector[k++];
		for (int i = 0; i < dim; i++)
			for (int j = i + 1; j < dim; j++)
				matrix[i][j] = matrix[j][i] = vector[k++];
		return matrix;
	}

	@Override
	public double getValueAt(double... covariances) {
		final int dim = gp.getTrainingSet().getDimension();
		final IMatrix cov = algebra.createMatrix(vector2symmetricMatrix(
				covariances, dim));

		final double entropy = entropy(cov);
		final double crossEntropy = crossEntropy(cov);
		final double kl = crossEntropy - entropy;
		final double fit = -kl;
		return fit;
	}

	final static private double twoPiE = 2 * Math.PI * Math.E;

	final private double entropy(IMatrix cov) {
		return 0.5 * Math.log(Math.pow(twoPiE, cov.getRows())
				* algebra.determinant(cov));
	}

	final private double crossEntropy(IMatrix bigSigma) {
		final int dim = mu.getLength();
		final double a2;
		final double[] l2vec = new double[dim];

		final double[] hyperparams = gp.getKernel().getHypeparameters();
		if (gp.getKernel() instanceof KernelRBF) {
			a2 = hyperparams[0] * hyperparams[0];
			for (int i = 0; i < dim; i++)
				l2vec[i] = hyperparams[1] * hyperparams[1];
		} else if (gp.getKernel() instanceof KernelRbfARD) {
			a2 = hyperparams[0] * hyperparams[0];
			for (int i = 0; i < dim; i++)
				l2vec[i] = hyperparams[1 + i] * hyperparams[1 + i];
		} else
			throw new IllegalStateException();

		final IMatrix bigLambda = algebra.createDiag(l2vec);
		final IMatrix bigSL = bigSigma.add(bigLambda);

		final double detLambda = algebra.determinant(bigLambda);
		final double detSigmaLambda = algebra.determinant(bigSL);

		final IMatrix invSigmaLambda = algebra.invert(bigSL);
		final IMatrix invSigmaLambdaMu = invSigmaLambda.mmul(mu);

		final double exp1 = Math.exp(-0.5 * invSigmaLambdaMu.dot(mu));

		final int n = gp.getTrainingSet().getSize();
		double[] z;
		try {
			z = gp.getAux();
		} catch (NonPosDefMatrixException e) {
			throw new IllegalStateException(e);
		}
		IMatrix invSigmaLambdaMu_i;
		double sum = 0;
		for (int i = 0; i < n; i++) {
			final IMatrix mu_i = algebra.createMatrix(gp.getTrainingSet()
					.getInstance(i));
			final double dot1 = invSigmaLambdaMu.dot(mu_i);
			invSigmaLambdaMu_i = invSigmaLambda.mmul(mu_i);
			final double dot2 = invSigmaLambdaMu_i.dot(mu_i);
			sum += z[i] * Math.exp(dot1 - 0.5 * dot2);
		}

		return -a2 * Math.sqrt(detLambda / detSigmaLambda) * exp1 * sum;
	}

}
