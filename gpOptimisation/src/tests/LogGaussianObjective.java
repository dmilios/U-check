package tests;

import gp.kernels.*;
import gpoptim.GPOptimisation;
import gpoptim.GpoResult;
import gridSampling.LatinHypercubeSampler;
import linalg.IAlgebra;
import linalg.IMatrix;
import linalg.JblasAlgebra;
import optim.ObjectiveFunction;

class LogGaussianObjective implements ObjectiveFunction {

	final private double[] mean;
	final private double[] variances;
	final static private IAlgebra algebra = new JblasAlgebra();

	public LogGaussianObjective(double[] mean, double[] variaces) {
		if (mean.length != variaces.length)
			throw new IllegalArgumentException("mean.length != variaces.length");
		this.mean = mean;
		this.variances = variaces;
	}

	@Override
	public double getValueAt(double... point) {
		final int n = mean.length;
		double logdet = 0;
		for (double v : variances)
			logdet += Math.log(v);
		final IMatrix Sigma = algebra.createDiag(variances);
		final IMatrix invSigma = algebra.invert(Sigma);
		final IMatrix x = algebra.createMatrix(point);
		final IMatrix m = algebra.createMatrix(mean);
		final IMatrix x_m = x.sub(m);
		final double exponent = -0.5 * x_m.transpose().mmul(invSigma).dot(x_m);
		return -n / 2 * Math.log(2 * Math.PI) - (logdet / 2) + exponent;
	}

	public static void main(String[] args) {

		// === Definition of the objective function
		final double[] m = new double[] { 1, 2 };
		final double[] var = new double[] { 3, 2 };
		final LogGaussianObjective function = new LogGaussianObjective(m, var);

		// === Definition of the search bounds
		final double[] lb = new double[] { -30, -10 };
		final double[] ub = new double[] { 30, 10 };

		//
		// === GPO options
		final GPOptimisation gpo = new GPOptimisation();
		gpo.getOptions().setInitialSampler(new LatinHypercubeSampler(4));
		gpo.getOptions().setInitialObservtions(32);
		gpo.getOptions().setGridSampleNumber(50);
		gpo.getOptions().setLogspace(false);

		gpo.getOptions().setMaxIterations(200);
		gpo.getOptions().setMaxFailedAttempts(100);

		gpo.getOptions().setNoiseTermRatio(0.1);
		gpo.getOptions().setUseNoiseTermRatio(true);
		gpo.getOptions().setHyperparamOptimisation(true);
		gpo.getOptions().setKernelGP(new KernelRBF());

		// example of another kernel
		// KernelFunction kernel = new KernelRbfARD(new double[] { 1, 1, 1 });
		// gpo.getOptions().setKernelGP(kernel);

		//
		// === GP Optimisation
		System.out.println(gpo.getOptions());
		final GpoResult result = gpo.optimise(function, lb, ub);
		System.out.println(result);
	}
}
