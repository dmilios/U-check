package modelChecking;

import java.util.ArrayList;

import expr.Context;
import expr.Variable;

public class MovingAvgFunction extends SignalFunction {

	private double movingWindowSize;

	public MovingAvgFunction(String name, Context context, Variable var,
			double width) {
		super(name, context, var);
		this.movingWindowSize = width;
	}

	@Override
	public double[] evaluateSignal(double[] t, double[] x) {
		final int n = (int) (0.5 * movingWindowSize / (t[1] - t[0]));
		return extractMovingAvg(x, n);
	}

	public static final double[] extractMovingAvg(final double[] x, int n) {
		final double[] y = new double[x.length];
		final ArrayList<Double> collection = new ArrayList<Double>(n);
		for (int i = 0; i < y.length; i++) {
			collection.clear();
			collection.add(x[i]);
			for (int j = 1; j <= n; j++)
				if (i + j < x.length)
					collection.add(x[i + j]);
			for (int j = 1; j <= n; j++)
				if (i - j >= 0)
					collection.add(x[i - j]);
			y[i] = average(collection);
		}
		return y;
	}

	private static double sum(ArrayList<Double> list) {
		double sum = 0;
		for (int i = 0; i < list.size(); i++)
			sum = sum + list.get(i);
		return sum;
	}

	private static double average(ArrayList<Double> list) {
		return sum(list) / list.size();
	}

}
