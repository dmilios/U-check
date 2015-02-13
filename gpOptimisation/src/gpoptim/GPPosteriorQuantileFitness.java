package gpoptim;

import gp.AbstractGP;
import gp.GpDataset;
import gp.regression.RegressionPosterior;
import optim.ObjectiveFunction;

public class GPPosteriorQuantileFitness implements ObjectiveFunction {

	private final AbstractGP<RegressionPosterior> gp;
	private final double beta;
	private final GpDataset testDatapoint;

	public GPPosteriorQuantileFitness(AbstractGP<RegressionPosterior> gp,
			double beta) {
		this.gp = gp;
		this.beta = beta;
		testDatapoint = new GpDataset(gp.getTrainingSet().getDimension(), 1);
	}

	@Override
	public double getValueAt(double... point) {
		testDatapoint.set(point);
		final double value = gp.getGpPosterior(testDatapoint).getUpperBound(
				beta)[0];
		return value;
	}

}
