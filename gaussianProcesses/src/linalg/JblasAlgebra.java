package linalg;

import org.jblas.Decompose;
import org.jblas.Decompose.LUDecomposition;
import org.jblas.DoubleMatrix;
import org.jblas.NativeBlas;
import org.jblas.Singular;
import org.jblas.Solve;
import org.jblas.exceptions.LapackPositivityException;

public class JblasAlgebra implements IAlgebra {

	@Override
	public IMatrix createMatrix(double[] data) {
		return new MatrixJBLAS(new DoubleMatrix(data));
	}

	@Override
	public IMatrix createMatrix(double[][] data) {
		return new MatrixJBLAS(new DoubleMatrix(data));
	}

	@Override
	public IMatrix createZeros(int n, int m) {
		return new MatrixJBLAS(DoubleMatrix.zeros(n, m));
	}

	@Override
	public IMatrix createOnes(int n, int m) {
		return new MatrixJBLAS(DoubleMatrix.ones(n, m));
	}

	@Override
	public IMatrix createEye(int n) {
		return new MatrixJBLAS(DoubleMatrix.eye(n));
	}

	@Override
	public IMatrix createDiag(double[] data) {
		return new MatrixJBLAS(DoubleMatrix.diag(new DoubleMatrix(data)));
	}

	@Override
	public IMatrix invert(IMatrix arg) {
		return solve(arg, createEye(arg.getRows()));
	}

	@Override
	public IMatrix invertPositive(IMatrix arg) throws NonPosDefMatrixException {
		return solvePositive(arg, createEye(arg.getRows()));
	}

	@Override
	public IMatrix solve(IMatrix A, IMatrix B) {
		final DoubleMatrix a = ((MatrixJBLAS) A).getMatrixObject();
		final DoubleMatrix b = ((MatrixJBLAS) B).getMatrixObject();
		final DoubleMatrix solution = Solve.solve(a, b);
		return new MatrixJBLAS(solution);
	}

	@Override
	public IMatrix solvePositive(IMatrix A, IMatrix B)
			throws NonPosDefMatrixException {
		// The 'dposv' function writes the solution on 'b'
		final DoubleMatrix a = ((MatrixJBLAS) A).getMatrixObject().dup();
		final DoubleMatrix b = ((MatrixJBLAS) B).getMatrixObject().dup();
		try {
			NativeBlas.dposv('U', a.rows, b.columns, a.data, 0, a.rows, b.data,
					0, b.rows);
		} catch (LapackPositivityException e) {
			throw new NonPosDefMatrixException();
		}
		return new MatrixJBLAS(b);
	}

	@Override
	public void solvePositiveInPlace(IMatrix A, IMatrix B)
			throws NonPosDefMatrixException {
		// The 'dposv' function writes the solution on 'b'
		// and the Cholesky factorisation on 'a'
		// (only if 'a' is the upper part of the original positive definite
		// matrix)
		final DoubleMatrix a = ((MatrixJBLAS) A).getMatrixObject();
		final DoubleMatrix b = ((MatrixJBLAS) B).getMatrixObject();
		for (int i = 0; i < a.rows; i++)
			for (int j = 0; j < i; j++)
				a.put(i, j, 0);
		try {
			NativeBlas.dposv('U', a.rows, b.columns, a.data, 0, a.rows, b.data,
					0, b.rows);
		} catch (LapackPositivityException e) {
			throw new NonPosDefMatrixException();
		}
	}

	@Override
	public IMatrix cholesky(IMatrix arg) throws NonPosDefMatrixException {
		final DoubleMatrix mat = ((MatrixJBLAS) arg).getMatrixObject();
		try {
			return new MatrixJBLAS(Decompose.cholesky(mat));
		} catch (Exception e) {
			throw new NonPosDefMatrixException();
		}
	}

	@Override
	public IMatrix[] svd(IMatrix arg) {
		final DoubleMatrix mat = ((MatrixJBLAS) arg).getMatrixObject();
		final IMatrix[] usv = new IMatrix[3];
		final DoubleMatrix[] results = Singular.fullSVD(mat);
		for (int i = 0; i < usv.length; i++)
			usv[i] = new MatrixJBLAS(results[i]);
		return usv;
	}
	
	@Override
	public double determinant(IMatrix A) {
		final DoubleMatrix a = ((MatrixJBLAS) A).getMatrixObject();
		LUDecomposition<DoubleMatrix> lu = Decompose.lu(a);
		// det(A) = det(L) * det(U) * det(P)
		return lu.l.diag().prod() * lu.u.diag().prod() * lu.p.diag().prod();
	}

}

final class MatrixJBLAS implements IMatrix {

	final private DoubleMatrix matrixObject;

	protected MatrixJBLAS(DoubleMatrix matrixObject) {
		this.matrixObject = matrixObject;
	}

	public DoubleMatrix getMatrixObject() {
		return matrixObject;
	}

	@Override
	public String toString() {
		return matrixObject.toString();
	}

	@Override
	public double[] getData() {
		return matrixObject.data;
	}

	@Override
	public int getRows() {
		return matrixObject.rows;
	}

	@Override
	public int getColumns() {
		return matrixObject.columns;
	}

	@Override
	public int getLength() {
		return matrixObject.length;
	}

