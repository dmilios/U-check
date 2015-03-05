package ucheck.cli;

import gp.classification.ClassificationPosterior;
import gpoptim.GpoResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

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
		String usage = "Usage:\n\tuncertainmc MODE OPTIONFILE\n";
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
		log.println(result.toString());
	}

	public static void performSmoothedMC(UcheckConfig config, Log log) {
		final MitlModelChecker check = config.getModelChecker();
		final SmmcOptions options = config.getSmMCOptions();
		final smoothedMC.Parameter[] params = config.getSmMCParameters();

		final SmoothedModelCheker smmc = new SmoothedModelCheker();
		final ClassificationPosterior result = smmc
				.performSmoothedModelChecking(check, params, options);

		final double smcElapsed = smmc.getStatisticalMCTimeElapsed();
		final double hypElapsed = smmc.getHyperparamOptimTimeElapsed();
		final double smmcElapsed = smmc.getSmoothedMCTimeElapsed();
		final double[] hyperparams = smmc.getHyperparamsUsed();
		log.println("# Smoothed Model Checking --- Results");
		log.println("Time for Statistical MC: " + smcElapsed + " sec");
		log.println("Time for hyperparam opt: " + hypElapsed + " sec");
		log.println("Time for Smoothed MC: " + smmcElapsed + " sec");
		log.println("Hyperparams used: " + Arrays.toString(hyperparams));

		System.out.println("\n" + SmmcUtils.results2csv(result, 2));
	}

}
