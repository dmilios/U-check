package smoothedMC;

import linalg.NonPosDefMatrixException;
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
		try {
			return gp.getGpPosterior(points);
		} catch (NonPosDefMatrixException e) {
			// the GP is trained; this should not happen
			throw new IllegalStateException(e);
		}
	}

	public ProbitRegressionPosterior getValuesAt(double[][] points) {
		final GpDataset testSet = new GpDataset(points);
		try {
			return gp.getGpPosterior(testSet);
		} catch (NonPosDefMatrixException e) {
			// the GP is trained; this should not happen
			throw new IllegalStateException(e);
		}
	}

}
