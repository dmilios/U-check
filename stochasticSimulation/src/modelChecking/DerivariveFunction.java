package modelChecking;

import expr.Context;
import expr.Variable;

public class DerivariveFunction extends SignalFunction {

	public DerivariveFunction(String name, Context context, Variable var) {
		super(name, context, var);
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

}
