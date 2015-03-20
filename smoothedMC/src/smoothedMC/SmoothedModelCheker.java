package smoothedMC;

import linalg.NonPosDefMatrixException;
import modelChecking.MitlModelChecker;
import optim.LocalOptimisation;
import optim.PointValue;
import optim.methods.PowellMethodApache;
import gp.GpDataset;
import gp.HyperparamLogLikelihood;
import gp.classification.ProbitRegressionPosterior;
import gp.classification.GPEP;
import gp.kernels.KernelRbfARD;
import smoothedMC.Parameter;
import smoothedMC.SmmcOptions;
import smoothedMC.gridSampling.GridSampler;

public class SmoothedModelCheker {

	private double hyperparamOptimTimeElapsed;
	private double statisticalMCTimeElapsed;
	private double smoothedMCTimeElapsed;
	private double[] hyperparamsUsed;

	public double getHyperparamOptimTimeElapsed() {
		return hyperparamOptimTimeElapsed;
	}

	public double getStatisticalMCTimeElapsed() {
		return statisticalMCTimeElapsed;
	}

	public double getSmoothedMCTimeElapsed() {
		return smoothedMCTimeElapsed;
	}

	public double[] getHyperparamsUsed() {
		return hyperparamsUsed;
	}

	public ProbitRegressionPosterior performSmoothedModelChecking(
			String modelFile, String mitlFile, Parameter[] params,
			SmmcOptions options) {

		throw new IllegalAccessError("Implement this! "
				+ " It is part of the old interface, but useful!");
	}

	public ProbitRegressionPosterior performSmoothedModelChecking(
			MitlModelChecker modelChecker, Parameter[] parameters,
			SmmcOptions options) throws NonPosDefMatrixException {
		long t0;
		double elapsed;
		t0 = System.currentTimeMillis();
		final GpDataset data = performStatisticalModelChecking(modelChecker,
				parameters, options);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		statisticalMCTimeElapsed = elapsed;
		if (options.isDebugEnabled())
			System.out.println("Statistical Model Checking:  " + elapsed
					+ " sec");

		return performSmoothedModelChecking(data, parameters, options);
	}

	public ProbitRegressionPosterior performSmoothedModelChecking(
			GpDataset data, Parameter[] parameters, SmmcOptions options)
			throws NonPosDefMatrixException {
		final AnalyticApproximation approx = getAnalyticApproximation(data,
				parameters, options);
		return performSmoothedModelChecking(approx, parameters, options);
	}

	public ProbitRegressionPosterior performSmoothedModelChecking(
			AnalyticApproximation approx, Parameter[] parameters,
			SmmcOptions options) {
		double[][] paramValueSet = options.getTestpoints();
		if (paramValueSet == null)
			paramValueSet = options.getSampler().sample(
					options.getNumberOfTestPoints(), parameters);
		return approx.getValuesAt(paramValueSet);
	}

	public AnalyticApproximation getAnalyticApproximation(
			MitlModelChecker modelChecker, Parameter[] parameters,
			SmmcOptions options) throws NonPosDefMatrixException {
		long t0;
		double elapsed;
		t0 = System.currentTimeMillis();
		final GpDataset data = performStatisticalModelChecking(modelChecker,
				parameters, options);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		statisticalMCTimeElapsed = elapsed;
		if (options.isDebugEnabled())
			System.out.println("Statistical Model Checking:  " + elapsed
					+ " sec");

		return getAnalyticApproximation(data, parameters, options);
	}

