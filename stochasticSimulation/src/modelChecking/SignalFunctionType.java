package modelChecking;

import java.util.ArrayList;

import expr.ArithmeticExpression;
import expr.Variable;

public enum SignalFunctionType {

	diff {
		public void setArguments(ArithmeticExpression... arguments)
				throws Exception {
			super.setArguments(arguments);
			if (arguments.length != 1)
				throw new Exception(usage());
		}

		@Override
		public double[] evaluateSignal(double[] t, double[] x) {
			final double[] dxdt = new double[x.length];
			dxdt[0] = 0;
			for (int timeIdx = 1; timeIdx < dxdt.length; timeIdx++)
				dxdt[timeIdx] = (x[timeIdx] - x[timeIdx - 1])
						/ (t[timeIdx] - t[timeIdx - 1]);
			return dxdt;
		}
	},

	movavg {
		private double movingWindowSize;

		public String usage() {
			return "Correct usage: " + name() + "(VARIABLE, WIDTH)";
		}

		@Override
		public void setArguments(ArithmeticExpression... arguments)
				throws Exception {
			if (arguments.length != 2)
				throw new Exception(usage());
			if (!arguments[1].getVariables().isEmpty())
				throw new Exception(usage());
			super.setArguments(arguments);
			movingWindowSize = arguments[1].evaluate();
		}

		@Override
		public double[] evaluateSignal(double[] t, double[] x) {
			final int n = (int) (0.5 * movingWindowSize / (t[1] - t[0]));
			return extractMovingAvg(x, n);
		}

	};

	// =================== interface of enum

	private Variable variable;

	public String usage() {
		return "Correct usage: " + name() + "(VARIABLE)";
	}

	public void setArguments(ArithmeticExpression... arguments)
			throws Exception {
		variable = (Variable) arguments[0];
		if (arguments.length == 0 || !(arguments[0] instanceof Variable))
			throw new Exception(usage());
	}

	public double[] evaluateSignal(final double[] t, final double[] x) {
		return null;
	}

	public Variable getVariable() {
		return variable;
	}

	// =================== static helper functions

	private static final double[] extractMovingAvg(final double[] x, int n) {
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
