package tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import mitl.MiTL;
import mitl.MitlPropertiesList;
import parsers.MitlFactory;
import biopepa.BiopepaFile;
import simhya.matlab.SimHyAModel;
import simhya.model.flat.parser.TokenMgrError;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import ssa.Trajectory;
import ucheck.methods.UcheckModel;

public class TestMC {

	public static void main(String[] args) throws Exception {
		test_extinction_formula();
		// compare_row_sums();
	}

	public static void test_extinction_formula() throws Exception {

		// CTMCModel and SimHyA give same results for params: [1.1, 0.8]
		// Note: for params: [1.1, 0.8], same results only for trajectory
		// (timeseries seems to be less accurate for CTMCModel; i.e. jittering)

		String[] mitl = { "ignorants", "rGreaterThanS", "peakAndLow" };
		double tf = 5;
		int runs = 1000;
		String[] params = { "k_s", "k_r" };
		double[] values = { 1.1, 0.8 };
		// double[] values = { 0.5, 0.1 };

		UcheckModel model1 = new UcheckModel();
		model1.loadModel("models/RUMORS.txt");
		model1.loadSMCformulae("formulae/rumour.mtl");
		model1.setSSA();
		model1.setParameters(params, values);

		SimHyAModel model2 = new SimHyAModel();
		model2.loadModel("models/RUMORS.txt");
		model2.loadSMCformulae("formulae/my_rumors_prop.txt");
		model2.setSSA();
		model2.setParameters(params, values);

		BiopepaFile bio = new BiopepaFile("models/rumour.biopepa");
		CTMCModel model3 = bio.getModel();
		model3 = bio.getModel(params, values);
		String mitlText = readFile("formulae/rumour.mtl");
		MitlFactory factory = new MitlFactory(model3.getStateVariables());
		MitlPropertiesList l = factory.constructProperties(mitlText);
		ArrayList<MiTL> list = l.getProperties();
		MiTL[] formulae = new MiTL[list.size()];
		list.toArray(formulae);

		boolean[][] obs;

		obs = model1.performSMC(mitl, tf, runs);
		System.out.println(model1.getClass().getSimpleName());
		System.out.println(Arrays.toString(rowsums(obs)));

		System.out.println();

		obs = model2.smc_set(mitl, tf, runs);
		System.out.println(model2.getClass().getSimpleName());
		System.out.println(Arrays.toString(rowsums(obs)));

		System.out.println();

		obs = smc(model3, formulae, tf, runs);
		System.out.println(model3.getClass().getSimpleName());
		System.out.println(Arrays.toString(rowsums(obs)));

		// model2.plotTrajectory();
	}

	public static void compare_row_sums() throws TokenMgrError, Exception {
		UcheckModel model1 = new UcheckModel();
		model1.loadModel("models/RUMORS.txt");
		model1.loadSMCformulae("formulae/rumour.mtl");
		model1.setSSA();

		SimHyAModel model2 = new SimHyAModel();
		model2.loadModel("models/RUMORS.txt");
		model2.loadSMCformulae("formulae/my_rumors_prop.txt");
		model2.setSSA();

		String[] mitl = new String[] { "ignorants", "rGreaterThanS",
				"peakAndLow" };
		double tf = 5;
		int runs = 1000;
		boolean[][] obs;

		obs = model1.performSMC(mitl, tf, runs);
		System.out.println(model1.getClass().getSimpleName());
		System.out.println(Arrays.toString(rowsums(obs)));

		System.out.println();

		obs = model2.smc_set(mitl, tf, runs);
		System.out.println(model2.getClass().getSimpleName());
		System.out.println(Arrays.toString(rowsums(obs)));
	}

	private static final double[] rowsums(final boolean[][] obs) {
		final int m = obs[0].length;
		final double[] counts = new double[m];
		for (int i = 0; i < obs.length; i++)
			for (int j = 0; j < m; j++)
				if (obs[i][j])
					counts[j]++;
		for (int j = 0; j < m; j++)
			counts[j] /= obs.length;
		return counts;
	}

	private static final String readFile(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(filename);
		final byte[] fileData = new byte[input.available()];
		input.read(fileData);
		input.close();
		return new String(fileData);
	}

	private static final boolean[][] smc(CTMCModel model, MiTL[] phi,
			double tf, int runs) {
		final StochasticSimulationAlgorithm ssa = new GillespieSSA(model);
		final int timepoints = 1000;
		final int m = phi.length;
		final boolean[][] results = new boolean[runs][m];
		for (int run = 0; run < runs; run++) {
			final Trajectory x = ssa.generateTimeseries(0, tf, timepoints + 1);
			// final Trajectory x = ssa.generateTrajectory(0, tf);
			for (int j = 0; j < m; j++)
				results[run][j] = phi[j].evaluate(x, 0);
		}
		return results;
	}

}
