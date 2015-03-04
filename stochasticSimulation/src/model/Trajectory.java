package model;

import java.util.Arrays;

import expr.Context;
import expr.Variable;

public final class Trajectory {

	final private double[] times;
	final private Context context;
	final private double[][] values;

	public Trajectory(double[] times, Context context, double[][] values) {
		this.times = times;
		this.context = context;
		this.values = values;
	}

	
	
	public Context getContext() {
		return context;
	}

	public final double[] getTimes() {
		return times;
	}

	public double[][] getValues() {
		return values;
	}
	
	public double[] getValues(int index) {
		return values[index];
	}

	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		bf.append("times : ");
		bf.append(Arrays.toString(times));
		bf.append("\n");
		final Variable[] vars = context.getVariables();
		for (int i = 0; i < vars.length; i++) {
			bf.append(vars[i].getName());
			bf.append(": ");
			bf.append(Arrays.toString(values[i]));
			bf.append("\n");
		}
		return bf.toString();
	}

	public String toCSV() {
		final StringBuffer bf = new StringBuffer();
		bf.append("# Time");
		final Variable[] vars = context.getVariables();
		for (int v = 0; v < vars.length; v++) {
			bf.append(",\t");
			bf.append(vars[v].getName());
		}
		bf.append("\n");
		final int n = times.length;
		for (int i = 0; i < n; i++) {
			bf.append(times[i]);
			for (int v = 0; v < vars.length; v++) {
				bf.append(",\t");
				bf.append(values[v][i]);
			}
			bf.append("\n");
		}
		return bf.toString();
	}

}
