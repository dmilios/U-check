package gpoMC;

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

	public double getSimulationEndTime() {
		return simulationEndTime;
	}

	public void setSimulationEndTime(double simulationEndTime) {
		this.simulationEndTime = simulationEndTime;
	}

	public int getSimulationRuns() {
		return simulationRuns;
	}

	public void setSimulationRuns(int simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	public int getSimulationTimepoints() {
		return simulationTimepoints;
	}

	public void setSimulationTimepoints(int simulationTimepoints) {
		this.simulationTimepoints = simulationTimepoints;
	}

	public boolean isTimeseriesEnabled() {
		return timeseriesEnabled;
	}

	public void setTimeseriesEnabled(boolean timeseriesEnabled) {
		this.timeseriesEnabled = timeseriesEnabled;
	}

}
