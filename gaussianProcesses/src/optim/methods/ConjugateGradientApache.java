package optim.methods;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer.Formula;

import optim.DifferentiableObjective;
import optim.LocalOptimisation;
import optim.ObjectiveFunction;
import optim.PointValue;

public class ConjugateGradientApache extends LocalOptimisation {

	@Override
	public PointValue optimise(final ObjectiveFunction func, double[] init) {

		final MultivariateFunction f = new MultivariateFunction() {
			@Override
			public double value(double[] point) {
				return func.getValueAt(point);
			}
		};

		final MultivariateVectorFunction df = new MultivariateVectorFunction() {
			@Override
			public double[] value(double[] point)
					throws IllegalArgumentException {
				if (func instanceof DifferentiableObjective)
					return ((DifferentiableObjective) func)
							.getGradientAt(point);
				throw new IllegalArgumentException(func.getClass().getName()
						+ " is not compatible with "
						+ this.getClass().getName());
			}
		};

		// from commons.math documentation
		// On the one hand, the Fletcher-Reeves formula is guaranteed
		// to converge if the start point is close enough of the optimum
		// whether the Polak-Ribière formula may not converge in rare cases.
		// On the other hand, the Polak-Ribière formula is often faster
		// when it does converge. Polak-Ribière is often used.

		final Formula form = Formula.FLETCHER_REEVES;
		final ConvergenceChecker<PointValuePair> checker = new SimpleValueChecker(
				1e-4, 1e-8);
		final GradientMultivariateOptimizer optim;
		optim = new NonLinearConjugateGradientOptimizer(form, checker);

		final OptimizationData[] optimData = new OptimizationData[4];
		optimData[0] = new org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction(
				f);
		optimData[1] = new ObjectiveFunctionGradient(df);
		optimData[2] = new MaxEval(1000);
		optimData[3] = new InitialGuess(init);

		final PointValuePair pair = optim.optimize(optimData);
		final double[] optimum = pair.getPoint();
		final double value = pair.getValue();
		return new PointValue(optimum, value);
	}
}
