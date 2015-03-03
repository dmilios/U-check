package ucheck.config;

import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;
import gp.kernels.KernelRbfARD;
import gpoMC.LFFOptions;
import gpoMC.ObservationsFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import priors.ExponentialPrior;
import priors.GammaPrior;
import priors.GaussianPrior;
import priors.Prior;
import priors.UniformPrior;
import ucheck.methods.UcheckModel;
import simhya.model.flat.parser.FlatParser;
import simhya.model.flat.parser.ParseException;
import smoothedMC.SmmcOptions;
import ucheck.cli.Log;
import ucheck.cli.PrintStreamLog;

public class Config_SimHyA {

	private Map<String, PropertySpec> optionSpecs = new HashMap<String, PropertySpec>();
	private Map<String, Object> configOptions = new HashMap<String, Object>();

	private ArrayList<String> parameterNames = new ArrayList<String>();
	private Map<String, double[]> parameters = new HashMap<String, double[]>();
	private Map<String, Prior> priors = new HashMap<String, Prior>();

	private Log log;

	private UcheckModel model = new UcheckModel();
	private String[] formulae;
	private boolean[][] observations;

	public Config_SimHyA() {
		this(new PrintStreamLog(System.out));
	}

	public Config_SimHyA(Log log) {
		this.log = log;
		defineConfigurationOptions();
	}

	/**
	 * Loads all the configurations options from the specified InputStream.<br>
	 * Any errors or warnings will be reported in the log.
	 */
	synchronized public void load(InputStream istream) {
		Scanner scanner = new Scanner(istream);
		int lineCounter = 0;
		while (scanner.hasNext()) {
			lineCounter++;
			final String line = scanner.nextLine().trim();
			if (line.startsWith("#"))
				continue;

			final String split[] = line.split("=");
			if (split.length == 1 && split[0].isEmpty())
				continue;

			if (split.length != 2) {
				log.printError("Invalid assignment statement at line "
						+ lineCounter);
				continue;
			}

			final String option = split[0].trim();
			final String value = split[1].trim();

			final String[] optionSplit = option.split("\\s+");
			if (optionSplit.length == 1)
				readOption(option, value, lineCounter);
			if (optionSplit.length == 2)
				if (optionSplit[0].equals("parameter"))
					readParameter(optionSplit[1], value);
				else if (optionSplit[0].equals("prior"))
					readPrior(optionSplit[1], value);
				else
					log.printWarning("Congifuration option \"" + option
							+ "\" is undefined at line " + lineCounter);
		}
		scanner.close();
		verifyLoadedInformation();
	}

	/**
	 * Verified that the information loaded is valid. Any errors or warnings
	 * will be reported in the log.
	 */
	private void verifyLoadedInformation() {

		// verify model
		String modelFile = (String) configOptions.get("model");
		if (modelFile.startsWith("\""))
			modelFile = modelFile.substring(1);
		if (modelFile.endsWith("\""))
			modelFile = modelFile.substring(0, modelFile.length() - 1);

		try {
			FlatParser parser = new FlatParser();
			parser.parseFromFile(modelFile);
			model.loadModel(modelFile);
		} catch (ParseException e) {
			final String msg = e.getMessage();
			if (msg.contains("java.io.FileNotFoundException"))
				log.printError(modelFile + " (No such file or directory)");
			else
				log.printError(msg);
		}

		// verify MiTL file
		String mitlFile = (String) configOptions.get("propertyFile");
		if (mitlFile.startsWith("\""))
			mitlFile = mitlFile.substring(1);
		if (mitlFile.endsWith("\""))
			mitlFile = mitlFile.substring(0, mitlFile.length() - 1);

		try {
			model.loadSMCformulae(mitlFile);
		} catch (Exception e) {
			log.printError("Could not load property file " + mitlFile);
		}

		// verify property names
		final Object[] buffer = (Object[]) configOptions.get("properties");
		formulae = new String[buffer != null ? buffer.length : 0];
		if (buffer != null)
			System.arraycopy(buffer, 0, formulae, 0, buffer.length);
		if (formulae.length == 0)
			log.printError("Properties have not been properly defined!");

		else
			try {
				final double tfinal = 1e-8;
				model.smc_set(formulae, tfinal, 0);
			} catch (Exception e) {
				log.printError(e.getMessage());
			}

		// verify observations
		if (configOptions.get("mode").equals("inference")) {
			String obsFile = (String) configOptions.get("observations");
			if (obsFile.startsWith("\""))
				obsFile = obsFile.substring(1);
			if (obsFile.endsWith("\""))
				obsFile = obsFile.substring(0, obsFile.length() - 1);
			String obsText = "";
			try {
				obsText = readFile(obsFile);
			} catch (IOException e) {
				log.printError("Observations file " + e.getMessage());
				return;
			}
			ObservationsFile obs = new ObservationsFile();
			observations = obs.load(obsText, formulae.length);
			if (observations == null)
				log.printError("Invalid observations file");
		}

		// verify that one or more parameters have been defined
		if (parameterNames.size() == 0)
			log.printError("At least one parameter has to be specified!");

		// verify that the parameter defined are included in the model
		for (final String parameterName : parameterNames)
			try {
				model.setParameters(new String[] { parameterName },
						new double[] { 1 });
			} catch (Exception e) {
				log.printError("Parameter \"" + parameterName
						+ "\" does not exist in the model!");
			}

		// verify that the kernel hyperparameters are valid
		Object kerneltype = configOptions.get("kernel");
		Object[] lengthscales = (Object[]) configOptions.get("lengthscale");
		if (kerneltype.equals("rbfiso"))
			if (lengthscales.length > 1)
				log.printWarning("Redundant lenghtscales for "
						+ "isometric RBF kernel; "
						+ "only the first one will be used");
		if (kerneltype.equals("rbfard"))
			if (lengthscales.length != parameterNames.size())
				log.printError("Automatic Relevance Determination requires "
						+ "lengthscales to be specified for all parameters!");
	}

