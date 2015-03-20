package gpoptim;

import linalg.IAlgebra;
import linalg.JblasAlgebra;
import linalg.NonPosDefMatrixException;
import optim.LocalOptimisation;
import optim.ObjectiveFunction;
import optim.PointValue;
import optim.methods.PowellMethodApache;
import gp.GpDataset;
import gp.GpPosterior;
import gp.HyperparamLogLikelihood;
import gp.kernels.KernelFunction;
import gp.regression.RegressionGP;
import gpoptim.tranformations.EmptyTransformer;
import gpoptim.tranformations.LogTransformer;
import gpoptim.tranformations.Transformer;

public class GPOptimisation {

	private GpoOptions options = new GpoOptions();
	private RegressionGP gp;
	private double noiseTermUsed = 1;
	private Transformer pointTransformer = null;
	final private IAlgebra algebra;

	public GPOptimisation() {
		this(new JblasAlgebra());
	}

	public GPOptimisation(IAlgebra algebra) {
		this.algebra = algebra;
	}

	public GpoOptions getOptions() {
		return options;
	}

	public void setOptions(GpoOptions options) {
		this.options = options;
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

		final KernelFunction kernel = options.getKernelGP();
		gp = new RegressionGP(algebra, kernel);
		gp.setPreferInversion(true);

		if (options.getLogspace())
			pointTransformer = new LogTransformer();
		else
			pointTransformer = new EmptyTransformer();
		lbounds = pointTransformer.applyTransformation(lbounds);
		ubounds = pointTransformer.applyTransformation(ubounds);

		final GpoResult result = new GpoResult();
		final int n = options.getInitialObservtions();
		this.initialiseGP(n, lbounds, ubounds, objFunction);
		if (options.getHyperparamOptimisation()) {
			final long t0 = System.currentTimeMillis();
			optimiseGPHyperParameters(options);
			final long t1 = System.currentTimeMillis();
			result.setHyperparamOptimTimeElapsed((t1 - t0) / 1000.0);
		} else if (options.useDefaultHyperparams()) {
			final GpDataset dataset = gp.getTrainingSet();
			final double[] hyp = kernel.getDefaultHyperarameters(dataset);
			kernel.setHyperarameters(hyp);
		}
		result.setHyperparamsUsed(kernel.getHypeparameters());

		final int m = options.getGridSampleNumber();
		final int dim = lbounds.length;
		final GpDataset testSet = new GpDataset(dim, m);

		boolean notCoverged = true;
		int iteration = 0;
		int addedPointsNoImprovement = 0;
		int failedAttempts = 0;
		int evaluations = 0;

		resetProgress("\nGP Optimisation...");
		final long t0 = System.currentTimeMillis();
		while (notCoverged) {
			if (iteration++ > options.getMaxIterations()) {
				notCoverged = false;
				result.setTerminationCause("Maximum iterations");
			} else if (addedPointsNoImprovement > options
					.getMaxAddedPointsNoImprovement()) {
				notCoverged = false;
				result.setTerminationCause("No improvement observed");
			} else if (failedAttempts > options.getMaxFailedAttempts()) {
				notCoverged = false;
				result.setTerminationCause("No more potential maxima were found");
			}

			final double beta = options.getBeta()
					* (1 + 0.1 * Math.log(iteration));

			double[][] gridVals = options.getGridSampler().sample(m, lbounds,
					ubounds);
			testSet.set(gridVals);
			GpPosterior gpPost;
			try {
				gpPost = gp.getGpPosterior(testSet);
			} catch (NonPosDefMatrixException e) {
				throw new IllegalStateException(e);
			}
			double[] decision = gpPost.getUpperBound(beta);
			double[] observations = gp.getTrainingSet().getTargets();

			int maxDecisionIndex = maxarg(decision);
			double maxObservation = max(observations);
			double maxDecision = decision[maxDecisionIndex];

			double[] candidate = new double[dim];
			System.arraycopy(gridVals[maxDecisionIndex], 0, candidate, 0, dim);
			maxDecision = optimiseCandidate(candidate, lbounds, ubounds, beta);

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
			printProgress();
		}
		resetProgress("\n");

		final long t1 = System.currentTimeMillis();
		result.setGpOptimTimeElapsed((t1 - t0) / 1000.0);
		result.setIterations(iteration);
		result.setEvaluations(evaluations);
		result.setPointsExplored(gp.getTrainingSet());

		final int bestIndex = maxarg(gp.getTrainingSet().getTargets());
		double[] point = gp.getTrainingSet().getInstance(bestIndex);
		double fitness = gp.getTrainingSet().getTargets()[bestIndex];

		// fitness = optimiseCandidate(point, lbounds, ubounds, 0);
		point = pointTransformer.invertTransformation(point);

		final double[][] cov = estimateCovariance(point, gp);
		result.setSolution(point);
		result.setCovariance(cov);
		result.setFitness(fitness);
		return result;
	}

