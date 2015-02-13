package smoothedMC;

import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;
import smoothedMC.gridSampling.GridSampler;
import smoothedMC.gridSampling.UniformRndSampler;

public final class Options {

	private double simulationEndTime = 0;
	private int simulationRuns = 10;
	private int simulationTimepoints = 200;
	private boolean timeseriesEnabled = true;

	private int inputDatapoints = 20;
	private int outputDatapoints = 100;
	private GridSampler sampler = new UniformRndSampler();

	private KernelFunction kernelGP = new KernelRBF();
	private boolean hyperparamOptimisation = false;
	private int hyperparamOptimisationRestarts = 5;
	private double covarianceCorrection = 1e-4;

	private boolean debugEnabled = false;

	public Options() {
	}

	public Options(Options copy) {
		this.simulationEndTime = copy.simulationEndTime;
		this.simulationRuns = copy.simulationRuns;
		this.simulationTimepoints = copy.simulationTimepoints;
		this.timeseriesEnabled = copy.timeseriesEnabled;
		this.inputDatapoints = copy.inputDatapoints;
		this.outputDatapoints = copy.outputDatapoints;
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

	public double getSimulationEndTime() {
		return simulationEndTime;
	}

	public void setSimulationEndTime(double simulationEndTime) {
		this.simulationEndTime = simulationEndTime;
	}

	public int getN() {
		return inputDatapoints;
	}

	public void setN(int datapoints) {
		this.inputDatapoints = datapoints;
	}

	public int getM() {
		return outputDatapoints;
	}

	public void setM(int datapoints) {
		this.outputDatapoints = datapoints;
	}

	public int getSimulationRuns() {
		return simulationRuns;
	}

	public int getSimulationTimepoints() {
		return simulationTimepoints;
	}

	public boolean isTimeseriesEnabled() {
		return timeseriesEnabled;
	}

	public KernelFunction getKernelGP() {
		return kernelGP;
	}

	public boolean getHyperparamOptimisation() {
		return hyperparamOptimisation;
	}

	public int getHyperparamOptimisationRestarts() {
		return hyperparamOptimisationRestarts;
	}

	public double getCovarianceCorrection() {
		return covarianceCorrection;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

	public void setSimulationRuns(int simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	public void setSimulationTimepoints(int simulationTimepoints) {
		this.simulationTimepoints = simulationTimepoints;
	}

	public void setTimeseriesEnabled(boolean timeseriesEnabled) {
		this.timeseriesEnabled = timeseriesEnabled;
	}

	public GridSampler getSampler() {
		return sampler;
	}

	public void setSampler(GridSampler sampler) {
		this.sampler = sampler;
	}

	public void setKernelGP(KernelFunction kernelGP) {
		this.kernelGP = kernelGP;
	}

	public void setHyperparamOptimisation(boolean hyperparamOptimisation) {
		this.hyperparamOptimisation = hyperparamOptimisation;
	}

	public void setHyperparamOptimisationRestarts(
			int hyperparamOptimisationRestarts) {
		this.hyperparamOptimisationRestarts = hyperparamOptimisationRestarts;
	}

	public void setCovarianceCorrection(double covarianceCorrection) {
		this.covarianceCorrection = covarianceCorrection;
	}

}