	@Override
	public double get(int i, int j) {
		return matrixObject.get(i, j);
	}

	@Override
	public void put(int i, int j, double v) {
		matrixObject.put(i, j, v);
	}

	@Override
	public double get(int i) {
		return matrixObject.get(i);
	}

	@Override
	public void put(int i, double v) {
		matrixObject.put(i, v);
	}

	@Override
	public IMatrix getColumn(int i) {
		return new MatrixJBLAS(matrixObject.getColumn(i));
	}

	@Override
	public void putColumn(int i, IMatrix col) {
		matrixObject.putColumn(i, ((MatrixJBLAS) col).getMatrixObject());
	}

	@Override
	public IMatrix getRow(int i) {
		return new MatrixJBLAS(matrixObject.getRow(i));
	}

	@Override
	public void putRow(int i, IMatrix row) {
		matrixObject.putRow(i, ((MatrixJBLAS) row).getMatrixObject());
	}

	@Override
	public void copy(IMatrix arg) {
		matrixObject.copy(((MatrixJBLAS) arg).matrixObject);
	}

	@Override
	public IMatrix duplicate() {
		return new MatrixJBLAS(matrixObject.dup());
	}

	@Override
	public IMatrix transpose() {
		return new MatrixJBLAS(matrixObject.transpose());
	}

	@Override
	public int rank() {
		final DoubleMatrix v = Singular.SVDValues(matrixObject);
		if (!v.isVector())
			throw new IllegalStateException();
		final double machineEpsilon = Math.ulp(1.0);
		int nonzero = 0;
		for (int i = 0; i < v.getLength(); i++)
			if (Math.abs(v.get(i)) > machineEpsilon)
				nonzero++;
		return nonzero;
	}

	@Override
	public IMatrix add(IMatrix arg) {
		return add((MatrixJBLAS) arg);
	}

	public IMatrix add(MatrixJBLAS arg) {
		return new MatrixJBLAS(matrixObject.add(arg.matrixObject));
	}

	@Override
	public IMatrix add(double arg) {
		return new MatrixJBLAS(matrixObject.add(arg));
	}

	@Override
	public IMatrix sub(IMatrix arg) {
		return sub((MatrixJBLAS) arg);
	}

	public IMatrix sub(MatrixJBLAS arg) {
		return new MatrixJBLAS(matrixObject.sub(arg.matrixObject));
	}

	@Override
	public IMatrix sub(double arg) {
		return new MatrixJBLAS(matrixObject.sub(arg));
	}

	@Override
	public IMatrix rsub(IMatrix arg) {
		return rsub((MatrixJBLAS) arg);
	}

	public IMatrix rsub(MatrixJBLAS arg) {
		return new MatrixJBLAS(matrixObject.rsub(arg.matrixObject));
	}

	@Override
	public IMatrix rsub(double arg) {
		return new MatrixJBLAS(matrixObject.rsub(arg));
	}

	@Override
	public IMatrix mul(IMatrix arg) {
		return mul((MatrixJBLAS) arg);
	}

	public IMatrix mul(MatrixJBLAS arg) {
		return new MatrixJBLAS(matrixObject.mul(arg.matrixObject));
	}

	@Override
	public IMatrix mul(double arg) {
		return new MatrixJBLAS(matrixObject.mul(arg));
	}

	@Override
	public IMatrix mmul(IMatrix arg) {
		return mmul((MatrixJBLAS) arg);
	}

	public IMatrix mmul(MatrixJBLAS arg) {
		return new MatrixJBLAS(matrixObject.mmul(arg.matrixObject));
	}

	@Override
	public double dot(IMatrix arg) {
		return dot((MatrixJBLAS) arg);
	}

	public double dot(MatrixJBLAS arg) {
		return matrixObject.dot(arg.matrixObject);
	}

	@Override
	public IMatrix div(IMatrix arg) {
		return div((MatrixJBLAS) arg);
	}

	public IMatrix div(MatrixJBLAS arg) {
		return new MatrixJBLAS(matrixObject.div(arg.matrixObject));
	}

	@Override
	public IMatrix div(double arg) {
		return new MatrixJBLAS(matrixObject.div(arg));
	}

	@Override
	public IMatrix rdiv(IMatrix arg) {
		return rdiv((MatrixJBLAS) arg);
	}

	public IMatrix rdiv(MatrixJBLAS arg) {
		return new MatrixJBLAS(matrixObject.rdiv(arg.matrixObject));
	}

	@Override
	public IMatrix rdiv(double arg) {
		return new MatrixJBLAS(matrixObject.rdiv(arg));
	}

	@Override
	public IMatrix neg() {
		return new MatrixJBLAS(matrixObject.neg());
	}

	@Override
	public double sum() {
		return matrixObject.sum();
	}

	@Override
	public IMatrix diag() {
		return new MatrixJBLAS(matrixObject.diag());
	}

	@Override
	public IMatrix repmat(int rows, int columns) {
		return new MatrixJBLAS(matrixObject.repmat(rows, columns));
	}

	@Override
	public double max() {
		return matrixObject.max();
	}

	@Override
	public double min() {
		return matrixObject.min();
	}

	@Override
	public IMatrix sort() {
		return new MatrixJBLAS(matrixObject.sort());
	}

}
