package testsTACAS;

import gp.GpDataset;
import gp.classification.ProbitRegressionPosterior;

import java.io.File;
import java.io.IOException;

import linalg.NonPosDefMatrixException;
import smoothedMC.SmmcOptions;
import smoothedMC.Parameter;
import smoothedMC.SmmcUtils;
import smoothedMC.SmoothedModelCheker;
import smoothedMC.gridSampling.RegularSampler;
import biopepa.BiopepaFile;

public class SIR_TACAS {

	/**
	 * @param args
	 * @throws IOException
	 * @throws NonPosDefMatrixException
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, NonPosDefMatrixException {

		final Parameter ki = new Parameter("ki", 0.005, 0.3);
		final Parameter kr = new Parameter("kr", 0.005, 0.2);

		Parameter[] par_ki = new Parameter[] { ki };
		Parameter[] par_kr = new Parameter[] { kr };
		Parameter[] par_kikr = new Parameter[] { ki, kr };

		final String file = "models/SIR.biopepa";

		// stmc(file, "extinction", extinction, par_ki, 120, false);
		// smmc(file, "extinction", extinction, par_ki, 120, false);
		// stmc(file, "extinction", extinction, par_kr, 120, false);
		// smmc(file, "extinction", extinction, par_kr, 120, false);
		// stmc(file, "extinction", extinction, par_kikr, 120, false);
		smmc(file, "extinction", extinction, par_kikr, 120, false);

		// SIMULATION_RUNS = 1000;
		// stmc(file, "extinction", extinction, par_kikr, 120, false);
	}

	static int SIMULATION_RUNS = 50;
	static int POINTS_IN_SMMC_GRID = 36;
	static int POINTS_SAMPLED = 256;

	protected final static String extinction = "F[100, 120] I=0  &  G[0, 100] I>0\n";

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
	 * @throws NonPosDefMatrixException 
	 */
	public static final void smmc(String modelFile, String experimentName,
			String mitlText, Parameter[] parameters, double tFinal,
			boolean withMeans) throws IOException, NonPosDefMatrixException {
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

		options.setKernelGP(SMCExtra_OLD.defaultKernelRBF(parameters, true));
		options.setHyperparamOptimisation(true);
		options.setHyperparamOptimisationRestarts(0);

		//options.setKernelGP(new KernelRbfARD(new double[] { 2.43, 0.0295,
			//	0.0017 }));

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
		if (withMeans)
			data = SMCExtra_OLD.statisticalMCwithMeans(biopepaFile, mitlText,
					parameters, options);
		else
			data = SMCExtra_OLD.statisticalMCwithDerivatives(biopepaFile, mitlText,
					parameters, options);

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
			String mitlText, Parameter[] parameters, double tFinal,
			boolean withMeans) throws IOException {
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
		if (withMeans)
			deepResults = SMCExtra_OLD.statisticalMCwithMeans(biopepaFile,
					mitlText, parameters, options);
		else
			deepResults = SMCExtra_OLD.statisticalMCwithDerivatives(biopepaFile,
					mitlText, parameters, options);
		SMCExtra_OLD.binomial2matlab(deepResults, options.getSimulationRuns(),
				filePrefix + "_deepSMC");
		System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - ");
		System.out.println();
	}

}
