package tests;

import java.io.FileWriter;
import java.io.IOException;

import mitl.MiTL;
import model.Trajectory;
import parsers.MitlFactory;
import rand.ApacheMT;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import biopepa.BiopepaFile;

public class TestSSA {

	public static void main(String[] args) throws IOException {
		compare_traj_timeseries();
	}

	public static void compare_traj_timeseries() throws IOException {
		final String modelFile = "models/SIR.biopepa";
		final String mitlText = "(F[100,120] I = 0) & ( G<=100 I > 0 )\n";

		BiopepaFile bio = new BiopepaFile(modelFile);
		CTMCModel model = bio.getModel();
		StochasticSimulationAlgorithm ssa = new GillespieSSA(model);
		
		long seed = 7642325;
		seed = (long) (Math.random() * 10000000);
		System.out.println("seed: " + seed);

		ssa.setRandomEngine(new ApacheMT(seed));
		Trajectory x = ssa.generateTrajectory(0, 200);

		ssa.setRandomEngine(new ApacheMT(seed));
		Trajectory y = ssa.generateTimeseries(0, 200, 100);

		MitlFactory factory = new MitlFactory(model.getStateVariables());
		MiTL mtl = factory.constructProperties(mitlText).getProperties().get(0);

		FileWriter fw;

		System.out.println("Trajectory:");
		System.out.println("-----------");
		System.out.println(x);
		System.out.println(mtl);
		System.out.println(mtl.evaluate(x, 0));
		fw = new FileWriter("src/tests/SIR_trajectory.csv");
		fw.write(x.toCSV());
		fw.close();

		System.out.println("\n");

		System.out.println("Timeseries:");
		System.out.println("-----------");
		System.out.println(y);
		System.out.println(mtl);
		System.out.println(mtl.evaluate(y, 0));
		fw = new FileWriter("src/tests/SIR_timeseries.csv");
		fw.write(y.toCSV());
		fw.close();
	}

}
