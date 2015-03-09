package modelChecking;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import parsers.MitlFactory;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import mitl.SignalFunction;
import model.ModelInterface;
import model.Trajectory;

public class MitlModelChecker {

	final private ModelInterface model;
	private MiTL[] properties = null;
	private ArrayList<String> errors = new ArrayList<String>();

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
		MitlFactory factory = new MitlFactory(model.getModelVariables());
		MitlPropertiesList l = factory.constructProperties(mitlText);
		errors.clear();
		errors.addAll(factory.getErrors());

		ArrayList<SignalFunction> sfs = factory.getSignals();
		
		
		final ArrayList<String> falseErrors = new ArrayList<String>();
		for (String error : errors) {

			// very ugly... refactor some time...
			final String prefix = "Function \"";
			final String postfix = "\" is not defined!";
			if (error.startsWith(prefix) && error.endsWith(postfix)) {
				final String name = error.substring(prefix.length(),
						error.indexOf(postfix));

				if (name.equals("movavg"))
					falseErrors.add(error);
			}
		}
		for (String err : falseErrors)
			errors.remove(err);

		if (errors.size() == 0) {
			ArrayList<MiTL> list = l.getProperties();
			properties = new MiTL[list.size()];
			list.toArray(properties);
		}
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
			result[i] = properties[i].evaluate(x, 0);
		return result;
	}

	private double[] modelCheckRobust(Trajectory x) {
		final int m = properties.length;
		final double[] result = new double[m];
		for (int i = 0; i < m; i++)
			result[i] = properties[i].evaluateValue(x, 0);
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
