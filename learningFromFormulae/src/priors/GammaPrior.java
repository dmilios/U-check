package priors;

public class GammaPrior extends Prior {

	final private double alpha;
	final private double beta;

	/**
	 * @param k
	 *            shape parameter
	 * @param theta
	 *            scale parameter (== 1/rate)
	 */
	public GammaPrior(double k, double theta) {
		if (k <= 0 || theta <= 0)
			throw new IllegalArgumentException();
		this.alpha = k;
		this.beta = 1.0 / theta;
	}

	@Override
	public double logProbability(double x) {
		final double lognum = alpha * Math.log(beta) + (alpha - 1)
				* Math.log(x) - x * beta;
		final double logdenom = logGamma(alpha);
		return lognum - logdenom;
	}

	// Lanczos approximation formula. See Numerical Recipes 6.1.
	private static double logGamma(double x) {
		double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
				+ 24.01409822 / (x + 2) - 1.231739516 / (x + 3) + 0.00120858003
				/ (x + 4) - 0.00000536382 / (x + 5);
		return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	}

	@Override
	public String toString() {
		return "Gamma(" + alpha + ", " + 1.0 / beta + ")";
	}

}
