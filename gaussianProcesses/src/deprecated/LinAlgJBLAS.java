package deprecated;

import org.jblas.Decompose;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

/**
 * This class makes use of Solve.solvePositive(), which itself utilises the
 * 'dposv' routine of LAPACK. <br>
 * The 'dposv' routine calculates he solution to a real system of linear
 * equations A * X = B, where A is a symmetric positive definite matrix.<br>
 * The Cholesky decomposition is used to factor A.
 */
public class LinAlgJBLAS extends LinAlgebraGP {

	private DoubleMatrix nnK;
	private DoubleMatrix sigma2_diag;
	private DoubleMatrix y;
	private DoubleMatrix mmK;
	private DoubleMatrix mnK;
	private DoubleMatrix nmK;

	/** C = K_old + sigma2 * I */
	private DoubleMatrix C;
	private DoubleMatrix invC_y;

	private boolean intermediateResultsCalculated = false;
	private DoubleMatrix invC = null;

	@Override
	public void set(double[][] nnK, double[] sigma2, double[] y) {
		this.nnK = new DoubleMatrix(nnK);
		this.sigma2_diag = DoubleMatrix.diag(new DoubleMatrix(sigma2));
		this.y = new DoubleMatrix(y);
		C = null;
		invC_y = null;
		this.intermediateResultsCalculated = false;
		invC = null;
	}

	@Override
	public void set(double[][] mmK, double[][] mnK) {
		this.mmK = new DoubleMatrix(mmK);
		this.mnK = new DoubleMatrix(mnK);
		this.nmK = this.mnK.transpose();
	}

	@Override
	public void set(double[] mmK_diag, double[][] mnK) {
		this.mmK = new DoubleMatrix(mmK_diag);
		this.mnK = new DoubleMatrix(mnK);
		this.nmK = this.mnK.transpose();
	}

	private void calculateIntermediateResults() {
		if (intermediateResultsCalculated)
			return;
		C = nnK.add(sigma2_diag);
		invC_y = solvePositive(C, y);
		intermediateResultsCalculated = true;
	}

	@Override
	public double[] calculatePosteriorMean() {
		this.calculateIntermediateResults();
		return mnK.mmul(invC_y).toArray();
	}

	@Override
	public double[] calculatePosteriorVariance() {
		this.calculateIntermediateResults();
		final int n = C.rows;
		final int m = mmK.rows;
		if (n < m && invC == null)
			invC = invertPosDef(C);

		final DoubleMatrix invC_nmK;
		if (invC != null)
			invC_nmK = invC.mmul(nmK);
		else
			invC_nmK = solvePositive(C, nmK);
		final DoubleMatrix mnK_invC_nmK__diag = (mnK.mmul(invC_nmK)).diag();

		final DoubleMatrix sumMatrix_diag;
		if (mmK.isVector())
			sumMatrix_diag = mmK.sub(mnK_invC_nmK__diag);
		else
			sumMatrix_diag = mmK.diag().sub(mnK_invC_nmK__diag);

		for (int i = 0; i < sumMatrix_diag.rows; i++)
			if (sumMatrix_diag.get(i) < 0)
				sumMatrix_diag.put(i, 0.01);
		return sumMatrix_diag.toArray();
	}

	final private static double log2pi_x_05 = 0.5 * Math.log(2 * Math.PI);

	@Override
	public double calculateMarginalLikelihood() {
		this.calculateIntermediateResults();
		final DoubleMatrix U = Decompose.cholesky(C);

		double sum = 0;
		final double[] diag = U.diag().data;
		for (double val : diag)
			sum += Math.log(val);
		double logdetC = sum + sum;

		final double term1 = -0.5 * logdetC;
		final double term2 = -0.5 * y.transpose().dot(invC_y);
		final double term3 = -y.length * log2pi_x_05;
		return term1 + term2 + term3;
	}

	final static protected DoubleMatrix invertPosDef(DoubleMatrix mat) {
		return solvePositive(mat, DoubleMatrix.eye(mat.rows));
	}

	final static private DoubleMatrix solvePositive(DoubleMatrix mat,
			DoubleMatrix b) {
		DoubleMatrix solution;
		while (true)
			try {
				solution = Solve.solvePositive(mat, b);
				break;
			} catch (Exception e) {
				mat = mat.add(DoubleMatrix.eye(mat.rows));
			}
		return solution;
	}

}
