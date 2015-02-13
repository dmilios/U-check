package smoothedMC.gridSampling;

import smoothedMC.Parameter;

public class UniformRndSampler implements GridSampler {

	@Override
	public double[][] sample(int n, Parameter[] params) {
		final int dim = params.length;
		final double[][] inputVals = new double[n][];
		for (int i = 0; i < n; i++) {
			inputVals[i] = new double[dim];
			for (int j = 0; j < dim; j++) {
				final double l = params[j].getLowerBound();
				final double u = params[j].getUpperBound();
				inputVals[i][j] = rand.nextDouble() * (u - l) + l;
			}
		}
		return inputVals;
	}

}
