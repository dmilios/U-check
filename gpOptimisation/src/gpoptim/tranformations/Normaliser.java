package gpoptim.tranformations;

public class Normaliser implements Transformer {

	private final double[] originalLBs;
	private final double[] originalUBs;

	public Normaliser(double[] originalLBs, double[] originalUBs) {
		this.originalLBs = originalLBs;
		this.originalUBs = originalUBs;
	}

	final protected double getOriginalLB(int i) {
		return originalLBs[i];
	}

	final protected double getOriginalUB(int i) {
		return originalUBs[i];
	}

	public double[] applyTransformation(double[] x) {
		final double[] normalised = new double[x.length];
		for (int d = 0; d < x.length; d++) {
			final double range = getOriginalUB(d) - getOriginalLB(d);
			normalised[d] = (x[d] - getOriginalLB(d)) / range;
		}
		return normalised;
	}

	public double[] invertTransformation(double[] normalised) {
		final double[] x = new double[normalised.length];
		for (int d = 0; d < normalised.length; d++) {
			final double range = getOriginalUB(d) - getOriginalLB(d);
			x[d] = normalised[d] * range + getOriginalLB(d);
		}
		return x;
	}

}
