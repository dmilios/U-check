package gpoptim;

import optim.LocalOptimisation;
import optim.ObjectiveFunction;
import optim.PointValue;
import gp.GpDataset;
import gp.GpPosterior;
import gp.HyperparamLogLikelihood;
import gp.regression.RegressionGP;

public class GPOptimisation {

	final private GpoOptions options = new GpoOptions();
	private RegressionGP gp;
	private double noiseTermUsed = 1;
	private LogspaceConverter logspace = null;

	public GPOptimisation() {

	}

	public GpoOptions getOptions() {
		return options;
	}

	public GpoResult optimise(ObjectiveFunction objFunction, double[] lbounds,
			double[] ubounds) {
		return optimise(
				new ConstantNoiseObjective(objFunction, options.getNoiseTerm()),
				lbounds, ubounds);
	}

	public GpoResult optimise(NoisyObjectiveFunction objFunction,
			double[] lbounds, double[] ubounds) {

		if (lbounds.length != ubounds.length)
			throw new IllegalArgumentException(
					"lbounds.length != ubounds.length");
		for (int i = 0; i < ubounds.length; i++)
			if (lbounds[i] >= ubounds[i])
				throw new IllegalArgumentException("lbound >= ubound");

		gp = new RegressionGP(options.getKernelGP());
		gp.setPreferInversion(true);

		logspace = new LogspaceConverter(lbounds, ubounds);
		if (options.getLogspace()) {
			lbounds = logspace.convertToLogspace(lbounds);
			ubounds = logspace.convertToLogspace(ubounds);
		}

		final GpoResult result = new GpoResult();
		final int n = options.getInitialObservtions();
		this.initialiseGP(n, lbounds, ubounds, objFunction);
		if (options.getHyperparamOptimisation()) {
			final long t0 = System.currentTimeMillis();
			optimiseGPHyperParameters(options);
			final long t1 = System.currentTimeMillis();
			result.setHyperparamOptimTimeElapsed((t1 - t0) / 1000.0);
		}
		result.setHyperparamsUsed(gp.getKernel().getHyperarameters());

		final int m = options.getGridSampleNumber();
		final int dim = lbounds.length;
		final GpDataset testSet = new GpDataset(dim, m);

		boolean notCoverged = true;
		int iteration = 0;
		int addedPointsNoImprovement = 0;
		int failedAttempts = 0;
		int evaluations = n;

		final long t0 = System.currentTimeMillis();
		while (notCoverged) {
			if (iteration++ > options.getMaxIterations())
				notCoverged = false;
			else if (addedPointsNoImprovement > options
					.getMaxAddedPointsNoImprovement())
				notCoverged = false;
			else if (failedAttempts > options.getMaxFailedAttempts())
				notCoverged = false;

			final double beta = options.getBeta()
					* (1 + 0.1 * Math.log(iteration));

			double[][] gridVals = options.getGridSampler().sample(m, lbounds,
					ubounds);
			testSet.set(gridVals);
			final GpPosterior gpPost = gp.getGpPosterior(testSet);
			double[] decision = gpPost.getUpperBound(beta);
			double[] observations = gp.getTrainingSet().getTargets();

			int maxDecisionIndex = maxarg(decision);
			double maxObservation = max(observations);
			double maxDecision = decision[maxDecisionIndex];

			double[] candidate = new double[dim];
			System.arraycopy(gridVals[maxDecisionIndex], 0, candidate, 0, dim);
			maxDecision = optimiseCandidate(candidate, beta);

			// found a new potential maximum
			if (maxDecision >= maxObservation) {
				failedAttempts = 0;
				evaluations++;
				final double lastObservation = this.addToGP(candidate,
						objFunction);
				if (lastObservation < maxObservation
						* options.getImprovementFactor())
					addedPointsNoImprovement++;
				else
					addedPointsNoImprovement = 0;
			} else
				failedAttempts++;
		}
		final long t1 = System.currentTimeMillis();
		result.setGpOptimTimeElapsed((t1 - t0) / 1000.0);
		result.setIterations(iteration);
		result.setEvaluations(evaluations);

		final int bestIndex = maxarg(gp.getTrainingSet().getTargets());
		double[] point = gp.getTrainingSet().getInstance(bestIndex);
		if (options.getLogspace())
			point = logspace.convertfromLogspace(point);

		result.setSolution(point);
		result.setFitness(gp.getTrainingSet().getTargets()[bestIndex]);
		return result;
	}