	public AnalyticApproximation getAnalyticApproximation(GpDataset data,
			Parameter[] parameters, SmmcOptions options)
			throws NonPosDefMatrixException {
		GPEP gp = new GPEP(options.getKernelGP());
		gp.setTrainingSet(data);
		gp.setScale(options.getSimulationRuns());
		gp.setCovarianceCorrection(options.getCovarianceCorrection());

		long t0;
		double elapsed;
		if (options.getHyperparamOptimisation()) {
			t0 = System.currentTimeMillis();
			optimiseGPHyperParameters(gp, options);
			elapsed = (System.currentTimeMillis() - t0) / 1000d;
			hyperparamOptimTimeElapsed = elapsed;
			if (options.isDebugEnabled())
				System.out.println("Hyperparameter optimisation: " + elapsed
						+ " sec");
		}

		else if (options.useDefaultHyperparams()) {
			final double[] hyp = gp.getKernel().getDefaultHyperarameters(
					gp.getTrainingSet());
			gp.getKernel().setHyperarameters(hyp);
		}

		hyperparamsUsed = options.getKernelGP().getHypeparameters();
		if (options.isDebugEnabled()) {
			System.out.println("amplitude:   "
					+ options.getKernelGP().getHypeparameters()[0]);
			System.out.println("lengthscale: "
					+ options.getKernelGP().getHypeparameters()[1]);
			if (options.getKernelGP() instanceof KernelRbfARD) {
				int dim = options.getKernelGP().getHypeparameters().length - 1;
				for (int i = 1; i < dim; i++)
					System.out.println("lengthscale: "
							+ options.getKernelGP().getHypeparameters()[i + 1]);
			}
		}

		t0 = System.currentTimeMillis();
		gp.doTraining();
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		smoothedMCTimeElapsed = elapsed;
		if (options.isDebugEnabled()) {
			System.out.println("Smoothed Model Checking:     " + elapsed
					+ " sec");
			final double lik = gp.getMarginalLikelihood();
			System.out.println("log-likelihood: " + lik);
		}

		return new AnalyticApproximation(gp);
	}

	public GpDataset performStatisticalModelChecking(
			MitlModelChecker modelChecker, Parameter[] parameters,
			SmmcOptions options) {

		final String[] paramNames = new String[parameters.length];
		for (int i = 0; i < paramNames.length; i++)
			paramNames[i] = parameters[i].getName();

		final GridSampler sampler = options.getSampler();
		final double[][] paramValueSet = sampler.sample(options.getN(),
				parameters);
		final int datapoints = paramValueSet.length;
		final double[] paramValueOutputs = new double[datapoints];

		final double endTime = options.getSimulationEndTime();
		final int runs = options.getSimulationRuns();
		final int timepoints = options.getSimulationTimepoints();

		for (int i = 0; i < datapoints; i++) {
			modelChecker.getModel().setParameters(paramNames, paramValueSet[i]);
			boolean[][] obs = modelChecker.performMC(endTime, runs, timepoints);
			for (int run = 0; run < runs; run++)
				if (obs[run][0])
					paramValueOutputs[i]++;
			paramValueOutputs[i] /= runs;
		}
		return new GpDataset(paramValueSet, paramValueOutputs);
	}

	private void optimiseGPHyperParameters(GPEP gp, SmmcOptions options) {
		final boolean logspace = true;
		HyperparamLogLikelihood func = new HyperparamLogLikelihood(gp, logspace);
		GpDataset train = gp.getTrainingSet();
		final double init[] = gp.getKernel().getDefaultHyperarameters(train);
		if (variance(train.getTargets()) == 0) {
			gp.getKernel().setHyperarameters(init);
			return; // don't bother optimising; they are all either '1' or '0'
		}

		if (logspace)
			for (int i = 0; i < init.length; i++)
				init[i] = Math.log(init[i]);

		LocalOptimisation alg = new PowellMethodApache();
		PointValue best = alg.optimise(func, init);
		for (int r = 0; r < options.getHyperparamOptimisationRestarts(); r++) {
			final double[] currentInit = new double[init.length];
			for (int i = 0; i < currentInit.length; i++)
				currentInit[i] = Math.random() * init[i] * 2;
			final PointValue curr = alg.optimise(func, currentInit);
			if (curr.getValue() > best.getValue())
				best = curr;
		}

		final double[] point = best.getPoint();
		if (logspace)
			for (int i = 0; i < point.length; i++)
				point[i] = Math.exp(best.getPoint()[i]);
		gp.getKernel().setHyperarameters(point);
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
		return sum / (double) (vector.length - 1);
	}

}
