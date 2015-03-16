package ucheck;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public final class DoubleMatrixFile {

	synchronized public double[][] load(InputStream istream, int n)
			throws IOException, NumberFormatException {
		final byte[] fileData = new byte[istream.available()];
		istream.read(fileData);
		return load(new String(fileData), n);
	}

	/**
	 * @param text
	 *            representation of a matrix
	 * @param n
	 *            the number of columns
	 */
	final public double[][] load(String text, int n)
			throws NumberFormatException {
		final String[] lines = text.split("\\n");
		final ArrayList<double[]> valuesList = new ArrayList<double[]>();

		int counter = 0;
		for (String line : lines) {
			counter++;
			if (line.trim().isEmpty())
				continue;
			final String[] words = line.trim().split("\\W+");
			if (words.length != n)
				throw new NumberFormatException(
						"Wrong number of columns at line " + counter);
			final double[] vals = new double[n];
			for (int i = 0; i < n; i++)
				vals[i] = Double.parseDouble(words[i]);
			valuesList.add(vals);
		}

		final double[][] values = new double[valuesList.size()][];
		valuesList.toArray(values);
		return values;
	}

}
