package gpoptim;

public final class LogspaceConverter {

	private final double[] originalLBs;
	private final double[] originalUBs;

	public LogspaceConverter(double[] originalLBs, double[] originalUBs) {
		this.originalLBs = originalLBs;
		this.originalUBs = originalUBs;
	}

	public double[] convertToLogspace(double[] x) {
		final double[] logx = new double[x.length];
		for (int d = 0; d < x.length; d++) {
			final double low = Math.log(originalLBs[d]);
			final double up = Math.log(originalUBs[d]);
			final double mean = (up + low) / 2;
			final double width = up - mean;
			final double p = Math.log(x[d]);
			logx[d] = (p - mean) / width;
		}
		return logx;
	}

	public double[] convertfromLogspace(double[] logx) {
		final double[] x = new double[logx.length];
		for (int d = 0; d < logx.length; d++) {
			final double low = Math.log(originalLBs[d]);
			final double up = Math.log(originalUBs[d]);
			final double mean = (up + low) / 2;
			final double width = up - mean;
			final double p = logx[d] * width + mean;
			x[d] = Math.exp(p);
		}
		return x;
	}

}