	private void readOption(String property, String value, int line) {
		final PropertySpec spec = optionSpecs.get(property);

		if (spec == null) {
			log.printWarning("Congifuration option \"" + property
					+ "\" is undefined at line " + line);
			return;
		}

		if (spec.isValid(value))
			configOptions.put(property, spec.getValueOf(value));

		else {
			log.printWarning("Invalid assignment for \"" + property
					+ "\" at line " + line + "; it should be "
					+ spec.getValidValues() + "; default value \""
					+ spec.getDefaultValueString() + "\" is used.");
			final Object defaultVal = spec.getDefaultValue();
			configOptions.put(property, defaultVal);
		}
	}

	private void readParameter(String name, String value) {
		final PropertySpec spec = new RangeSpec(name, new double[] { 0, 1 });
		final String errormsg = "Invalid bounds for \"" + name + "\"; "
				+ "should be of the form: " + spec.getValidValues();

		if (spec.isValid(value))
			parameters.put(name, (double[]) spec.getValueOf(value));
		else {
			parameters.put(name, (double[]) spec.getDefaultValue());
			log.printError(errormsg);
		}
		parameterNames.add(name);
	}

	// ugly for now, but it works
	private void readPrior(String name, String value) {
		final PropertySpec argSpec = new CollectionSpec("", null,
				new DoubleSpec("", 1));
		String[] splitValue = value.trim().split("\\(");
		splitValue[1] = "(" + splitValue[1].trim(); // put "(" back

		final String errmsg = "Invalid prior for \"" + name
				+ "\"; prior will be ignored!";

		if (splitValue.length == 2) {
			final String distribution = splitValue[0].trim();
			final String arguments = splitValue[1].trim();
			Object[] args = new Object[0];
			if (argSpec.isValid(arguments))
				args = (Object[]) argSpec.getValueOf(arguments);

			if (distribution.equals("unifrom")) {
				if (args.length == 2) {
					final double a = (double) args[0];
					final double b = (double) args[1];
					if (a >= b)
						log.printWarning(errmsg);
					else
						priors.put(name, new UniformPrior(a, b));
				} else
					log.printWarning(errmsg);
			} else if (distribution.equals("normal")) {
				if (args.length == 2) {
					final double mu = (double) args[0];
					final double s2 = (double) args[1];
					if (s2 <= 0)
						log.printWarning(errmsg);
					else
						priors.put(name, new GaussianPrior(mu, s2));
				} else
					log.printWarning(errmsg);
			} else if (distribution.equals("exponential")) {
				if (args.length == 1) {
					final double mu = (double) args[0];
					if (mu <= 0)
						log.printWarning(errmsg);
					else
						priors.put(name, new ExponentialPrior(mu));
				} else
					log.printWarning(errmsg);
			} else if (distribution.equals("gamma")) {
				if (args.length == 2) {
					final double k = (double) args[0];
					final double theta = (double) args[1];
					if (k <= 0 || theta <= 0)
						log.printWarning(errmsg);
					else
						priors.put(name, new GammaPrior(k, theta));
				} else
					log.printWarning(errmsg);
			} else
				log.printWarning(errmsg);

		} else
			log.printWarning(errmsg);
	}

