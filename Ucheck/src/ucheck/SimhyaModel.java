package ucheck;

import model.ModelInterface;
import simhya.dataprocessing.DataCollector;
import simhya.dataprocessing.OdeDataCollector;
import simhya.dataprocessing.StochasticDataCollector;
import simhya.model.flat.FlatModel;
import simhya.model.flat.parser.FlatParser;
import simhya.model.flat.parser.ParseException;
import simhya.model.flat.parser.TokenMgrError;
import simhya.simengine.Simulator;
import simhya.simengine.SimulatorFactory;
import simhya.simengine.ode.OdeSimulator;
import simhya.simengine.utils.InactiveProgressMonitor;
import expr.Context;
import expr.Variable;

/** Wrapper class for SimHyA */
public class SimhyaModel implements ModelInterface {

	private FlatModel flatModel;
	private Simulator simulator;
	private DataCollector collector;

	private Context context;

	@Override
	public void loadModel(String modelFile) throws NumberFormatException,
			ParseException, TokenMgrError {
		FlatParser parser = new FlatParser();
		flatModel = parser.parseFromFile(modelFile);
		setSSA();
		this.context = new Context();
		for (final String name : flatModel.getOriginalModelVariables())
			new Variable(name, context);
	}

	@Override
	public void setParameters(String[] names, double[] values) {
		for (int i = 0; i < names.length; i++)
			flatModel.changeInitialValueOfParameter(names[i], values[i]);
	}

	public void setSSA() {
		collector = new StochasticDataCollector(flatModel);
		collector.saveAllVariables();
		simulator = SimulatorFactory.newSSAsimulator(flatModel, collector);
		simulator.setProgressMonitor(new InactiveProgressMonitor());
	}

	public void setGB() {
		collector = new StochasticDataCollector(flatModel);
		collector.saveAllVariables();
		simulator = SimulatorFactory.newGBsimulator(flatModel, collector);
		simulator.setProgressMonitor(new InactiveProgressMonitor());
	}

	public void setODE() {
		collector = new OdeDataCollector(flatModel);
		collector.saveAllVariables();
		simulator = SimulatorFactory.newODEsimulator(flatModel, collector);
		simulator.setProgressMonitor(new InactiveProgressMonitor());
	}

	@Override
	public Context getModelVariables() {
		return context;
	}

	@Override
	public model.Trajectory[] generateTrajectories(double tfinal, int runs,
			int timepoints) {

		simulator.setInitialTime(0);
		simulator.setFinalTime(tfinal);

		collector.clearAll();
		collector.storeWholeTrajectoryData(runs);
		collector.setPrintConditionByTime(timepoints, tfinal);
		simulator.initialize();
		for (int run = 0; run < runs; run++) {
			if (!(simulator instanceof OdeSimulator)) // breaks otherwise
				collector.newTrajectory();
			simulator.resetModel(true);
			simulator.reinitialize();
			simulator.run();
		}
		model.Trajectory[] trajectories = new model.Trajectory[runs];
		for (int run = 0; run < runs; run++)
			trajectories[run] = conversion(collector.getTrajectory(run));
		return trajectories;
	}

	@Override
	public model.Trajectory[] generateTrajectories(double tfinal, int runs) {
		return generateTrajectories(tfinal, runs, 1000);
	}

	final private model.Trajectory conversion(
			simhya.dataprocessing.Trajectory simhyaTrajectory) {
		final double[][] allData = simhyaTrajectory.getAllData();
		final double[] times = allData[0];
		final double[][] values = new double[allData.length - 1][];
		for (int i = 1; i < allData.length; i++)
			values[i - 1] = allData[i];
		return new model.Trajectory(times, context, values);
	}

}
