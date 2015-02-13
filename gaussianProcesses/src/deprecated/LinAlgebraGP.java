package deprecated;

public abstract class LinAlgebraGP extends LinearAlgebra {

	/**
	 * @param nnK
	 *            covariance matrix for training set
	 * @param sigma2
	 *            noise parameter (i.e. C = nnK + sigma2 * I)
	 * @param y
	 *            targets for training set
	 */

	public abstract void set(double[][] nnK, double[] sigma2, double[] y);

	/**
	 * @param mmK
	 *            covariance matrix for test set
	 * @param mnK
	 *            covariance matrix between test and training set
	 */
	public abstract void set(double[][] mmK, double[][] mnK);

	/**
	 * @param mmK_diag
	 *            covariance matrix diagonal for test set
	 * @param mnK
	 *            covariance matrix between test and training set
	 */
	public abstract void set(double[] mmK_diag, double[][] mnK);

	public abstract double[] calculatePosteriorMean();

	public abstract double[] calculatePosteriorVariance();

	public abstract double calculateMarginalLikelihood();

}
