package gp.kernels;

import gp.GpDataset;

public class KernelRBF extends KernelFunction {

	private double[] hyp;
	private double amplitude2;
	private double invLengthscale2;

	public KernelRBF() {
		this(1, 1);
	}

	public KernelRBF(double a, double l) {
		setHyperarameters(new double[] { a, l });
	}

	@Override
	public void setHyperarameters(double[] hyp) {
		this.hyp = hyp;
		final double a = hyp[0];
		final double l = hyp[1];
		this.amplitude2 = a * a;
		this.invLengthscale2 = 1.0 / (l * l);
	}

	@Override
	public double[] getHyperarameters() {
		return hyp;
	}

	@Override
	public double[] getDefaultHyperarameters(GpDataset data) {
		final double[] defaultHyp = new double[hyp.length];
		double max, min;

		max = Double.NEGATIVE_INFINITY;
		min = Double.POSITIVE_INFINITY;
		final double[] y = data.getTargets();
		for (int i = 0; i < y.length; i++) {
			if (y[i] > max)
				max = y[i];
			if (y[i] < min)
				min = y[i];
		}
		defaultHyp[0] = (max - min) / 10;

		double sum = 0;
		final int n = data.getSize();
		final int dim = data.getDimension();
		for (int d = 0; d < dim; d++) {
			max = Double.NEGATIVE_INFINITY;
			min = Double.POSITIVE_INFINITY;
			for (int i = 0; i < n; i++) {
				final double curr = data.getInstance(i)[d];
				if (curr > max)
					max = curr;
				if (curr < min)
					min = curr;
			}
			sum += (max - min) / 10;
		}
		defaultHyp[1] = sum / dim;

		return defaultHyp;
	}

	@Override
	public double calculate(double[] x1, double[] x2) {
		final int n = x1.length;
		double sum = 0;
		for (int i = 0; i < n; i++) {
			final double v = x1[i] - x2[i];
			sum += v * v;
		}
		// exp(-0.5 * sum((x1 - x2).^2));
		return amplitude2 * Math.exp(-0.5 * sum * invLengthscale2);
	}

	@Override
	public double calculateDerivative(double[] x1, double[] x2, int i) {
		// dfxdx = -(x / l^2) * fx(x);
		final double k0 = calculate(x1, x2);
		final double ki = -invLengthscale2 * (x1[i] - x2[i]);
		return k0 * ki;
	}

	@Override
	public double calculateSecondDerivative(double[] x1, double[] x2, int i,
			int j) {
		final double k0 = calculate(x1, x2);
		final double ki = 2 * invLengthscale2 * (x1[i] - x2[i]);
		final double kj = 2 * invLengthscale2 * (x1[j] - x2[j]);
		double k = k0 * ki * kj;
		if (i == j)
			k = k - 2 * invLengthscale2 * k0;
		return k;
	}

}
