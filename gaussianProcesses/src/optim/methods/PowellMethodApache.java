package optim.methods;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;

import optim.LocalOptimisation;
import optim.ObjectiveFunction;
import optim.PointValue;

public class PowellMethodApache extends LocalOptimisation {

	@Override
	public PointValue optimise(final ObjectiveFunction func, double[] init) {

		// Just keep track of the best solution manually
		// To be used as last resort if the Powell method fails
		final double[] bestSoFar = new double[init.length];
		final double[] bestValueSoFar = { -Double.MAX_VALUE };
		System.arraycopy(init, 0, bestSoFar, 0, init.length);

		final MultivariateFunction f = new MultivariateFunction() {
			@Override
			public double value(double[] point) {
				final double value = func.getValueAt(point);
				if (value > bestValueSoFar[0]) {
					bestValueSoFar[0] = value;
					System.arraycopy(point, 0, bestSoFar, 0, point.length);
				}
				return value;
			}
		};

		MultivariateOptimizer optim = new PowellOptimizer(1e-8, 1e-8);
		final OptimizationData[] optimData = new OptimizationData[3];
		optimData[0] = new org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction(
				f);
		optimData[1] = new MaxEval(1000);

		optimData[2] = new InitialGuess(init);

		final PointValuePair pair;
		double[] optimum;
		double value;

		try {
			pair = optim.optimize(optimData);
			optimum = pair.getPoint();
			value = pair.getValue();
		} catch (TooManyEvaluationsException e) {
			// last resort if the Powell method fails
			optimum = bestSoFar;
			value = bestValueSoFar[0];
		}

		return new PointValue(optimum, value);
	}

}
