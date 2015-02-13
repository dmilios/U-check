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
		final MultivariateFunction f = new MultivariateFunction() {
			@Override
			public double value(double[] point) {
				return func.getValueAt(point);
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
			System.out.println("Powell method failed; using initial value");
			optimum = init;
			value = Double.NEGATIVE_INFINITY;
		}

		return new PointValue(optimum, value);
	}

}
