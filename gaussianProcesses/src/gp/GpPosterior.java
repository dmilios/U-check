package gp;

public abstract class GpPosterior {

	final private GpDataset inputData;
	final private double[] mean;
	final private double[] variance;
	final private double[] standardDeviation;

	protected GpPosterior(GpDataset inputData, double[] mean, double[] var) {
		this.inputData = inputData;
		this.mean = mean;
		this.variance = var;
		standardDeviation = new double[variance.length];
		for (int i = 0; i < standardDeviation.length; i++)
			standardDeviation[i] = Math.sqrt(variance[i]);
	}

	final public int getSize() {
		return mean.length;
	}

	final public GpDataset getInputData() {
		return inputData;
	}

	final public double[] getMean() {
		return mean;
	}

	final public double[] getVariance() {
		return variance;
	}

	final public double[] getStandardDeviation() {
		return standardDeviation;
	}

	abstract public double[] getLowerBound(final double beta);

	abstract public double[] getUpperBound(final double beta);

}