	final private void addProperty(PropertySpec spec) {
		optionSpecs.put(spec.getName(), spec);
	}

	final private void defineConfigurationOptions() {

		// main experiment options
		addProperty(new StringSpec("model", ""));
		addProperty(new StringSpec("propertyFile", ""));
		addProperty(new CollectionSpec("properties", new String[] {},
				new IDSpec("propertyName", "")));
		addProperty(new StringSpec("observations", ""));
		addProperty(new CategoricalSpec("mode", "", "inference", "smoothedmc"));

		// common simulation options
		addProperty(new DoubleSpec("endTime", 0, 0, false));
		addProperty(new IntegerSpec("runs", 100, 1));
		addProperty(new IntegerSpec("timepoints", 200, 2));
		addProperty(new BooleanSpec("timeseriesEnabled", false));

		// common kernel options
		addProperty(new CategoricalSpec("kernel", "rbfiso", "rbfiso", "rbfard"));
		addProperty(new DoubleSpec("amplitude", 1, 0, false));
		addProperty(new CollectionSpec("lengthscale", new Object[] { 1.0 },
				new DoubleSpec("lengthscale", 1, 0, false)));

		// common options (GP hyperparameters)
		addProperty(new BooleanSpec("hyperparamOptimisation", false));
		addProperty(new IntegerSpec("hyperparamOptimisationRestarts", 5, 0));

		// common options (GP data)
		addProperty(new IntegerSpec("initialObservtions", 100, 1));
		addProperty(new IntegerSpec("numberOfTestPoints", 50, 1));

		// inference options (GP optimisation parameters)
		addProperty(new BooleanSpec("logspace", false));
		addProperty(new IntegerSpec("maxIterations", 500, 1));
		addProperty(new IntegerSpec("maxAddedPointsNoImprovement", 100, 1));
		addProperty(new DoubleSpec("improvementFactor", 1.01, 0, false));
		addProperty(new IntegerSpec("maxFailedAttempts", 200, 1));
		addProperty(new DoubleSpec("beta", 2, 0, true));

		// inference options (GP regression parameters)
		addProperty(new DoubleSpec("noiseTerm", 1, 0, false));
		addProperty(new DoubleSpec("noiseTermRatio", 0.1, 0, false));
		addProperty(new BooleanSpec("useNoiseTermRatio", false));
		addProperty(new BooleanSpec("heteroskedastic", false));
		addProperty(new BooleanSpec("useDefaultHyperparams", true));

		// smoothedmc options (GP classification parameters)
		addProperty(new DoubleSpec("covarianceCorrection", 1e-4, 0, false));
	}

	public String getMode() {
		final String mode = (String) configOptions.get("mode");
		if (mode != null)
			return mode;
		return "";
	}

	public UcheckModel getModel() {
		return model;
	}

	public String[] getFormnulae() {
		return formulae;
	}

	public boolean[][] getObservations() {
		return observations;
	}

	public smoothedMC.Parameter[] getSmMCParameters() {
		smoothedMC.Parameter[] params = new smoothedMC.Parameter[parameterNames
				.size()];
		for (int i = 0; i < params.length; i++) {
			final String name = parameterNames.get(i);
			double[] value = parameters.get(name);
			params[i] = new smoothedMC.Parameter(name, value[0], value[1]);
		}
		return params;
	}

	public gpoMC.Parameter[] getLFFParameters() {
		gpoMC.Parameter[] params = new gpoMC.Parameter[parameterNames.size()];
		for (int i = 0; i < params.length; i++) {
			final String name = parameterNames.get(i);
			double[] value = parameters.get(name);
			params[i] = new gpoMC.Parameter(name, value[0], value[1]);
		}
		return params;
	}

	public Prior[] getLFFPriors() {
		final Prior[] priors = new Prior[parameterNames.size()];
		for (int i = 0; i < parameterNames.size(); i++) {
			String name = parameterNames.get(i);
			final Prior prior = this.priors.get(name);
			if (prior != null)
				priors[i] = prior;
			else {
				double[] value = parameters.get(name);
				priors[i] = new UniformPrior(value[0], value[1]);
			}
		}
		return priors;
	}

