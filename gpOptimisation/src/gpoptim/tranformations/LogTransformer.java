package gpoptim.tranformations;

public class LogTransformer implements Transformer {

	@Override
	public double[] applyTransformation(double[] x) {
		final double[] logx = new double[x.length];
		for (int d = 0; d < x.length; d++)
			logx[d] = Math.log(x[d]);
		return logx;
	}

	@Override
	public double[] invertTransformation(double[] logx) {
		final double[] x = new double[logx.length];
		for (int d = 0; d < logx.length; d++)
			x[d] = Math.exp(logx[d]);
		return x;
	}
}
