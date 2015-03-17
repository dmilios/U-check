package testsTACAS;

import gp.GpDataset;
import gp.classification.ProbitRegressionPosterior;

import java.io.File;
import java.io.IOException;

import smoothedMC.SmmcOptions;
import smoothedMC.Parameter;
import smoothedMC.SmmcUtils;
import smoothedMC.SmoothedModelCheker;
import smoothedMC.gridSampling.RegularSampler;
import biopepa.BiopepaFile;

public class Lacz_debug {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Parameter k2 = new Parameter("k2", 1000, 5000);
		Parameter k7 = new Parameter("k7", 45, 450);

		// as in "expression burst" experiment
		// k2 = new Parameter("k2", 1000, 10000);
		// k7 = new Parameter("k7", 45, 450);

		Parameter[] par_k2k7 = new Parameter[] { k2, k7 };

		final String file = "models/lacz.biopepa";

		POINTS_SAMPLED = 256;
		SMCExtra.MOVING_WINDOW_HALF_SIZE = 4;

		// smmc(file, "burst", burstFormula, par_k2k7, 21000);
		smmc(file, "movavg", movavgFormula, par_k2k7, 21000);

		// SIMULATION_RUNS = 1000;
		// stmc(file, "movavg", movavgFormula, par_k2k7, 21000);
	}

	static int SIMULATION_RUNS = 10;
	static int POINTS_IN_SMMC_GRID = 100;
	static int POINTS_SAMPLED = 256;

	// protected final static String movavgFormula = "G[15000, 20000] "
	// +
	// "((-0.1*LacZ_mavg) <= LacZ-LacZ_mavg & LacZ-LacZ_mavg <= 0.1*LacZ_mavg)\n";

	// protected final static String movvarFormula =
	// "G[15000, 20000] (LacZ_mvar <= 0.1 & F<=5000 LacZ > 10) \n";

	protected final static String movavgFormula = "" // (F[15000, 21000] LacZ > 5) &
			+ "G[15000, 21000] "
			+ "((-0.1*LacZ_mavg) <= LacZ-LacZ_mavg & LacZ-LacZ_mavg <= 0.1*LacZ_mavg) \n";

	protected final static String movvarFormula = ""
			+ "(F<=1000 LacZ > 0) U[15000, 20000] LacZ_mvar <= 0.1 \n";

	protected final static String burstFormula = "F[16000, 21000] "
			+ "( LacZ_d > 0 & G[10, 2000] LacZ_d <= 0 )\n";

	/**
	 * Smoothed model checking
	 * 
	 * @param modelFile
	 * @param experimentName
	 *            for bookkeeping
	 * @param mitlText
	 *            the MiTL formula. Important: has to end with '\n'
	 * @param parameters
	 * @param tFinal
	 * @param withMeans
	 *            If true, then mean information will be calculated; otherwise,
	 *            derivative information will be calculated instead.
	 */
	public static final void smmc(String modelFile, String experimentName,
			String mitlText, Parameter[] parameters, double tFinal)
			throws IOException {
		BiopepaFile biopepaFile = new BiopepaFile(modelFile);

		final SmmcOptions options = new SmmcOptions();
		options.setN(POINTS_IN_SMMC_GRID);
		options.setNumberOfTestPoints(POINTS_SAMPLED);
		options.setSimulationRuns(SIMULATION_RUNS);
		options.setSimulationEndTime(tFinal);
		options.setSampler(new RegularSampler());
		options.setDebugEnabled(true);

		options.setTimeseriesEnabled(true);
		options.setSimulationTimepoints(200);

		options.setKernelGP(SMCExtra.defaultKernelRBF(parameters, true));
		options.setHyperparamOptimisation(true);
		options.setHyperparamOptimisationRestarts(0);

		// options.setKernelGP(new KernelRbfARD(new double[] { 2.38, 0.02,
		// 0.02 }));

		final String[] path = modelFile.split(File.separator);
		final String modelName = path[path.length - 1].split("\\.")[0];
		/** info about the model, the experiment name and the parameters */
		String filePrefix = modelName + "_" + experimentName + "_";
		for (int i = 0; i < parameters.length; i++)
			filePrefix += parameters[i].getName();

		System.out.println("Property: ");
		System.out.println(mitlText);
		System.out.print("Parameters: ");
		System.out.print(parameters[0].getName());
		for (int i = 1; i < parameters.length; i++)
			System.out.print(", " + parameters[i].getName());
		System.out.println("\nSimulation end time: "
				+ options.getSimulationEndTime());

		System.out.println("Method: Smoothed MC");
		System.out.println("SMC datapoints: " + options.getN());
		System.out.println("Simulation runs: " + options.getSimulationRuns());
		System.out.println("Smoothed MC datapoints: " + options.getNumberOfTestPoints());
		System.out.println(" - - - ");

		// generate a GP dataset that contains observations produced via
		// statistical MC
		final GpDataset data;
		data = SMCExtra.enhancedStatisticalMC(biopepaFile, mitlText,
				parameters, options);

		SMCExtra.binomial2matlab(data, options.getSimulationRuns(), filePrefix
				+ "_smmcDATA");

		// given the observations, do the smoothed MC
		SmoothedModelCheker smmc = new SmoothedModelCheker();
		ProbitRegressionPosterior post = smmc.performSmoothedModelChecking(data,
				parameters, options);

		SmmcUtils.results2matlab(post, filePrefix + "_smmc");
		System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - ");
		System.out.println();
	}

	/**
	 * Statistical model checking
	 * 
	 * @param modelFile
	 * @param experimentName
	 *            for bookkeeping
	 * @param mitlText
	 *            the MiTL formula. Important: has to end with '\n'
	 * @param parameters
	 * @param tFinal
	 * @param withMeans
	 *            If true, then mean information will be calculated; otherwise,
	 *            derivative information will be calculated instead.
	 */
	public static final void stmc(String modelFile, String experimentName,
			String mitlText, Parameter[] parameters, double tFinal)
			throws IOException {
		BiopepaFile biopepaFile = new BiopepaFile(modelFile);

		final SmmcOptions options = new SmmcOptions();
		options.setN(POINTS_SAMPLED);
		options.setSimulationRuns(SIMULATION_RUNS);
		options.setSimulationEndTime(tFinal);
		options.setSampler(new RegularSampler());
		options.setDebugEnabled(true);

		options.setTimeseriesEnabled(true);
		options.setSimulationTimepoints(200);

		final String[] path = modelFile.split(File.separator);
		final String modelName = path[path.length - 1].split("\\.")[0];
		/** info about the model, the experiment name and the parameters */
		String filePrefix = modelName + "_" + experimentName + "_";
		for (int i = 0; i < parameters.length; i++)
			filePrefix += parameters[i].getName();

		System.out.println("Property: ");
		System.out.println(mitlText);
		System.out.print("Parameters: ");
		System.out.print(parameters[0].getName());
		for (int i = 1; i < parameters.length; i++)
			System.out.print(", " + parameters[i].getName());
		System.out.println("\nSimulation end time: "
				+ options.getSimulationEndTime());

		System.out.println("Method: Statistical MC");
		System.out.println("SMC datapoints: " + options.getN());
		System.out.println("Simulation runs: " + options.getSimulationRuns());
		System.out.println(" - - - ");

		final GpDataset deepResults;
		deepResults = SMCExtra.enhancedStatisticalMC(biopepaFile, mitlText,
				parameters, options);

		SMCExtra.binomial2matlab(deepResults, options.getSimulationRuns(),
				filePrefix + "_deepSMC");
		System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - ");
		System.out.println();
	}

}
