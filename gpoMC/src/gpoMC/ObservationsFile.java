package gpoMC;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public final class ObservationsFile {

	synchronized public boolean[][] load(InputStream istream, int n)
			throws IOException {
		final byte[] fileData = new byte[istream.available()];
		istream.read(fileData);
		return load(new String(fileData), n);
	}

	/**
	 * @param text
	 *            representation of the observations as a matrix
	 * @param n
	 *            the number of formulae (and the columns expected in the
	 *            matrix)
	 */
	final public boolean[][] load(String text, int n) {
		final String[] lines = text.split("\\n");
		final ArrayList<boolean[]> observationsList = new ArrayList<boolean[]>();

		for (String line : lines) {
			if (line.trim().isEmpty())
				continue;
			final String[] words = line.trim().split("\\W+");
			if (words.length != n) {
				throw new IllegalArgumentException();
			}

			final boolean[] obs = new boolean[n];
			for (int i = 0; i < n; i++) {
				final String word = words[i];
				if (word.equals("1") || word.equalsIgnoreCase("true"))
					obs[i] = true;
				else if (word.equals("0") || word.equalsIgnoreCase("false"))
					obs[i] = false;
				else {
					throw new IllegalArgumentException();
				}
			}
			observationsList.add(obs);
		}

		final boolean[][] observations = new boolean[observationsList.size()][];
		observationsList.toArray(observations);
		return observations;
	}

}
