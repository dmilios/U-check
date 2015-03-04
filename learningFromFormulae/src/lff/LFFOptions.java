package lff;

import gpoptim.GpoOptions;

public final class LFFOptions {

	private double simulationEndTime = 0;
	private int simulationRuns = 10;
	private int simulationTimepoints = 200;
	private boolean timeseriesEnabled = true;
	private GpoOptions gpoOptions = new GpoOptions();

	@Override
	public String toString() {
		String str = "";
		str += "# Stochastic Simulation --- Setup\n";
		str += "End time: " + simulationEndTime + "\n";
		str += "Runs: " + simulationRuns + "\n";
		str += "Timepoints: " + simulationTimepoints + "\n";
		str += "Timeseries enabled: " + timeseriesEnabled + "\n";
		str += "\n";
		str += gpoOptions.toString();
		return str;
	}

	public GpoOptions getGpoOptions() {
		return gpoOptions;
	}

	/** The time up to which the system will be simulated. */
	public double getSimulationEndTime() {
		return simulationEndTime;
	}

	/** The time up to which the system will be simulated. */
	public void setSimulationEndTime(double simulationEndTime) {
		this.simulationEndTime = simulationEndTime;
	}

	/**
	 * The number of the independent simulations for each parameter value.
	 */
	public int getSimulationRuns() {
		return simulationRuns;
	}

	/**
	 * The number of the independent simulations for each parameter value.
	 */
	public void setSimulationRuns(int simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	/**
	 * The number of time-points for which the state is recorded in a
	 * time-series
	 */
	public int getSimulationTimepoints() {
		return simulationTimepoints;
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
	public boolean isTimeseriesEnabled() {
		return timeseriesEnabled;
	}

	/**
	 * Whether time-series will be used in model checking, instead of proper
	 * trajectories. For large models, the use of time-series is recommended.
	 */
	public void setTimeseriesEnabled(boolean timeseriesEnabled) {
		this.timeseriesEnabled = timeseriesEnabled;
	}

}
