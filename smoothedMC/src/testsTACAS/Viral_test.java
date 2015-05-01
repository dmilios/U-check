package testsTACAS;

import gp.GpDataset;
import gp.classification.ProbitRegressionPosterior;
import gp.kernels.KernelRbfARD;

import java.io.File;

import model.ModelInterface;
import modelChecking.MitlModelChecker;
import smoothedMC.Parameter;
import smoothedMC.SmmcOptions;
import smoothedMC.SmmcUtils;
import smoothedMC.SmoothedModelCheker;
import smoothedMC.gridSampling.RegularSampler;
import biopepa.BiopepaModel;

public class Viral_test {

	static int SIMULATION_RUNS = 2;
	static int POINTS_IN_SMMC_GRID = 9;
	static int POINTS_SAMPLED = 9;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Parameter k1 = new Parameter("k1", 0.2, 2);
		Parameter k3 = new Parameter("k3", 100, 1500);

		Parameter[] par_k1k3 = new Parameter[] { k1, k3 };

		final String file = "models/viral.biopepa";
		smmc(file, "", formula, par_k1k3, 200);

		// SIMULATION_RUNS = 1000;
		stmc(file, "", formula, par_k1k3, 200);
	}

	protected final static String formula = ""
			+ "(G<=50 Genome < 10) & (F[50, 200] Genome > 100)\n";

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
	 * @throws Exception
	 */
	public static final void smmc(String modelFile, String experimentName,
			String mitlText, Parameter[] parameters, double tFinal)
			throws Exception {

		final SmmcOptions options = new SmmcOptions();
		options.setN(POINTS_IN_SMMC_GRID);
		options.setNumberOfTestPoints(POINTS_SAMPLED);
		options.setSimulationRuns(SIMULATION_RUNS);
		options.setSimulationEndTime(tFinal);
		options.setSampler(new RegularSampler());
		options.setDebugEnabled(true);

		options.setTimeseriesEnabled(true);
		options.setSimulationTimepoints(1000);

		options.setKernelGP(new KernelRbfARD(parameters.length));
		options.setUseDefaultHyperparams(true);
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
		System.out.println("Smoothed MC datapoints: "
				+ options.getNumberOfTestPoints());
		System.out.println(" - - - ");

		ModelInterface model = new BiopepaModel();
		model.loadModel(modelFile);
		SmoothedModelCheker smc = new SmoothedModelCheker();
		MitlModelChecker mc = new MitlModelChecker(model);
		mc.setProperties(mitlText);

		// generate a GP dataset that contains observations produced via
		// statistical MC
		final GpDataset data;
		long t0;
		double elapsed;
		t0 = System.currentTimeMillis();
		data = smc.performStatisticalModelChecking(mc, parameters, options);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		System.out.println("Statistical Model Checking:  " + elapsed + " sec");
		
		SMCExtra.binomial2matlab(data, options.getSimulationRuns(), filePrefix
				+ "_smmcDATA");

		// given the observations, do the smoothed MC
		ProbitRegressionPosterior post = smc.performSmoothedModelChecking(data,
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
	 * @throws Exception
	 */
	public static final void stmc(String modelFile, String experimentName,
			String mitlText, Parameter[] parameters, double tFinal)
			throws Exception {

		final SmmcOptions options = new SmmcOptions();
		options.setN(POINTS_SAMPLED);
		options.setSimulationRuns(SIMULATION_RUNS);
		options.setSimulationEndTime(tFinal);
		options.setSampler(new RegularSampler());
		options.setDebugEnabled(true);

		options.setTimeseriesEnabled(true);
		options.setSimulationTimepoints(1000);

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

		ModelInterface model = new BiopepaModel();
		model.loadModel(modelFile);
		SmoothedModelCheker smc = new SmoothedModelCheker();
		MitlModelChecker mc = new MitlModelChecker(model);
		mc.setProperties(mitlText);
		final GpDataset deepResults;
		long t0;
		double elapsed;
		t0 = System.currentTimeMillis();
		deepResults = smc.performStatisticalModelChecking(mc, parameters,
				options);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		System.out.println("Statistical Model Checking:  " + elapsed + " sec");

		SMCExtra.binomial2matlab(deepResults, options.getSimulationRuns(),
				filePrefix + "_deepSMC");
		System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - ");
		System.out.println();
	}

}
