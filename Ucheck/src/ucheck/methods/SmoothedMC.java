package ucheck.methods;

import modelChecking.MitlModelChecker;
import optim.LocalOptimisation;
import optim.PointValue;
import optim.methods.PowellMethodApache;
import gp.GpDataset;
import gp.HyperparamLogLikelihood;
import gp.classification.ClassificationPosterior;
import gp.classification.GPEP;
import gp.kernels.KernelRbfARD;
import smoothedMC.Parameter;
import smoothedMC.SmmcOptions;
import smoothedMC.gridSampling.GridSampler;

public class SmoothedMC {

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

	public ClassificationPosterior performSmoothedModelChecking(
			MitlModelChecker modelChecker, String formula,
			Parameter[] parameters, SmmcOptions options) {
		long t0;
		double elapsed;
		t0 = System.currentTimeMillis();
		final GpDataset data = performStatisticalModelChecking(modelChecker,
				formula, parameters, options);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		statisticalMCTimeElapsed = elapsed;
		if (options.isDebugEnabled())
			System.out.println("Statistical Model Checking:  " + elapsed
					+ " sec");

		return performSmoothedModelChecking(data, parameters, options);
	}

	public ClassificationPosterior performSmoothedModelChecking(GpDataset data,
			Parameter[] parameters, SmmcOptions options) {
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
		final double[][] paramValueSet = options.getSampler().sample(
				options.getM(), parameters);
		final GpDataset testSet = new GpDataset(paramValueSet);
		final ClassificationPosterior post = gp.getGpPosterior(testSet);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		smoothedMCTimeElapsed = elapsed;
		if (options.isDebugEnabled()) {
			System.out.println("Smoothed Model Checking:     " + elapsed
					+ " sec");
			final double lik = gp.getMarginalLikelihood();
			System.out.println("log-likelihood: " + lik);
		}
		return post;
	}

	public GpDataset performStatisticalModelChecking(
			MitlModelChecker modelChecker, String formula,
			Parameter[] parameters, SmmcOptions options) {

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

		for (int i = 0; i < datapoints; i++) {
			modelChecker.getModel().setParameters(paramNames, paramValueSet[i]);
			final boolean[][] obs = modelChecker.performMC(endTime, runs, 1000);
			for (int run = 0; run < runs; run++)
				if (obs[run][0])
					paramValueOutputs[i]++;
			paramValueOutputs[i] /= runs;
		}
		return new GpDataset(paramValueSet, paramValueOutputs);
	}

	private void optimiseGPHyperParameters(GPEP gp, SmmcOptions options) {
		HyperparamLogLikelihood func = new HyperparamLogLikelihood(gp);
		LocalOptimisation alg = new PowellMethodApache();
		final double init[] = gp.getKernel().getHypeparameters();
		PointValue best = alg.optimise(func, init);

		for (int r = 0; r < options.getHyperparamOptimisationRestarts(); r++) {
			final double[] currnetInit = new double[init.length];
			for (int i = 0; i < currnetInit.length; i++)
				currnetInit[i] = Math.random() * init[i] * 2;
			final PointValue curr = alg.optimise(func, currnetInit);
			if (curr.getValue() > best.getValue())
				best = curr;
		}
		gp.getKernel().setHyperarameters(best.getPoint());
	}

}
