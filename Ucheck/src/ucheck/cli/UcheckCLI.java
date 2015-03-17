package ucheck.cli;

import gp.classification.ProbitRegressionPosterior;
import gpoptim.GpoResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import lff.LFFOptions;
import lff.LearnFromFormulae;
import modelChecking.MitlModelChecker;
import priors.Prior;
import smoothedMC.SmmcOptions;
import smoothedMC.SmmcUtils;
import smoothedMC.SmoothedModelCheker;
import ucheck.config.UcheckConfig;

public class UcheckCLI {

	public static void main(String[] args) {

		final Log log = new PrintStreamLog(System.out);

		String title = "U-check: Model checking tool for uncertain systems";
		String usage = "Usage:\n\tuncertainmc OPTIONFILE\n";
		String optionfilehelp = "\"OPTIONFILE\" is a file that contains "
				+ "all the experiment options.\n"
				+ "If not set, then a default option file will be print on screen.\n";

		log.println(title);
		log.println();

		if (args.length == 0) {
			log.println(usage);
			log.println(optionfilehelp);
			return;
		}

		final String optionfile = args[0];
		FileInputStream fstream = null;
		UcheckConfig config = null;
		try {
			fstream = new FileInputStream(optionfile);
			config = new UcheckConfig(log);
			config.load(fstream);
			fstream.close();
		} catch (IOException e) {
			log.printError(e.getMessage());
		}

		if (config != null) {
			// all errors should be caught by this point
			if (log.getErrors() > 0)
				return;

			final String mode = config.getMode();
			if (mode.equals("inference"))
				performInference(config, log);
			if (mode.equals("robust"))
				performRobustSystemDesign(config, log);
			if (mode.equals("smoothedmc"))
				performSmoothedMC(config, log);
		}

		if (log.getWarnings() > 0)
			log.println("\nTotal warnings: " + log.getWarnings());
		if (log.getErrors() > 0) {
			log.println("\nOperation could not be completed due to errors!");
			log.println("Total errors: " + log.getErrors());
		}
	}

	public static void performInference(UcheckConfig config, Log log) {
		final MitlModelChecker modelChecker = config.getModelChecker();
		final boolean[][] observations = config.getObservations();
		final LFFOptions lffOptions = config.getLFFOptions();
		final lff.Parameter[] params = config.getLFFParameters();
		final Prior[] priors = config.getLFFPriors();

		LearnFromFormulae lff = new LearnFromFormulae(modelChecker);
		lff.setParams(params);
		lff.setPriors(priors);
		lff.setOptions(lffOptions);
		GpoResult result = lff.performInference(observations);
		printInferenceResults(log, config, result);
	}

	public static void performRobustSystemDesign(UcheckConfig config, Log log) {
		final MitlModelChecker modelChecker = config.getModelChecker();
		final LFFOptions lffOptions = config.getLFFOptions();
		final lff.Parameter[] params = config.getLFFParameters();

		LearnFromFormulae lff = new LearnFromFormulae(modelChecker);
		lff.setParams(params);
		lff.setOptions(lffOptions);
		GpoResult result = lff.robustSystemDesign();
		printRobustnessResults(log, config, result);
	}

	public static void performSmoothedMC(UcheckConfig config, Log log) {

		final File outDir = new File(config.getOutputDir());
		if (!outDir.exists())
			outDir.mkdirs();
		if (!outDir.canWrite() || !outDir.isDirectory()) {
			log.printError("Cannot write into the \"" + outDir
					+ "\" directory!");
			return;
		}

		final MitlModelChecker check = config.getModelChecker();
		final SmmcOptions options = config.getSmMCOptions();
		final smoothedMC.Parameter[] params = config.getSmMCParameters();

		final SmoothedModelCheker smmc = new SmoothedModelCheker();
		final ProbitRegressionPosterior result = smmc
				.performSmoothedModelChecking(check, params, options);
		printSmmcResults(log, config, smmc, result);
	}

	private static void printInferenceResults(Log log, UcheckConfig config,
			GpoResult result) {
		final lff.Parameter[] params = config.getLFFParameters();
		log.println("# Inference from Qualitative Data");
		log.println("Model file: " + config.getModelFile());
		log.println("MiTL file: " + config.getMitlFile());
		log.println();
		log.print("Parameters explored: ");
		log.print(params[0].getName());
		for (int i = 1; i < params.length; i++)
			log.print(", " + params[i].getName());
		log.println();
		log.println();
		log.println(result.toString());
	}

