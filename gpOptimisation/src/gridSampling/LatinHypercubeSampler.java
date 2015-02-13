package gridSampling;

import java.util.ArrayList;

/**
 * Latin hypercube sampling implementation. The number of subdivisions have to
 * be specified, along with the total number of samples 'n'.<br>
 * Ideally, the total number of samples should be a multiple of
 * {@code subdivisions^dim}.
 */
public class LatinHypercubeSampler implements GridSampler {

	private int subdivisions;

	/**
	 * @param subdivisions
	 *            of each dimension. <br>
	 *            Ideally, the total number of samples should be a multiple of
	 *            {@code subdivisions^dim}.
	 */
	public LatinHypercubeSampler(int subdivisions) {
		this.subdivisions = subdivisions;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " -- " + getSubdivisions()
				+ " subdivisions";
	}

	public int getSubdivisions() {
		return subdivisions;
	}

	public void setSubdivisions(int subdivisions) {
		this.subdivisions = subdivisions;
	}

	@Override
	public double[][] sample(int n, double[] lbounds, double[] ubounds) {

		if (lbounds.length != ubounds.length)
			throw new IllegalArgumentException(
					"lbounds.length != ubounds.length");
		for (int i = 0; i < ubounds.length; i++)
			if (lbounds[i] >= ubounds[i])
				throw new IllegalArgumentException("lbound >= ubound");

		final double[] ranges = new double[lbounds.length];
		for (int j = 0; j < lbounds.length; j++)
			ranges[j] = ubounds[j] - lbounds[j];

		final int dim = lbounds.length;
		final int samplesPerSubspace = (int) Math.ceil(n
				/ Math.pow(subdivisions, dim));
		final int n_actual = (int) (Math.pow(subdivisions, dim) * samplesPerSubspace);

		final double[][] samples = sampleOrthogonal(dim, subdivisions,
				samplesPerSubspace);
		for (int i = 0; i < samples.length; i++)
			for (int j = 0; j < samples[i].length; j++) {
				samples[i][j] /= n_actual;
				samples[i][j] *= ranges[j];
				samples[i][j] += lbounds[j];
			}

		if (n == n_actual)
			return samples;

		if (n < n_actual) {
			final int discard = n_actual - n;
			final ArrayList<Integer> bag = new ArrayList<Integer>(n_actual);
			for (int i = 0; i < n_actual; i++)
				bag.add(i);
			for (int i = 0; i < discard; i++)
				bag.remove(rand.nextInt(bag.size()));

			final double[][] revisedSamples = new double[n][];
			int revisedIndex = 0;
			for (int i = 0; i < n_actual; i++)
				if (bag.contains(i))
					revisedSamples[revisedIndex++] = samples[i];
			return revisedSamples;
		}

		throw new IllegalStateException("That should not happen!");
	}

	final static private double[][] sampleOrthogonal(int dim, int subdivisions,
			int samplesPerSubspace) {

		final int m = (int) (samplesPerSubspace * Math.pow(subdivisions,
				dim - 1));

		final int indexVectors[][][] = new int[dim][subdivisions][m];
		for (int i = 0; i < dim; i++)
			for (int j = 0; j < subdivisions; j++)
				indexVectors[i][j] = randperm(m);

		final int Ns = (int) Math.pow(subdivisions, dim);
		int sampleIndex = 0;
		final double[][] samples = new double[subdivisions * m][];

		while (sampleIndex < samples.length) {

			for (int j = 0; j < Ns; j++) {
				final int[] pj = new int[dim];
				for (int i0 = 0; i0 < dim; i0++)
					pj[i0] = (int) (Math
							.floor((j) / Math.pow(subdivisions, i0)) % subdivisions);

				final int[] s = new int[dim];
				for (int i = 0; i < dim; i++) {
					s[i] = indexVectors[i][pj[i]][0];
					for (int i0 = 1; i0 < indexVectors[i][pj[i]].length; i0++)
						indexVectors[i][pj[i]][i0 - 1] = indexVectors[i][pj[i]][i0];
					indexVectors[i][pj[i]][indexVectors[i][pj[i]].length - 1] = -1;
				}

				samples[sampleIndex] = new double[dim];
				for (int i = 0; i < dim; i++)
					samples[sampleIndex][i] = s[i] + pj[i] * m;
				sampleIndex++;
			}
		}

		return samples;
	}

	final static private int[] randperm(final int n) {
		final ArrayList<Integer> bag = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++)
			bag.add(i);
		final int[] perm = new int[n];
		for (int i = 0; i < n; i++)
			perm[i] = bag.remove(rand.nextInt(bag.size()));
		return perm;
	}

}
