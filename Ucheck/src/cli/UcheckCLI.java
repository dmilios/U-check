package cli;

import gp.classification.ClassificationPosterior;
import gpoMC.LFFOptions;
import gpoMC.LearnFromFormulae;
import gpoptim.GpoResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import config.Configuration;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import biopepa.BiopepaFile;
import parsers.MitlFactory;
import priors.Prior;
import smoothedMC.SmmcOptions;
import smoothedMC.SmmcUtils;
import smoothedMC.SmoothedModelCheker;
import ssa.CTMCModel;

public class UcheckCLI {

	public static void main(String[] args) {

		final Log log = new PrintStreamLog(System.out);

		String title = "Uncertain Model Checking Tool";
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
		Configuration config = null;
		try {
			fstream = new FileInputStream(optionfile);
			config = new Configuration(log);
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

	@SuppressWarnings("deprecation")
	public static void performInference(Configuration config, Log log) {

		final BiopepaFile biopepa = config.getBiopepaModel();
		final String mitlText = config.getMitlText();
		final boolean[][] observations = config.getObservations();
		final LFFOptions lffOptions = config.getLFFOptions();
		final gpoMC.Parameter[] params = config.getLFFParameters();
		final Prior[] priors = config.getLFFPriors();

		final CTMCModel model = biopepa.getModel();

		MitlFactory factory = new MitlFactory(model.getStateVariables());
		MitlPropertiesList l = factory.constructProperties(mitlText);
		ArrayList<MiTL> list = l.getProperties();
		MiTL[] formulae = new MiTL[list.size()];
		list.toArray(formulae);

		LearnFromFormulae lff = new LearnFromFormulae();
		lff.setModel(model);
		lff.setParams(params);
		lff.setPriors(priors);
		lff.setOptions(lffOptions);
		lff.setBiopepa(biopepa);
		lff.setMitlText(mitlText);
		
		// lff.setPriors(null);

		GpoResult result = lff.performInference(formulae, observations);
		log.println(result.toString());
	}

	public static void performSmoothedMC(Configuration config, Log log) {
		final BiopepaFile biopepa = config.getBiopepaModel();
		final String mitlText = config.getMitlText();
		final SmmcOptions options = config.getSmMCOptions();
		final smoothedMC.Parameter[] params = config.getSmMCParameters();

		final SmoothedModelCheker smmc = new SmoothedModelCheker();
		final ClassificationPosterior result = smmc
				.performSmoothedModelChecking(biopepa, mitlText, params,
						options);

		log.println("# Smoothed Model Checking --- Results");
		log.println("Time for Statistical MC: "
				+ smmc.getStatisticalMCTimeElapsed() + " sec");
		log.println("Time for hyperparam opt: "
				+ smmc.getHyperparamOptimTimeElapsed() + " sec");
		log.println("Time for Smoothed MC: " + smmc.getSmoothedMCTimeElapsed()
				+ " sec");
		log.println("Hyperparams used: "
				+ Arrays.toString(smmc.getHyperparamsUsed()));

		// System.out.println("\n" + SmmcUtils.results2csv(result, 2));
	}

}
