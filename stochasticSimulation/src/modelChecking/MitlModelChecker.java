package modelChecking;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import parsers.MitlFactory;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import model.ModelInterface;
import model.Trajectory;

public class MitlModelChecker {

	final private ModelInterface model;
	private MiTL[] properties = null;

	public MitlModelChecker(ModelInterface model) {
		this.model = model;
	}

	public ModelInterface getModel() {
		return model;
	}

	public MiTL[] getProperties() {
		return properties;
	}
	
	public void loadProperties(String file) throws IOException {
		setProperties(readFile(file));
	}

	public void setProperties(String mitlText) {
		MitlFactory factory = new MitlFactory(model.getModelVariables());
		MitlPropertiesList l = factory.constructProperties(mitlText);
		ArrayList<MiTL> list = l.getProperties();
		properties = new MiTL[list.size()];
		list.toArray(properties);
	}

	public boolean[][] performMC(double tfinal, int runs, int timepoints) {
		final boolean[][] results = new boolean[runs][];
		final Trajectory[] trajectories = model.generateTrajectories(tfinal,
				runs, timepoints);
		for (int run = 0; run < runs; run++)
			results[run] = modelCheck(trajectories[run]);
		return results;
	}

	public double[][] performMCRobust(double tfinal, int runs, int timepoints) {
		final double[][] results = new double[runs][];
		final Trajectory[] trajectories = model.generateTrajectories(tfinal,
				runs, timepoints);
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
