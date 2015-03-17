package smoothedMC;

import gp.GpDataset;
import gp.classification.ProbitRegressionPosterior;
import gp.classification.GPEP;

public final class AnalyticApproximation {

	final GPEP gp;

	/**
	 * @param gp
	 *            A trained GP which constitutes an analytic approximation to
	 *            the satisfaction function
	 */
	public AnalyticApproximation(GPEP gp) {
		this.gp = gp;
	}

	public ProbitRegressionPosterior getValuesAt(GpDataset points) {
		return gp.getGpPosterior(points);
	}

	public ProbitRegressionPosterior getValuesAt(double[][] points) {
		final GpDataset testSet = new GpDataset(points);
		return gp.getGpPosterior(testSet);
	}

}
