package smoothedMC;

import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;
import smoothedMC.gridSampling.GridSampler;
import smoothedMC.gridSampling.RegularSampler;

public final class SmmcOptions {

	private double simulationEndTime = 0;
	private int simulationRuns = 10;
	private int simulationTimepoints = 200;
	private boolean timeseriesEnabled = true;

	private int initialObservtions = 20;
	private int numberOfTestPoints = 100;
	private GridSampler sampler = new RegularSampler();

	private KernelFunction kernelGP = new KernelRBF();
	private boolean useDefaultHyperparams = true;
	private boolean hyperparamOptimisation = false;
	private int hyperparamOptimisationRestarts = 5;
	private double covarianceCorrection = 1e-4;

	private boolean debugEnabled = false;

	public SmmcOptions() {
	}

	public SmmcOptions(SmmcOptions copy) {
		this.simulationEndTime = copy.simulationEndTime;
		this.simulationRuns = copy.simulationRuns;
		this.simulationTimepoints = copy.simulationTimepoints;
		this.timeseriesEnabled = copy.timeseriesEnabled;
		this.initialObservtions = copy.initialObservtions;
		this.numberOfTestPoints = copy.numberOfTestPoints;
		this.sampler = copy.sampler;
		this.kernelGP = copy.kernelGP;
		this.hyperparamOptimisation = copy.hyperparamOptimisation;
		this.hyperparamOptimisationRestarts = copy.hyperparamOptimisationRestarts;
		this.covarianceCorrection = copy.covarianceCorrection;
		this.debugEnabled = copy.debugEnabled;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	/** The time up to which the system will be simulated. */
	public double getSimulationEndTime() {
		return simulationEndTime;
	}

	/** The time up to which the system will be simulated. */
	public void setSimulationEndTime(double simulationEndTime) {
		this.simulationEndTime = simulationEndTime;
	}

	/** The number of points in the training set of the GP. */
	public int getN() {
		return initialObservtions;
	}

	/** The number of points in the training set of the GP. */
	public void setN(int datapoints) {
		this.initialObservtions = datapoints;
	}

	/**
	 * The number of points at which we explore the value of the satisfaction
	 * function. These will be the test points in the GP.
	 */
	public int getM() {
		return numberOfTestPoints;
	}

	/**
	 * The number of points at which we explore the value of the satisfaction
	 * function. These will be the test points in the GP.
	 */
	public void setM(int datapoints) {
		this.numberOfTestPoints = datapoints;
	}

	/**
	 * The number of the independent simulations for each parameter value during
	 * the initial SMC step.
	 */
	public int getSimulationRuns() {
		return simulationRuns;
	}

	/**
	 * The number of time-points for which the state is recorded in a
	 * time-series
	 */
	public int getSimulationTimepoints() {
		return simulationTimepoints;
	}

	/**
	 * Whether time-series will be used in model checking, instead of proper
	 * trajectories. For large models, the use of time-series is recommended.
	 */
	public boolean isTimeseriesEnabled() {
		return timeseriesEnabled;
	}

	/** The kernel function used by the GP. */
	public KernelFunction getKernelGP() {
		return kernelGP;
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
	 * A very small correction added to the diagonal of the Gram matrix to
	 * ensure that it is positive definite.<br>
	 * The default value is 1e-4.<br>
	 * Try increasing this value if the Gram matrix appears to be non-positive
	 * definite.
	 */
	public double getCovarianceCorrection() {
		return covarianceCorrection;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

	/**
	 * The number of the independent simulations for each parameter value during
	 * the initial SMC step.
	 */
	public void setSimulationRuns(int simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	/**
	 * The number of time-points for which the state is recorded in a
	 * time-series
	 */
	public void setSimulationTimepoints(int simulationTimepoints) {
		this.simulationTimepoints = simulationTimepoints;
	}

	/**
	 * Whether time-series will be used in model checking, instead of proper
	 * trajectories. For large models, the use of time-series is recommended.
	 */
	public void setTimeseriesEnabled(boolean timeseriesEnabled) {
		this.timeseriesEnabled = timeseriesEnabled;
	}

	public GridSampler getSampler() {
		return sampler;
	}

	public void setSampler(GridSampler sampler) {
		this.sampler = sampler;
	}

	/** The kernel function used by the GP. */
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
	 * A very small correction added to the diagonal of the Gram matrix to
	 * ensure that it is positive definite.<br>
	 * The default value is 1e-4.<br>
	 * Try increasing this value if the Gram matrix appears to be non-positive
	 * definite.
	 */
	public void setCovarianceCorrection(double covarianceCorrection) {
		this.covarianceCorrection = covarianceCorrection;
	}

}
