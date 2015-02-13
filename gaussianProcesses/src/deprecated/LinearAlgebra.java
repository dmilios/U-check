package deprecated;

public abstract class LinearAlgebra {

	final static public void scale(double[][] matrix, double a) {
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j] *= a;
	}

	final static public void mmult(final double[][] A, final double[] x,
			final double[] result) {
		final int n = x.length;
		for (int i = 0; i < n; i++)
			result[i] = dot(A[i], x);
	}

	final static public double dot(final double[] x1, final double[] x2) {
		final int n = x1.length;
		double sum = 0;
		for (int i = 0; i < n; i++)
			sum += x1[i] * x2[i];
		return sum;
	}

	final static public String toMatlab(double[][] matrix) {
		final StringBuffer bf = new StringBuffer();
		bf.append("[");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++)
				bf.append(" " + matrix[i][j]);
			bf.append("; ");
		}
		bf.append("]");
		return bf.toString();
	}

}
