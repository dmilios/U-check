package tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import parsers.MitlFactory;
import biopepa.BiopepaFile;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import model.Trajectory;

public class ObservationsGenerator {

	private int simulationRuns = 20;

	public void setSimulationRuns(int simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	public boolean[][] generate(CTMCModel model, MiTL[] formulae, double tfinal) {
		final boolean[][] data = new boolean[simulationRuns][formulae.length];
		final StochasticSimulationAlgorithm ssa = new GillespieSSA(model);
		for (int run = 0; run < simulationRuns; run++) {
			final Trajectory x = ssa.generateTrajectory(0, tfinal);
			for (int f = 0; f < formulae.length; f++)
				data[run][f] = formulae[f].evaluate(x, 0);
		}
		return data;
	}

	public static void main(String[] args) throws IOException {

		String model = "../gpoMC/models/rumour.biopepa";
		String properties = "../gpoMC/formulae/rumour.mtl";
		double tfinal = 5;
		int runs = 100;

		if (args.length >= 1)
			model = args[0];
		if (args.length >= 2)
			properties = args[1];
		if (args.length >= 3)
			tfinal = Double.parseDouble(args[2]);
		if (args.length >= 4)
			runs = Integer.parseInt(args[3]);

		ObservationsGenerator gen = new ObservationsGenerator();
		gen.setSimulationRuns(runs);

		final CTMCModel ctmc = (new BiopepaFile(model)).getModel();
		final String mitlText = readFile(properties);
		MitlFactory factory = new MitlFactory(ctmc.getStateVariables());
		MitlPropertiesList l = factory.constructProperties(mitlText);
		ArrayList<MiTL> list = l.getProperties();
		MiTL[] formulae = new MiTL[list.size()];
		list.toArray(formulae);

		boolean[][] data = gen.generate(ctmc, formulae, tfinal);
		final int idx = properties.indexOf(".mtl");
		final String datfile = properties.substring(0, idx) + ".dat";
		savetofile(datfile, data);
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

	private static final String readFile(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(filename);
		final byte[] fileData = new byte[input.available()];
		input.read(fileData);
		input.close();

		return new String(fileData);
	}

}
