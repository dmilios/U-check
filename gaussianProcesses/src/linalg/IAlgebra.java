package linalg;

public interface IAlgebra {

	public IMatrix createMatrix(double[] data);

	public IMatrix createMatrix(double[][] data);

	public IMatrix createZeros(int n, int m);

	public IMatrix createOnes(int n, int m);

	public IMatrix createEye(int n);

	public IMatrix createDiag(double[] data);

	public IMatrix invert(IMatrix arg);

	public IMatrix invertPositive(IMatrix arg) throws NonPosDefMatrixException;

	public IMatrix solve(IMatrix A, IMatrix B);

	public IMatrix solvePositive(IMatrix A, IMatrix B)
			throws NonPosDefMatrixException;

	public void solvePositiveInPlace(IMatrix A, IMatrix B)
			throws NonPosDefMatrixException;

	public IMatrix cholesky(IMatrix arg) throws NonPosDefMatrixException;

	/**
	 * Compute the singular value decomposition: arg = U*S*V'
	 * 
	 * @return An array that holds the matrices: U, S ad V
	 */
	public IMatrix[] svd(IMatrix arg);

	public double determinant(IMatrix A);

}
