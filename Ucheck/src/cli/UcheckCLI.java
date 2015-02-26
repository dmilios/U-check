package cli;

import gpoMC.LFFOptions;
import gpoMC.LearnFromFormulae;
import gpoMC.ObservationsFile;
import gpoptim.GpoResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import config.Configuration;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import biopepa.BiopepaFile;
import parsers.MitlFactory;
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

		try {

			final Configuration contents = new Configuration(log);
			contents.load(new FileInputStream(optionfile));

			final String mode = contents.getMode();
			if (mode.equals("inference"))
				performInference(contents, log);
			if (mode.equals("smoothedmc"))
				performSmoothedMC(contents, log);

		} catch (FileNotFoundException e) {
			log.printError(e.getMessage());
		} catch (IOException e) {
			log.printError(e.getMessage());
		}

		if (log.getWarnings() > 0)
			log.println("\nTotal warnings: " + log.getWarnings());
		if (log.getErrors() > 0) {
			log.println("\nOperation could not be completed due to errors!");
			log.println("Total errors: " + log.getErrors());
		}
	}

	@SuppressWarnings("deprecation")
	public static void performInference(Configuration contents, Log log)
			throws IOException {
		try {

			final String modelFile = contents.getModel();
			final String mitlFile = contents.getProperties();
			final String observationsFile = contents.getObservations();
			final LFFOptions lffOptions = contents.getLFFOptions();
			final gpoMC.Parameter[] params = contents.getLFFParameters();
			
			if (log.getErrors() > 0)
				return;

			final BiopepaFile biopepa = new BiopepaFile(modelFile);
			final String mitlText = readFile(mitlFile);
			final String obsText = readFile(observationsFile);
			final CTMCModel model = biopepa.getModel();

			MitlFactory factory = new MitlFactory(model.getStateVariables());
			MitlPropertiesList l = factory.constructProperties(mitlText);
			ArrayList<MiTL> list = l.getProperties();
			MiTL[] formulae = new MiTL[list.size()];
			list.toArray(formulae);

			ObservationsFile obs = new ObservationsFile();
			final boolean[][] observations = obs.load(obsText, formulae.length);

			LearnFromFormulae lff = new LearnFromFormulae();
			lff.setModel(model);
			lff.setParams(params);
			lff.setOptions(lffOptions);
			lff.setBiopepa(biopepa);
			lff.setMitlText(mitlText);

			GpoResult result = lff.performInference(formulae, observations);
			log.println(result.toString());

		} catch (FileNotFoundException e) {
			log.printError(e.getMessage());
		}
	}

	public static void performSmoothedMC(Configuration contents, Log log) {
	}

	private static final String readFile(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(filename);
		final byte[] fileData = new byte[input.available()];
		input.read(fileData);
		input.close();
		return new String(fileData);
	}

}
