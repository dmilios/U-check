package gpoptim;

import java.util.Arrays;

import optim.LocalOptimisation;
import optim.methods.PowellMethodApache;
import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;
import gridSampling.GridSampler;
import gridSampling.UniformRndSampler;

public final class GpoOptions {

	private int initialObservtions = 20;
	private int gridSampleNumber = 50;
	private GridSampler initialSampler = new UniformRndSampler();
	private GridSampler gridSampler = new UniformRndSampler();
	private LocalOptimisation localOptimiser = new PowellMethodApache();
	private boolean logspace = false;

	private int maxIterations = 500;
	private int maxAddedPointsNoImprovement = 100;
	private double improvementFactor = 1.01;
	private int maxFailedAttempts = 200;

	private double noiseTerm = 1;
	private double noiseTermRatio = 0.1;
	private boolean useNoiseTermRatio = false;
	private boolean heteroskedastic = false;
	private double beta = 2;
	private KernelFunction kernelGP = new KernelRBF();
	private boolean useDefaultHyperparams = false;
	private boolean hyperparamOptimisation = false;
	private int hyperparamOptimisationRestarts = 5;

	@Override
	public String toString() {
		String str = "# Gaussian Process Optimisation --- Setup\n";
		str += "## Grid options:\n";
		str += "Initial Observtions: " + initialObservtions + "\n";
		str += "       Grid samples: " + gridSampleNumber + "\n";
		str += "Initial sampler: " + initialSampler + "\n";
		str += "   Grid sampler: " + gridSampler + "\n";
		str += "Local Optimiser: " + localOptimiser.getClass().getName() + "\n";
		str += "      Log space: " + logspace + "\n";
		str += "## Convergence options:\n";
		str += "Max iterations: " + maxIterations + "\n";
		str += "Max failed attempts: " + maxFailedAttempts + "\n";
		str += "Max added points with no improvement: "
				+ maxAddedPointsNoImprovement + "\n";
		str += "                  Improvement Factor: " + improvementFactor
				+ "\n";
		str += "## GP options:\n";
		if (!heteroskedastic) {
			if (!useNoiseTermRatio)
				str += "Noise term (sigma^2): " + noiseTerm + "\n";
			str += "Use noise term ratio: " + useNoiseTermRatio + "\n";
			if (useNoiseTermRatio)
				str += "    Noise term ratio: " + noiseTermRatio + "\n";
		}
		str += "Heteroskedastic noise: " + heteroskedastic + "\n";
		str += "Initial beta: " + beta + "\n";
		str += "GP Kernel: " + getKernelGP().getClass().getSimpleName() + "\n";
		str += "Hyperparam opt: " + hyperparamOptimisation + "\n";
		if (hyperparamOptimisation)
			str += "Hyperparam opt restarts: " + hyperparamOptimisationRestarts
					+ "\n";
		else
			str += "Hyperparameters: "
					+ Arrays.toString(getKernelGP().getHypeparameters()) + "\n";
		return str;
	}

	/**
	 * Whether the default hyperparameters are used by the GP (these depend on
	 * the kernel and the data). If yes, then the kernel hyperparameters will be
	 * overridden.
	 */
	public boolean useDefaultHyperparams() {
		return useDefaultHyperparams;
	}

	/**
	 * Whether the default hyperparameters are used by the GP (these depend on
	 * the kernel and the data). If yes, then the kernel hyperparameters will be
	 * overridden.
	 */
	public void setUseDefaultHyperparams(boolean useDefaultHyperparams) {
		this.useDefaultHyperparams = useDefaultHyperparams;
	}

	/**
	 * Whether log-normalisation is performed; should beset equal to
	 * {@code true} only for non-negative search spaces.
	 */
	final public boolean getLogspace() {
		return logspace;
	}

	/**
	 * Whether log-normalisation is performed; should beset equal to
	 * {@code true} only for non-negative search spaces.
	 */
	final public void setLogspace(boolean logspace) {
		this.logspace = logspace;
	}

	/**
	 * The variance parameter of the Gaussian noise added to the GP will be
	 * {@code getNoiseTermRatio() * rangeOfObservations}
	 */
	final public double getNoiseTermRatio() {
		return noiseTermRatio;
	}

