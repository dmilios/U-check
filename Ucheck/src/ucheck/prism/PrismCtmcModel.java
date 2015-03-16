package ucheck.prism;

import java.io.FileInputStream;

import model.ModelInterface;
import model.Trajectory;
import parser.PrismParser;
import parser.ast.ModulesFile;
import prism.ModelType;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import expr.Context;
import expr.Variable;

public class PrismCtmcModel implements ModelInterface {

	private CTMCModel ctmcModel;
	private Context context;
	private StochasticSimulationAlgorithm simulator;

	@Override
	public Context getModelVariables() {
		return context;
	}

	@Override
	public void loadModel(String modelFile) throws Exception {
		FileInputStream istr = new FileInputStream(modelFile);
		PrismParser p = new PrismParser();
		ModulesFile modules = p.parseModulesFile(istr, ModelType.CTMC);
		modules.tidyUp();
		Prism2PCTMC converter = new Prism2PCTMC();
		ctmcModel = converter.extractPCTMC(modules);
		context = ctmcModel.getStateVariables();
		simulator = new GillespieSSA(ctmcModel);
	}

	@Override
	public void setParameters(String[] names, double[] values) {
		for (int i = 0; i < names.length; i++) {
			Variable var = ctmcModel.getConstants().getVariable(names[i]);
			ctmcModel.getConstants().setValue(var, values[i]);
		}
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
		return trajectories;
	}
}
