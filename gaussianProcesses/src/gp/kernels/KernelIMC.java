package gp.kernels;

import gp.GpDataset;

@SuppressWarnings("unused")
@Deprecated
public class KernelIMC extends KernelFunction {

	final private int tasks;

	final private KernelFunction localKernel;
	private double[] hyp;
	private double[][] taskCovariances;

	public KernelIMC(int tasks, KernelFunction localKernel) {
		this.tasks = tasks;
		this.localKernel = localKernel;

		int corrParams = 0;
		for (int i = 1; i <= tasks; i++)
			corrParams += i;

		final int localParams = localKernel.getHypeparameters().length;
		final double[] hyp = new double[corrParams + localParams];
		for (int i = 0; i < tasks; i++) {
			for (int j = 0; j < i; j++)
				if (i == j)
					hyp[i + j] = 1;
				else
					hyp[i + j] = 0;
		}
		for (int i = corrParams; i < hyp.length; i++)
			hyp[i] = localKernel.getHypeparameters()[i - corrParams];
		setHyperarameters(hyp);
	}

	@Override
	public double calculate(double[] x1, double[] x2) {
		return 0;
	}

	@Override
	public double calculateDerivative(double[] x1, double[] x2, int derivativeI) {
		return 0;
	}

	@Override
	public double calculateSecondDerivative(double[] x1, double[] x2,
			int derivativeI, int derivativeJ) {
		return 0;
	}

	@Override
	public double[] getHypeparameters() {
		return null;
	}

	@Override
	@Deprecated
	public double[] getDefaultHyperarameters(GpDataset data) {
		return null;
	}

	@Override
	public void setHyperarameters(double[] hyp) {
		int corrParams = 0;
		for (int i = 1; i <= tasks; i++)
			corrParams += i;

	}

	@Override
	public double calculateHyperparamDerivative(double[] x1, double[] x2,
			int hyperparamIndex) {
		return 0;
	}

}
