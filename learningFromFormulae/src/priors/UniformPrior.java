package priors;

public final class UniformPrior extends Prior {

	final private double a;
	final private double b;

	/** Uniform distribution specified in the internal [a, b] */
	public UniformPrior(double a, double b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double logProbability(final double x) {
		if (x < a)
			return Math.log(0);
		if (x > b)
			return Math.log(0);
		return Math.log(1 / (b - a));
	}

	@Override
	public String toString() {
		return "Uniform(" + a + ", " + b + ")";
	}

}
