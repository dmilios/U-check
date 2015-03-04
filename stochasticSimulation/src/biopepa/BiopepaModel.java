package biopepa;

import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import expr.Context;
import model.ModelInterface;
import model.Trajectory;

public class BiopepaModel implements ModelInterface {

	private BiopepaFile biopepafile;
	private CTMCModel ctmcModel;
	private Context context;
	private StochasticSimulationAlgorithm simulator;

	@Override
	public Context getModelVariables() {
		return context;
	}

	@Override
	public void loadModel(String modelFile) throws Exception {
		biopepafile = new BiopepaFile(modelFile);
		ctmcModel = biopepafile.getModel();
		context = ctmcModel.getStateVariables();
		simulator = new GillespieSSA(ctmcModel);
	}

	@Override
	public void setParameters(String[] names, double[] values) {
		ctmcModel = biopepafile.getModel(names, values);
		// context = ctmcModel.getStateVariables();
		simulator = new GillespieSSA(ctmcModel);
	}

	@Override
	public Trajectory[] generateTrajectories(double stopTime, int runs,
			int timepoints) {
		final Trajectory[] trajectories = new Trajectory[runs];
		Trajectory x;
		for (int run = 0; run < runs; run++) {
			x = simulator.generateTimeseries(0, stopTime, timepoints);
			trajectories[run] = new Trajectory(x.getTimes(), context,
					x.getValues());
		}
		// NOTE: have to use the old context, because that only is
		// compatible with the model checker.
		// This is a deep bug; refactoring is required in the future
		return trajectories;
	}

	@Override
	public Trajectory[] generateTrajectories(double stopTime, int runs) {
		final Trajectory[] trajectories = new Trajectory[runs];
		Trajectory x;
		for (int run = 0; run < runs; run++) {
			x = simulator.generateTrajectory(0, stopTime);
			trajectories[run] = new Trajectory(x.getTimes(), context,
					x.getValues());
		}
		// NOTE: have to use the old context, because that only is
		// compatible with the model checker.
		// This is a deep bug; refactoring is required in the future
		return trajectories;
	}

}
