package gpoptim.tranformations;

public final class LogspaceNormaliser extends Normaliser {

	public LogspaceNormaliser(double[] originalLBs, double[] originalUBs) {
		super(originalLBs, originalUBs);
	}

	public double[] applyTransformation(double[] x) {
		final double[] logx = new double[x.length];
		for (int d = 0; d < x.length; d++) {
			final double low = Math.log(getOriginalLB(d));
			final double up = Math.log(getOriginalUB(d));
			final double mean = (up + low) / 2;
			final double width = up - mean;
			final double p = Math.log(x[d]);
			logx[d] = (p - mean) / width;
		}
		return logx;
	}

	public double[] invertTransformation(double[] logx) {
		final double[] x = new double[logx.length];
		for (int d = 0; d < logx.length; d++) {
			final double low = Math.log(getOriginalLB(d));
			final double up = Math.log(getOriginalUB(d));
			final double mean = (up + low) / 2;
			final double width = up - mean;
			final double p = logx[d] * width + mean;
			x[d] = Math.exp(p);
		}
		return x;
	}

}