	/**
	 * The variance parameter of the Gaussian noise added to the GP will be
	 * {@code getNoiseTermRatio() * rangeOfObservations}
	 */
	public void setNoiseTermRatio(double noiseTermRatio) {
		this.noiseTermRatio = noiseTermRatio;
	}

	/**
	 * Whether the Gaussian noise added to the GP will be a ratio of the range
	 * of observations in the training set. If not, then an explicitly-defined
	 * variance will be used.
	 */
	public boolean getUseNoiseTermRatio() {
		return useNoiseTermRatio;
	}

	/**
	 * Whether the Gaussian noise added to the GP will be a ratio of the range
	 * of observations in the training set. If not, then an explicitly-defined
	 * variance will be used.
	 */
	public void setUseNoiseTermRatio(boolean useNoiseTermRatio) {
		this.useNoiseTermRatio = useNoiseTermRatio;
	}

	/**
	 * GP optimisation will terminate if this number of iterations is reached,
	 * regardless if it has converged or not.
	 */
	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * GP optimisation will terminate if this number of iterations is reached,
	 * regardless if it has converged or not.
	 */
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	/**
	 * If this number of added points with no significant improvement is
	 * reached, then GP optimisation is considered to have converged.<br>
	 * An improvement is considered significant if:<br>
	 * {@code newValue > oldValue * improvementFactor}
	 * 
	 * @see getImprovementFactor()
	 * @see setImprovementFactor()
	 */
	public int getMaxAddedPointsNoImprovement() {
		return maxAddedPointsNoImprovement;
	}

	/**
	 * If this number of added points with no significant improvement is
	 * reached, then GP optimisation is considered to have converged.<br>
	 * An improvement is considered significant if:<br>
	 * {@code newValue > oldValue * improvementFactor}
	 * 
	 * @see getImprovementFactor()
	 * @see setImprovementFactor()
	 */
	public void setMaxAddedPointsNoImprovement(int maxAddedPointsNoImprovement) {
		this.maxAddedPointsNoImprovement = maxAddedPointsNoImprovement;
	}

	/**
	 * An improvement is considered significant if:<br>
	 * {@code newValue > oldValue * improvementFactor}
	 * 
	 * @see getMaxAddedPointsNoImprovement()
	 * @see setMaxAddedPointsNoImprovement()
	 */
	public double getImprovementFactor() {
		return improvementFactor;
	}

	/**
	 * An improvement is considered significant if:<br>
	 * {@code newValue > oldValue * improvementFactor}
	 * 
	 * @see getMaxAddedPointsNoImprovement()
	 * @see setMaxAddedPointsNoImprovement()
	 */
	public void setImprovementFactor(double improvementFactor) {
		this.improvementFactor = improvementFactor;
	}

	/**
	 * If this number of failed attempts to find a new local optimum (for the
	 * given upper quantile) is reached, then GP optimisation is considered to
	 * have converged.
	 */
	public int getMaxFailedAttempts() {
		return maxFailedAttempts;
	}

	/**
	 * If this number of failed attempts to find a new local optimum (for the
	 * given upper quantile) is reached, then GP optimisation is considered to
	 * have converged.
	 */
	public void setMaxFailedAttempts(int maxFailedAttempts) {
		this.maxFailedAttempts = maxFailedAttempts;
	}

	/**
	 * The upper quantile, with which the search is directed, is calculated as:<br>
	 * {@code getBeta() * standardDeviation}
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * The upper quantile, with which the search is directed, is calculated as:<br>
	 * {@code getBeta() * standardDeviation}
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	/**
	 * The number of observations to initiate the GP optimisation algorithm.
	 * 
	 * @see getInitialSampler()
	 * @see setInitialSampler()
	 */
	public int getInitialObservtions() {
		return initialObservtions;
	}

	/**
	 * The number of observations to initiate the GP optimisation algorithm.
	 * 
	 * @see getInitialSampler()
	 * @see setInitialSampler()
	 */
	public void setInitialObservtions(int initialObservtions) {
		this.initialObservtions = initialObservtions;
	}

	/**
	 * The number of points at which we calculate the GP posterior at each step
	 * of the GPO.
	 * 
	 * @see getGridSampler()
	 * @see setGridSampler()
	 */
	public int getGridSampleNumber() {
		return gridSampleNumber;
	}

