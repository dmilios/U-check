package tests;

import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import ssa.Trajectory;
import mitl.MiTL;

public class ObservationsGenerator {

	private int simulationRuns = 20;
	private int timepoints = 200;

	public void setSimulationRuns(int simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	public void setTimepoints(int timepoints) {
		this.timepoints = timepoints;
	}

	public boolean[][] generate(CTMCModel model, MiTL[] formulae,
			double tfinal, int runs) {
		final boolean[][] data = new boolean[runs][formulae.length];
		final StochasticSimulationAlgorithm ssa = new GillespieSSA(model);
		ssa.generateTimeseries(0, tfinal, timepoints);
		for (int run = 0; run < simulationRuns; run++) {
			final Trajectory x = ssa.generateTimeseries(0, tfinal, timepoints);
			for (int f = 0; f < formulae.length; f++)
				data[run][f] = formulae[f].evaluate(x, 0);
		}
		return data;
	}

}
