package priors;

public class ExponentialPrior extends Prior {

	final private double lambda;

	/**
	 * @param mu
	 *            mean parameter (== 1/rate)
	 */
	public ExponentialPrior(double mu) {
		if (mu <= 0)
			throw new IllegalArgumentException();
		this.lambda = 1.0 / mu;
	}

	@Override
	public double logProbability(final double x) {
		return Math.log(lambda) - lambda * x;
	}

	@Override
	public String toString() {
		return "Exp(" + (1.0 / lambda) + ")";
	}

}
