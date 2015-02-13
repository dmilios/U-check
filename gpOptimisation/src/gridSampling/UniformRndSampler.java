package gridSampling;

public class UniformRndSampler implements GridSampler {

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	@Override
	public double[][] sample(int n, double[] lbounds, double[] ubounds) {
		final int dim = lbounds.length;
		final double[][] inputVals = new double[n][];
		for (int i = 0; i < n; i++) {
			inputVals[i] = new double[dim];
			for (int j = 0; j < dim; j++) {
				final double l = lbounds[j];
				final double u = ubounds[j];
				inputVals[i][j] = rand.nextDouble() * (u - l) + l;
			}
		}
		return inputVals;
	}

}
