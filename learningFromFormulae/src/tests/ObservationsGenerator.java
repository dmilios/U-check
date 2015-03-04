package tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import modelChecking.MitlModelChecker;
import biopepa.BiopepaModel;

public class ObservationsGenerator {

	public static void main(String[] args) throws Exception {

		String model = "../learningFromFormulae/models/rumour.biopepa";
		String properties = "../learningFromFormulae/formulae/rumour.mtl";
		double tfinal = 5;
		int runs = 100;
		boolean robust = false;

		if (args.length >= 1)
			model = args[0];
		if (args.length >= 2)
			properties = args[1];
		if (args.length >= 3)
			tfinal = Double.parseDouble(args[2]);
		if (args.length >= 4)
			runs = Integer.parseInt(args[3]);
		if (args.length >= 5)
			robust = Boolean.parseBoolean(args[4]);

		final int idx = properties.indexOf(".mtl");
		final String datfile;
		if (!robust) {
			datfile = properties.substring(0, idx) + ".dat";
			boolean[][] data = generate(model, properties, tfinal, runs);
			savetofile(datfile, data);
		} else {
			datfile = properties.substring(0, idx) + "_robust.dat";
			double[][] data = generateRobust(model, properties, tfinal, runs);
			savetofile(datfile, data);
		}
	}

	public static boolean[][] generate(String model, String properties,
			double stopTime, int runs) throws Exception {
		BiopepaModel biopepa = new BiopepaModel();
		biopepa.loadModel(model);
		MitlModelChecker modelChecker = new MitlModelChecker(biopepa);
		modelChecker.loadProperties(properties);
		return modelChecker.performMC(stopTime, runs);
	}

	public static double[][] generateRobust(String model, String properties,
			double stopTime, int runs) throws Exception {
		BiopepaModel biopepa = new BiopepaModel();
		biopepa.loadModel(model);
		MitlModelChecker modelChecker = new MitlModelChecker(biopepa);
		modelChecker.loadProperties(properties);
		return modelChecker.performMCRobust(stopTime, runs);
	}

	private static void savetofile(String file, boolean[][] data)
			throws FileNotFoundException {
		final PrintWriter pw = new PrintWriter(file);
		for (int i = 0; i < data.length; i++) {
			final int last_j = data[i].length - 1;
			for (int j = 0; j < last_j; j++) {
				pw.print(data[i][j] ? '1' : '0');
				pw.print(", ");
			}
			pw.print(data[i][last_j] ? '1' : '0');
			pw.println();
		}
		pw.close();
	}

	private static void savetofile(String file, double[][] data)
			throws FileNotFoundException {
		final PrintWriter pw = new PrintWriter(file);
		for (int i = 0; i < data.length; i++) {
			final int last_j = data[i].length - 1;
			for (int j = 0; j < last_j; j++) {
				pw.print(data[i][j]);
				pw.print(", ");
			}
			pw.print(data[i][last_j]);
			pw.println();
		}
		pw.close();
	}

}