	public SmmcOptions getSmMCOptions() {
		final SmmcOptions options = new SmmcOptions();

		Set<String> keys = configOptions.keySet();
		for (final String key : keys) {
			final Object value = configOptions.get(key);

			if (key.equals("endTime"))
				options.setSimulationEndTime((double) value);
			else if (key.equals("runs"))
				options.setSimulationRuns((int) value);
			else if (key.equals("timepoints"))
				options.setSimulationTimepoints((int) value);
			else if (key.equals("timeseriesEnabled"))
				options.setTimeseriesEnabled((boolean) value);

			else if (key.equals("initialObservtions"))
				options.setN((int) value);
			else if (key.equals("numberOfTestPoints"))
				options.setM((int) value);

			else if (key.equals("hyperparamOptimisation"))
				options.setHyperparamOptimisation((boolean) value);
			else if (key.equals("hyperparamOptimisationRestarts"))
				options.setHyperparamOptimisationRestarts((int) value);
			else if (key.equals("covarianceCorrection"))
				options.setCovarianceCorrection((double) value);

			else if (key.equals("kernel"))
				options.setKernelGP(getKernel());

			else
				;
		}
		return options;
	}

	public LFFOptions getLFFOptions() {
		final LFFOptions options = new LFFOptions();

		Set<String> keys = configOptions.keySet();
		for (final String key : keys) {
			final Object value = configOptions.get(key);

			if (key.equals("endTime"))
				options.setSimulationEndTime((double) value);
			else if (key.equals("runs"))
				options.setSimulationRuns((int) value);
			else if (key.equals("timepoints"))
				options.setSimulationTimepoints((int) value);
			else if (key.equals("timeseriesEnabled"))
				options.setTimeseriesEnabled((boolean) value);

			else if (key.equals("initialObservtions"))
				options.getGpoOptions().setInitialObservtions((int) value);
			else if (key.equals("numberOfTestPoints"))
				options.getGpoOptions().setGridSampleNumber((int) value);
			else if (key.equals("logspace"))
				options.getGpoOptions().setLogspace((boolean) value);

			else if (key.equals("maxIterations"))
				options.getGpoOptions().setMaxIterations((int) value);
			else if (key.equals("maxAddedPointsNoImprovement"))
				options.getGpoOptions().setMaxAddedPointsNoImprovement(
						(int) value);
			else if (key.equals("improvementFactor"))
				options.getGpoOptions().setImprovementFactor((double) value);
			else if (key.equals("maxFailedAttempts"))
				options.getGpoOptions().setMaxFailedAttempts((int) value);
			else if (key.equals("beta"))
				options.getGpoOptions().setBeta((double) value);

			else if (key.equals("noiseTerm"))
				options.getGpoOptions().setNoiseTerm((double) value);
			else if (key.equals("noiseTermRatio"))
				options.getGpoOptions().setNoiseTermRatio((double) value);
			else if (key.equals("useNoiseTermRatio"))
				options.getGpoOptions().setUseNoiseTermRatio((boolean) value);
			else if (key.equals("heteroskedastic"))
				options.getGpoOptions().setHeteroskedastic((boolean) value);
			else if (key.equals("useDefaultHyperparams"))
				options.getGpoOptions().setUseDefaultHyperparams(
						(boolean) value);
			else if (key.equals("hyperparamOptimisation"))
				options.getGpoOptions().setHyperparamOptimisation(
						(boolean) value);
			else if (key.equals("hyperparamOptimisationRestarts"))
				options.getGpoOptions().setHyperparamOptimisationRestarts(
						(int) value);

			else if (key.equals("kernel"))
				options.getGpoOptions().setKernelGP(getKernel());

			else
				;
		}
		return options;
	}

	public KernelFunction getKernel() {
		// load hyperparameters, if exist
		Double a = (Double) configOptions.get("amplitude");
		if (a == null) {
			final PropertySpec spec = optionSpecs.get("amplitude");
			a = (Double) spec.getDefaultValue();
		}
		Object[] l = (Object[]) configOptions.get("lengthscale");
		if (l == null) {
			final PropertySpec spec = optionSpecs.get("lengthscale");
			l = (Object[]) spec.getDefaultValue();
		}

		final Object value = configOptions.get("kernel");
		if (value.equals("rbfiso")) {
			return new KernelRBF(a, (double) l[0]);
		}
		if (value.equals("rbfard")) {
			final int dim = parameterNames.size();
			final double[] hyp = new double[dim + 1];
			hyp[0] = a;
			if (dim == l.length)
				for (int i = 1; i < hyp.length; i++)
					hyp[i] = (double) l[i - 1];
			else
				for (int i = 1; i < hyp.length; i++)
					hyp[i] = (double) l[0];
			return new KernelRbfARD(hyp);
		}
		throw new IllegalStateException("This sould not happen!");
	}

	private static final String readFile(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(filename);
		final byte[] fileData = new byte[input.available()];
		input.read(fileData);
		input.close();
		return new String(fileData);
	}

}
