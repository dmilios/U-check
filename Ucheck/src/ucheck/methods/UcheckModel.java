package ucheck.methods;

import java.io.FileInputStream;
import java.io.IOException;

import simhya.dataprocessing.DataCollector;
import simhya.dataprocessing.StochasticDataCollector;
import simhya.dataprocessing.Trajectory;
import simhya.model.flat.FlatModel;
import simhya.model.flat.parser.FlatParser;
import simhya.model.flat.parser.ParseException;
import simhya.model.flat.parser.TokenMgrError;
import simhya.simengine.Simulator;
import simhya.simengine.SimulatorFactory;
import simhya.simengine.utils.InactiveProgressMonitor;

public class UcheckModel {

	private FlatModel flatModel;
	private Simulator simulator;
	private DataCollector collector;
	private SMC modelChecker;

	public void loadModel(String modelFile) throws NumberFormatException,
			ParseException, TokenMgrError {
		FlatParser parser = new FlatParser();
		flatModel = parser.parseFromFile(modelFile);
		setSSA();
	}

	public void loadSMCformulae(String mitlFile) throws IOException {
		final String mitlText = readFile(mitlFile);
		modelChecker = new SMC(flatModel);
		modelChecker.loadProperties(mitlText);
	}

	public boolean[][] smc_set(String[] formulae, double tfinal, int runs) {

		simulator.setInitialTime(0);
		simulator.setFinalTime(tfinal);

		final int timepoints = 1000;

		collector.clearAll();
		collector.storeWholeTrajectoryData(runs);
		collector.setPrintConditionByTime(timepoints, tfinal);
		simulator.initialize();

		for (int run = 0; run < runs; run++) {
			collector.newTrajectory();
			simulator.resetModel(true);
			simulator.reinitialize();
			simulator.run();
		}

		for (int run = 0; run < runs; run++) {
			Trajectory x = collector.getTrajectory(run);
			modelChecker.modelCheck(x);
		}

		return null;
	}

	public void setParameters(String[] names, double[] values) {
		for (int i = 0; i < names.length; i++)
			flatModel.setValueOfParameter(names[i], values[i]);
	}

	public void setSSA() {
		collector = new StochasticDataCollector(flatModel);
		collector.saveAllVariables();
		simulator = SimulatorFactory.newSSAsimulator(flatModel, collector);
		simulator.useChache(!true);
		simulator.setProgressMonitor(new InactiveProgressMonitor());
	}

	private static final String readFile(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(filename);
		final byte[] fileData = new byte[input.available()];
		input.read(fileData);
		input.close();
		return new String(fileData);
	}

}