	private static void printRobustnessResults(Log log, UcheckConfig config,
			GpoResult result) {
		final lff.Parameter[] params = config.getLFFParameters();
		log.println("# Robust Parameter Synthesis");
		log.println("Model file: " + config.getModelFile());
		log.println("MiTL file: " + config.getMitlFile());
		log.println();
		log.print("Parameters explored: ");
		log.print(params[0].getName());
		for (int i = 1; i < params.length; i++)
			log.print(", " + params[i].getName());
		log.println();
		log.println();
		log.println(result.toString());
	}

	private static void printSmmcResults(Log log, UcheckConfig config,
			SmoothedModelCheker smmc, ProbitRegressionPosterior result) {

		final smoothedMC.Parameter[] params = config.getSmMCParameters();
		final double smcElapsed = smmc.getStatisticalMCTimeElapsed();
		final double hypElapsed = smmc.getHyperparamOptimTimeElapsed();
		final double smmcElapsed = smmc.getSmoothedMCTimeElapsed();
		final double[] hyperparams = smmc.getHyperparamsUsed();
		log.println("# Smoothed Model Checking --- Results");
		log.println("Model file: " + config.getModelFile());
		log.println("MiTL file: " + config.getMitlFile());
		log.println();

		log.print("Parameters explored: ");
		log.print(params[0].getName());
		for (int i = 1; i < params.length; i++)
			log.print(", " + params[i].getName());
		log.println();
		log.println("Hyperparams used: ");
		log.println("      amplitude: " + hyperparams[0]);
		for (int i = 1; i < hyperparams.length; i++)
			log.println("    lengthscale: " + hyperparams[i]);
		log.println();

		log.println("Time for Statistical MC: " + smcElapsed + " sec");
		log.println("Time for hyperparam opt: " + hypElapsed + " sec");
		log.println("Time for Smoothed MC:    " + smmcElapsed + " sec");
		log.println();

		final String dir = config.getOutputDir();
		final String fullname = (new File(config.getModelFile())).getName();
		final String name = fullname.substring(0, fullname.lastIndexOf('.'));
		final String csv = dir + File.separator + name + ".csv";
		final String mfile = dir + File.separator + "load_" + name + ".m";

		final double beta = 2;
		final String resultStr = SmmcUtils.results2csv(result, beta);
		final String matlabStr = produceMatlabScript(params, name);

		FileWriter fw;
		try {
			fw = new FileWriter(csv);
			fw.write(resultStr);
			fw.close();
			log.println("Smoothed MC results have been successfully written to '"
					+ csv + "'");
		} catch (IOException e) {
			log.printError("Could not write to output file '" + csv + "'");
		}
		try {
			fw = new FileWriter(mfile);
			fw.write(matlabStr);
			fw.close();
			log.println("A MATLAB/Octave script has been successfully "
					+ "produced in '" + mfile + "'");
		} catch (IOException e) {
			log.printError("Could not write to output file '" + mfile + "'");
		}
	}

	static private String produceMatlabScript(smoothedMC.Parameter[] params,
			String name) {
		final int dimension = params.length;
		String str = "";
		str += "% ===== This file has been automatically produced by the\n";
		str += "% ===== U-check model checking tool for uncertain systems\n";
		str += "% \n";
		str += "%       It loads the Smoothed Model Checking results from '"
				+ name + ".csv'\n";
		str += "%       Parameters explored: " + params[0].getName();
		for (int i = 1; i < params.length; i++)
			str += ", " + params[i].getName();
		str += "\n\n";
		str += "if exist('OCTAVE_VERSION', 'builtin')\n";
		str += "\tdata = load('" + name + ".csv" + "');\n";
		str += "else\n";
		str += "\tdata = csvread('" + name + ".csv', 1);\n";
		str += "end\n";
		str += "\n";
		str += "% 'paramValues'     " + "is a Nx" + dimension
				+ " matrix (grid of values in the parameter space)\n";
		str += "% 'probabilities'   "
				+ "contains the estimated satisfaction probabilities\n";
		str += "% 'lowerConfBound'  "
				+ "Lower confidence bound for the satisfaction probabilities\n";
		str += "% 'upperConfBound'  "
				+ "Upper confidence bound for the satisfaction probabilities\n";
		str += "\n";
		str += "paramNames     = {" + "'" + params[0].getName() + "'";
		for (int i = 1; i < params.length; i++)
			str += ", '" + params[i].getName() + "'";
		str += "};\n";
		str += "paramValues    = data(:, 1:" + dimension + ");\n";
		str += "probabilities  = data(:, " + (dimension + 1) + ");\n";
		str += "lowerConfBound = data(:, " + (dimension + 2) + ");\n";
		str += "upperConfBound = data(:, " + (dimension + 3) + ");\n";

		return str;
	}

}
