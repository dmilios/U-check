package modelChecking;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import expr.Context;
import parsers.MitlFactory;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import model.ModelInterface;
import model.Trajectory;

public class MitlModelChecker {

	final private ModelInterface model;
	private MiTL[] properties = null;

	private ArrayList<String> errors = new ArrayList<String>();
	private ArrayList<SignalFunction> signalFunctions;

	public MitlModelChecker(ModelInterface model) {
		this.model = model;
	}

	public ModelInterface getModel() {
		return model;
	}

	public MiTL[] getProperties() {
		return properties;
	}

	public void loadProperties(String file) throws Exception {
		setProperties(readFile(file));
	}

	public ArrayList<String> getErrors() {
		return errors;
	}

	public void setProperties(String mitlText) throws Exception {
		final Context context = model.getModelVariables();
		MitlFactory factory = new MitlFactory(context);
		MitlPropertiesList l = factory.constructProperties(mitlText);
		errors.clear();
		errors.addAll(factory.getErrors());
		if (errors.size() > 0)
			return;

		ArrayList<MiTL> list = l.getProperties();
		properties = new MiTL[list.size()];
		list.toArray(properties);
		signalFunctions = factory.getFunctionsOfSignals();
	}

	private Trajectory enchance(final Trajectory x) {
		final Context context = x.getContext();
		final int totalVariables = context.getVariables().length;
		final int initVariables = x.getValues().length;
		if (totalVariables == initVariables)
			return x;

		double[][] values = new double[totalVariables][];
		for (int i = 0; i < initVariables; i++)
			values[i] = x.getValues()[i];
		for (int i = initVariables; i < totalVariables; i++) {
			SignalFunction sf = signalFunctions.get(i - initVariables);
			final int index = sf.getVariable().getId();
			values[i] = sf.evaluateSignal(x.getTimes(), values[index]);
		}
		return new Trajectory(x.getTimes(), x.getContext(), values);
	}

	public boolean[][] performMC(double tfinal, int runs, int timepoints) {
		if (properties == null || properties.length == 0)
			throw new IllegalStateException(
					"No properties have been specified!");
		final boolean[][] results = new boolean[runs][];
		final Trajectory[] trajectories = model.generateTrajectories(tfinal,
				runs, timepoints);
		for (int run = 0; run < runs; run++)
			results[run] = modelCheck(trajectories[run]);
		return results;
	}

	public boolean[][] performMC(double tfinal, int runs) {
		if (properties == null || properties.length == 0)
			throw new IllegalStateException(
					"No properties have been specified!");
		final boolean[][] results = new boolean[runs][];
		final Trajectory[] trajectories = model.generateTrajectories(tfinal,
				runs);
		for (int run = 0; run < runs; run++)
			results[run] = modelCheck(trajectories[run]);
		return results;
	}

	public double[][] performMCRobust(double tfinal, int runs, int timepoints) {
		if (properties == null || properties.length == 0)
			throw new IllegalStateException(
					"No properties have been specified!");
		final double[][] results = new double[runs][];
		final Trajectory[] trajectories = model.generateTrajectories(tfinal,
				runs, timepoints);
		for (int run = 0; run < runs; run++)
			results[run] = modelCheckRobust(trajectories[run]);
		return results;
	}

	public double[][] performMCRobust(double tfinal, int runs) {
		if (properties == null || properties.length == 0)
			throw new IllegalStateException(
					"No properties have been specified!");
		final double[][] results = new double[runs][];
		final Trajectory[] trajectories = model.generateTrajectories(tfinal,
				runs);
		for (int run = 0; run < runs; run++)
			results[run] = modelCheckRobust(trajectories[run]);
		return results;
	}

	private boolean[] modelCheck(Trajectory x) {
		final int m = properties.length;
		final boolean[] result = new boolean[m];
		for (int i = 0; i < m; i++)
			result[i] = properties[i].evaluate(enchance(x), 0);
		return result;
	}

	private double[] modelCheckRobust(Trajectory x) {
		final int m = properties.length;
		final double[] result = new double[m];
		for (int i = 0; i < m; i++)
			result[i] = properties[i].evaluateValue(enchance(x), 0);
		return result;
	}

	private static final String readFile(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(filename);
		final byte[] fileData = new byte[input.available()];
		input.read(fileData);
		input.close();
		return new String(fileData);
	}

}
