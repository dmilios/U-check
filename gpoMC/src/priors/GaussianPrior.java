package priors;

public final class GaussianPrior extends Prior {

	final private double mu;
	final private double s2;

	public GaussianPrior(double mu, double s2) {
		super();
		this.mu = mu;
		this.s2 = s2;
	}

	@Override
	public double logProbability(final double x) {
		return Math.log(1 / Math.sqrt(2 * Math.PI * s2)) - (x - mu) * (x - mu)
				/ (2 * s2);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Normal(" + mu + ", " + s2 + ")";
	}

}
