package testsTACAS;

import gp.GpDataset;
import gp.classification.ClassificationPosterior;

import java.io.File;
import java.io.IOException;

import smoothedMC.SmMCOptions;
import smoothedMC.Parameter;
import smoothedMC.SmmcUtils;
import smoothedMC.SmoothedModelCheker;
import smoothedMC.gridSampling.RegularSampler;
import biopepa.BiopepaFile;

public class LacZ_TACAS {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		final Parameter k2 = new Parameter("k2", 10, 10000);
		final Parameter k7 = new Parameter("k7", 0.45, 450);
		final Parameter k2wide = new Parameter("k2", 10, 100000);
		final Parameter k7wide = new Parameter("k7", 0.45, 4500);

		Parameter[] par_k2 = new Parameter[] { k2 };
		Parameter[] par_k7 = new Parameter[] { k7 };
		Parameter[] par_k2w = new Parameter[] { k2wide };
		Parameter[] par_k7w = new Parameter[] { k7wide };
		Parameter[] par_k2k7 = new Parameter[] { k2, k7 };

		final String file = "models/lacz.biopepa";

		stmc(file, "variation", variationFormula, par_k2w, 21000, true);
		smmc(file, "variation", variationFormula, par_k2w, 21000, true);
		stmc(file, "variation", variationFormula, par_k7w, 21000, true);
		smmc(file, "variation", variationFormula, par_k7w, 21000, true);
		stmc(file, "variation", variationFormula, par_k2k7, 21000, true);
		smmc(file, "variation", variationFormula, par_k2k7, 21000, true);

		stmc(file, "expressionburst", burstFormula, par_k2, 21000, false);
		smmc(file, "expressionburst", burstFormula, par_k2, 21000, false);
		stmc(file, "expressionburst", burstFormula, par_k7, 21000, false);
		smmc(file, "expressionburst", burstFormula, par_k7, 21000, false);
		stmc(file, "expressionburst", burstFormula, par_k2k7, 21000, false);
		smmc(file, "expressionburst", burstFormula, par_k2k7, 21000, false);
	}

	protected final static String variationFormula = "F<=21000 "
			+ "( G<=5000 ((-0.1*LacZ_m) <= LacZ-LacZ_m "
			+ "& LacZ-LacZ_m <= 0.1*LacZ_m) )\n";

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
			String mitlText, Parameter[] parameters, double tFinal,
			boolean withMeans) throws IOException {
		BiopepaFile biopepaFile = new BiopepaFile(modelFile);

		final SmMCOptions options = new SmMCOptions();
		options.setN(25 * parameters.length * parameters.length);
		options.setM(100 * parameters.length * parameters.length);
		options.setSimulationRuns(10);
		options.setSimulationEndTime(tFinal);
		options.setSampler(new RegularSampler());
		options.setDebugEnabled(true);

		options.setTimeseriesEnabled(true);
		options.setSimulationTimepoints(200);

		options.setKernelGP(SMCExtra_OLD.defaultKernelRBF(parameters, true));
		options.setHyperparamOptimisation(true);
		options.setHyperparamOptimisationRestarts(3);

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
		System.out.println("Smoothed MC datapoints: " + options.getM());
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
		ClassificationPosterior post = smmc.performSmoothedModelChecking(data,
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

		final SmMCOptions options = new SmMCOptions();
		options.setN(25 * parameters.length * parameters.length);
		options.setSimulationRuns(100);
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