	/**
	 * The number of points at which we calculate the GP posterior at each step
	 * of the GPO.
	 * 
	 * @see getGridSampler()
	 * @see setGridSampler()
	 */
	public void setGridSampleNumber(int gridSampleNumber) {
		this.gridSampleNumber = gridSampleNumber;
	}

	/** The kernel function used by the GP. */
	public KernelFunction getKernelGP() {
		return kernelGP;
	}

	/** Explicitly-defined variance of the Gaussian noise added to the GP. */
	public double getNoiseTerm() {
		return noiseTerm;
	}

	/** Explicitly-defined variance of the Gaussian noise added to the GP. */
	public void setNoiseTerm(double noiseTerm) {
		this.noiseTerm = noiseTerm;
	}

	/**
	 * Whether hyperparameter optimisation is used by the GP. If yes, then the
	 * kernel hyperparameters will be overridden.
	 */
	public boolean getHyperparamOptimisation() {
		return hyperparamOptimisation;
	}

	public int getHyperparamOptimisationRestarts() {
		return hyperparamOptimisationRestarts;
	}

	/**
	 * The algorithm that creates the initial grid of values for the GP.
	 * 
	 * @see getInitialObservtions()
	 * @see setInitialObservtions()
	 */
	public GridSampler getInitialSampler() {
		return initialSampler;
	}

	/**
	 * The algorithm that creates the initial grid of values for the GP.
	 * 
	 * @see getInitialObservtions()
	 * @see setInitialObservtions()
	 */
	public void setInitialSampler(GridSampler initialSampler) {
		this.initialSampler = initialSampler;
	}

	/**
	 * The grid of points at which we calculate the GP posterior at each step of
	 * the GPO.
	 * 
	 * @see getGridSampleNumber
	 * @see setGridSampleNumber
	 */
	public GridSampler getGridSampler() {
		return gridSampler;
	}

	/**
	 * The grid of points at which we calculate the GP posterior at each step of
	 * the GPO.
	 * 
	 * @see getGridSampleNumber
	 * @see setGridSampleNumber
	 */
	public void setGridSampler(GridSampler gridSampler) {
		this.gridSampler = gridSampler;
	}

	/**
	 * The algorithm that performs local optimisation.<br>
	 * Used for:<br>
	 * - hyperparameter optimisation<br>
	 * - optimisation of the upper quantile, when a new point in the grid is
	 * selected
	 */
	public LocalOptimisation getLocalOptimiser() {
		return localOptimiser;
	}

	/**
	 * The algorithm that performs local optimisation.<br>
	 * Used for:<br>
	 * - hyperparameter optimisation<br>
	 * - optimisation of the upper quantile, when a new point in the grid is
	 * selected
	 */
	public void setLocalOptimiser(LocalOptimisation localOptimiser) {
		this.localOptimiser = localOptimiser;
	}

	/**
	 * Set the kernel function used by the GP.<br>
	 * The hyperparameters will be overridden, if
	 * {@code getHyperparamOptimisation() == true}
	 */
	public void setKernelGP(KernelFunction kernelGP) {
		this.kernelGP = kernelGP;
	}

	/**
	 * Whether hyperparameter optimisation is used by the GP. If yes, then the
	 * kernel hyperparameters will be overridden.
	 */
	public void setHyperparamOptimisation(boolean hyperparamOptimisation) {
		this.hyperparamOptimisation = hyperparamOptimisation;
	}

	public void setHyperparamOptimisationRestarts(
			int hyperparamOptimisationRestarts) {
		this.hyperparamOptimisationRestarts = hyperparamOptimisationRestarts;
	}

	/**
	 * Whether an heteroskedastic GP is assumed; if yes, then the variance for
	 * each data-point is calculated by the objective function.
	 */
	public boolean isHeteroskedastic() {
		return heteroskedastic;
	}

	/**
	 * Whether an heteroskedastic GP is assumed; if yes, then the variance for
	 * each data-point is calculated by the objective function.
	 */
	public void setHeteroskedastic(boolean heteroskedasticNoise) {
		this.heteroskedastic = heteroskedasticNoise;
	}

}