	private void initialiseGP(int n, double[] lbounds, double[] ubounds,
			NoisyObjectiveFunction objFunction) {
		double[][] inputVals = options.getInitialSampler().sample(n, lbounds,
				ubounds);
		final double[] observations = new double[n];
		final double[] noise = new double[n];

		resetProgress("\nInitial Evaluations...");
		for (int i = 0; i < n; i++) {
			final double[] point;
			point = pointTransformer.invertTransformation(inputVals[i]);
			observations[i] = objFunction.getValueAt(point);
			if (options.isHeteroskedastic())
				noise[i] = objFunction.getVarianceAt(point);
			printProgress();
		}
		resetProgress("\n");

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
		point = pointTransformer.invertTransformation(point);
		final double observation = objFunction.getValueAt(point);
		final double noise;
		if (options.isHeteroskedastic())
			noise = objFunction.getVarianceAt(point);
		else if (options.getUseNoiseTermRatio())
			noise = noiseTermUsed;
		else
			noise = options.getNoiseTerm();
		point = pointTransformer.applyTransformation(point);
		gp.getTrainingSet().add(point, observation, noise);
		return observation;
	}

	private double optimiseCandidate(double[] candidate, double[] lbounds,
			double[] ubounds, double beta) {
		GPPosteriorQuantileFitness f = new GPPosteriorQuantileFitness(gp, beta);
		PointValue optimal = options.getLocalOptimiser().optimise(f, candidate);
		double[] optimalPoint = optimal.getPoint();
		double optimalValue = optimal.getValue();

		// If optimal is out of the specified search bounds, then discard.
		// (important, as a solution out-of-bounds could be nonsensical)
		boolean outOfBounds = false;
		for (int i = 0; i < lbounds.length; i++) {
			if (optimalPoint[i] < lbounds[i]) {
				optimalPoint[i] = lbounds[i];
				outOfBounds = true;
			} else if (optimalPoint[i] > ubounds[i]) {
				optimalPoint[i] = ubounds[i];
				outOfBounds = true;
			}
		}
		if (outOfBounds) {
			final double initialValue = f.getValueAt(candidate);
			optimalValue = f.getValueAt(optimalPoint);
			if (initialValue > optimalValue) {
				optimalPoint = candidate;
				optimalValue = initialValue;
			}
		}

		System.arraycopy(optimalPoint, 0, candidate, 0, candidate.length);
		return optimalValue;

	}

	private void optimiseGPHyperParameters(GpoOptions options) {
		final boolean logspace = true;
		HyperparamLogLikelihood func = new HyperparamLogLikelihood(gp, logspace);
		GpDataset train = gp.getTrainingSet();
		final double init[] = gp.getKernel().getDefaultHyperarameters(train);

		resetProgress("\nHyperparameter optimisation...");
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
			printProgress();
		}
		resetProgress("\n");

		final double[] point = best.getPoint();
		if (logspace)
			for (int i = 0; i < point.length; i++)
				point[i] = Math.exp(best.getPoint()[i]);
		gp.getKernel().setHyperarameters(point);
	}

	/**
	 * Estimates the covariance structure at a given point, via a variational
	 * approach; it exploits the functional form of GP regression posterior
	 * using a RBF kernel.
	 */
	private double[][] estimateCovariance(double[] point, RegressionGP gp) {
		final int dim = gp.getTrainingSet().getDimension();

		// init is in upper triangular form
		final int half = ((dim * dim - dim) / 2) + dim;
		final double[] init = new double[half];
		for (int i = 0; i < dim; i++)
			init[i] = 0.1;

		NegativeFreeEnergy func = new NegativeFreeEnergy(algebra, point, gp);
		final LocalOptimisation alg = options.getLocalOptimiser();
		final double[] vectorForm = alg.optimise(func, init).getPoint();
		return NegativeFreeEnergy.vector2symmetricMatrix(vectorForm, dim);
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

	private int dots = 0;

	private void resetProgress(String msg) {
		System.out.print(msg);
		dots = msg.length();
	}

	private void printProgress() {
		System.out.print('.');
		if (++dots == 79) {
			System.out.println();
			dots = 0;
		}
	}

}
