package gp.kernels;

import gp.GpDataset;

public abstract class KernelFunction {

	abstract public double calculate(final double[] x1, final double[] x2);

	abstract public double calculateDerivative(final double[] x1,
			final double[] x2, final int derivativeI);

	abstract public double calculateSecondDerivative(final double[] x1,
			final double[] x2, final int derivativeI, final int derivativeJ);

	abstract public double calculateHyperparamDerivative(final double[] x1,
			final double[] x2, final int hyperparamIndex);

	abstract public double[] getHypeparameters();

	abstract public double[] getDefaultHyperarameters(GpDataset data);

	abstract public void setHyperarameters(double[] hyp);

}
