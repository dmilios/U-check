package smoothedMC;

import gp.GpDataset;
import gp.classification.ClassificationPosterior;
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

	public ClassificationPosterior getValuesAt(GpDataset points) {
		return gp.getGpPosterior(points);
	}

	public ClassificationPosterior getValuesAt(double[][] points) {
		final GpDataset testSet = new GpDataset(points);
		return gp.getGpPosterior(testSet);
	}

}
