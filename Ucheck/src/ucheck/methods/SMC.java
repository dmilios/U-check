package ucheck.methods;

import java.util.ArrayList;

import expr.Context;
import expr.Variable;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import parsers.MitlFactory;
import simhya.model.flat.FlatModel;
import ssa.Trajectory;

public class SMC {

	final private FlatModel flatModel;
	private Context context;
	private MiTL[] properties = null;

	public SMC(FlatModel flatModel) {
		this.flatModel = flatModel;
	}

	public void loadProperties(String mitlText) {
		this.context = new Context();
		for (final String name : flatModel.getOriginalModelVariables())
			new Variable(name, context);

		MitlFactory factory = new MitlFactory(context);
		MitlPropertiesList l = factory.constructProperties(mitlText);
		ArrayList<MiTL> list = l.getProperties();
		properties = new MiTL[list.size()];
		list.toArray(properties);
	}

	final private Trajectory conversion(
			simhya.dataprocessing.Trajectory simhyaTrajectory) {
		final double[][] allData = simhyaTrajectory.getAllData();
		final double[] times = allData[0];
		final double[][] values = new double[allData.length - 1][];
		for (int i = 1; i < allData.length; i++)
			values[i - 1] = allData[i];
		return new Trajectory(times, context, values);
	}

	public boolean[] modelCheck(simhya.dataprocessing.Trajectory x) {
		return modelCheck(conversion(x));
	}

	public boolean[] modelCheck(Trajectory x) {
		final int m = properties.length;
		final boolean[] result = new boolean[m];
		for (int i = 0; i < m; i++)
			result[i] = properties[i].evaluate(x, 0);
		return result;
	}

	public double[] modelCheckRobust(simhya.dataprocessing.Trajectory x) {
		return modelCheckRobust(conversion(x));
	}

	public double[] modelCheckRobust(Trajectory x) {
		final int m = properties.length;
		final double[] result = new double[m];
		for (int i = 0; i < m; i++)
			result[i] = properties[i].evaluateValue(x, 0);
		return result;
	}

}