	private void initialiseGP(int n, double[] lbounds, double[] ubounds,
			NoisyObjectiveFunction objFunction) {
		double[][] inputVals = options.getInitialSampler().sample(n, lbounds,
				ubounds);
		final double[] observations = new double[n];
		final double[] noise = new double[n];
		for (int i = 0; i < n; i++) {
			final double[] point;
			if (options.getLogspace())
				point = logspace.convertfromLogspace(inputVals[i]);
			else
				point = inputVals[i];
			observations[i] = objFunction.getValueAt(point);
			if (options.isHeteroskedastic())
				noise[i] = objFunction.getVarianceAt(point);
		}

		if (!options.isHeteroskedastic()) {
			if (options.getUseNoiseTermRatio()) {
				final double ratio = options.getNoiseTermRatio();
				final double noiseTerm = (max(observations) - min(observations))
						* ratio;
				for (int i = 0; i < n; i++)
					noise[i] = noiseTerm;
				noiseTermUsed = noiseTerm;
			} else
				for (int i = 0; i < n; i++)
					noise[i] = options.getNoiseTerm();
		}

		GpDataset trainingSet = new GpDataset(inputVals, observations, noise);
		gp.setTrainingSet(trainingSet);
	}

	private double addToGP(double[] point, NoisyObjectiveFunction objFunction) {
		if (options.getLogspace())
			point = logspace.convertfromLogspace(point);
		final double observation = objFunction.getValueAt(point);
		final double noise;
		if (options.isHeteroskedastic())
			noise = objFunction.getVarianceAt(point);
		else if (options.getUseNoiseTermRatio())
			noise = noiseTermUsed;
		else
			noise = options.getNoiseTerm();
		gp.getTrainingSet().add(point, observation, noise);
		return observation;
	}

	protected double optimiseCandidate(double[] candidate, double beta) {
		GPPosteriorQuantileFitness f = new GPPosteriorQuantileFitness(gp, beta);
		PointValue optimal = options.getLocalOptimiser().optimise(f, candidate);
		System.arraycopy(optimal.getPoint(), 0, candidate, 0, candidate.length);
		return optimal.getValue();

	}

	private void optimiseGPHyperParameters(GpoOptions options) {
		final HyperparamLogLikelihood func = new HyperparamLogLikelihood(gp);
		final LocalOptimisation alg = options.getLocalOptimiser();
		final double init[] = gp.getKernel().getHyperarameters();
		PointValue best = alg.optimise(func, init);

		for (int r = 0; r < options.getHyperparamOptimisationRestarts(); r++) {
			final double[] currentInit = new double[init.length];
			for (int i = 0; i < currentInit.length; i++)
				currentInit[i] = Math.random() * init[i] * 2;
			final PointValue curr = alg.optimise(func, currentInit);
			if (curr.getValue() > best.getValue())
				best = curr;
		}
		gp.getKernel().setHyperarameters(best.getPoint());
	}

	static final private double max(final double[] vector) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < vector.length; i++)
			if (vector[i] > max)
				max = vector[i];
		return max;
	}

	static final private double min(final double[] vector) {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < vector.length; i++)
			if (vector[i] < min)
				min = vector[i];
		return min;
	}

	static final private int maxarg(final double[] vector) {
		int maxIndex = -1;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < vector.length; i++)
			if (vector[i] > max) {
				max = vector[i];
				maxIndex = i;
			}
		return maxIndex;
	}

}
