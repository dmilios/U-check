package config;

import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;
import gp.kernels.KernelRbfARD;
import gpoMC.LFFOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import smoothedMC.SmMCOptions;
import cli.Log;
import cli.PrintStreamLog;

public class Configuration {

	private Map<String, PropertySpec> propertySpecs = new HashMap<String, PropertySpec>();
	private Map<String, Object> properties = new HashMap<String, Object>();

	private ArrayList<String> parameterNames = new ArrayList<String>();
	private Map<String, double[]> parameters = new HashMap<String, double[]>();

	private Log log;

	public Configuration() {
		this(new PrintStreamLog(System.out));
	}

	public Configuration(Log log) {
		this.log = log;
		defineConfigurationOptions();
	}

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

			final String property = split[0].trim();
			final String value = split[1].trim();

			final String[] propertySplit = property.split("\\s+");
			if (propertySplit.length == 1)
				readProperty(property, value, lineCounter);
			if (propertySplit.length == 2)
				if (propertySplit[0].equals("parameter"))
					readParameter(propertySplit[1], value);
				else
					log.printWarning("Congifuration option \"" + property
							+ "\" is undefined at line " + lineCounter);
		}
		scanner.close();
		verifyLoadedInformation();
	}

	private void verifyLoadedInformation() {

		// verify that one or more parameters have been defined
		if (parameterNames.size() == 0)
			log.printError("At least one parameter has to be specified!");
		
		// verify that the parameter defined are included in the model
		for (final String parameterName : parameterNames) {
		 
		}

		// verify that the kernel hyperparameters are valid
		Object kerneltype = properties.get("kernel");
		Object[] lengthscales = (Object[]) properties.get("lengthscale");
		if (kerneltype.equals("rbfiso"))
			if (lengthscales.length > 1)
				log.printWarning("Redundant lenghtscales for "
						+ "isometric RBF kernel; "
						+ "only the first one will be used");
		if (kerneltype.equals("rbfard"))
			if (lengthscales.length > 1)
				log.printError("Automatic Relevance Determination requires "
						+ "lengthscales to be specified for all parameters!");
	}

	private void readProperty(String property, String value, int line) {
		final PropertySpec spec = propertySpecs.get(property);

		if (spec == null) {
			log.printWarning("Congifuration option \"" + property
					+ "\" is undefined at line " + line);
			return;
		}

		if (spec.isValid(value))
			properties.put(property, spec.getValueOf(value));

		else {
			log.printWarning("Invalid assignment for \"" + property
					+ "\" at line " + line + "; it should be "
					+ spec.getValidValues() + "; default value \""
					+ spec.getDefaultValue() + "\" is used.");
			final String defaultVal = spec.getDefaultValue();
			
			// FIXME: in case of CollectionSpec, default value is invalid
			
			properties.put(property, spec.getValueOf(defaultVal));
		}
	}

	private void readParameter(String name, String value) {
		final PropertySpec spec = new RangeSpec(name, "[0, 1]");
		final String errormsg = "Invalid bounds for \"" + name + "\"; "
				+ "should be of the form: " + spec.getValidValues();

		if (spec.isValid(value))
			parameters.put(name, (double[]) spec.getValueOf(value));
		else {
			parameters.put(name,
					(double[]) spec.getValueOf(spec.getDefaultValue()));
			log.printError(errormsg);
		}
		parameterNames.add(name);
	}

	final private void addProperty(PropertySpec spec) {
		propertySpecs.put(spec.getName(), spec);
	}

	final private void defineConfigurationOptions() {

		// main options
		addProperty(new StringSpec("model", ""));
		addProperty(new StringSpec("properties", ""));
		addProperty(new StringSpec("observations", ""));
		addProperty(new CategoricalSpec("mode", "", "inference", "smoothedmc"));

		// common simulation options
		addProperty(new DoubleSpec("endTime", "0", 0, false));
		addProperty(new IntegerSpec("runs", "100", 1));
		addProperty(new IntegerSpec("timepoints", "200", 2));
		addProperty(new BooleanSpec("timeseriesEnabled", "false"));

		// common kernel options
		addProperty(new CategoricalSpec("kernel", "rbfiso", "rbfiso", "rbfard"));
		addProperty(new DoubleSpec("amplitude", "1", 0, false));
		addProperty(new CollectionSpec("lengthscale", new DoubleSpec(
				"lengthscale", "1", 0, false)));

		// common options (GP parameters)
		addProperty(new BooleanSpec("hyperparamOptimisation", "false"));
		addProperty(new IntegerSpec("hyperparamOptimisationRestarts", "5", 0));

		// inference options (GP optimisation parameters)
		addProperty(new IntegerSpec("initialObservtions", "100", 1));
		addProperty(new IntegerSpec("gridSampleNumber", "50", 1));
		addProperty(new BooleanSpec("logspace", "false"));
		addProperty(new IntegerSpec("maxIterations", "500", 1));
		addProperty(new IntegerSpec("maxAddedPointsNoImprovement", "100", 1));
		addProperty(new DoubleSpec("improvementFactor", "1.01", 0, false));
		addProperty(new IntegerSpec("maxFailedAttempts", "200", 1));
		addProperty(new DoubleSpec("beta", "2", 0, true));

		// inference options (GP regression parameters)
		addProperty(new DoubleSpec("noiseTerm", "1", 0, false));
		addProperty(new DoubleSpec("noiseTermRatio", "0.1", 0, false));
		addProperty(new BooleanSpec("useNoiseTermRatio", "false"));
		addProperty(new BooleanSpec("heteroskedastic", "false"));
		addProperty(new BooleanSpec("useDefaultHyperparams", "true"));

		// smoothedmc options (GP classification parameters)
		addProperty(new DoubleSpec("covarianceCorrection", "1e-4", 0, false));
	}

	public String getMode() {
		final String mode = (String) properties.get("mode");
		if (mode != null)
			return mode;
		return "";
	}

	public String getModel() {
		return (String) properties.get("model");
	}

	public String getProperties() {
		return (String) properties.get("properties");
	}

	public String getObservations() {
		return (String) properties.get("observations");
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

	public SmMCOptions getSmMCOptions() {
		final SmMCOptions options = new SmMCOptions();

		Set<String> keys = properties.keySet();
		for (final String key : keys) {
			final Object value = properties.get(key);

			if (key.equals("endTime"))
				options.setSimulationEndTime((double) value);
			else if (key.equals("runs"))
				options.setSimulationRuns((int) value);
			else if (key.equals("timepoints"))
				options.setSimulationTimepoints((int) value);
			else if (key.equals("timeseriesEnabled"))
				options.setTimeseriesEnabled((boolean) value);

			else if (key.equals("inputDatapoints"))
				options.setN((int) value);
			else if (key.equals("outputDatapoints"))
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

		Set<String> keys = properties.keySet();
		for (final String key : keys) {
			final Object value = properties.get(key);

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
			else if (key.equals("gridSampleNumber"))
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
		Double a = (Double) properties.get("amplitude");
		if (a == null) {
			final PropertySpec spec = propertySpecs.get("amplitude");
			a = (Double) spec.getValueOf(spec.getDefaultValue());
		}
		Object[] l = (Object[]) properties.get("lengthscale");
		if (l == null) {
			final PropertySpec spec = propertySpecs.get("lengthscale");
			l = (Object[]) spec.getValueOf(spec.getDefaultValue());
		}

		final Object value = properties.get("kernel");
		if (value.equals("rbfiso"))
			return new KernelRBF(a, (double) l[0]);
		if (value.equals("rbfard")) {
			final double[] hyp = new double[l.length + 1];
			hyp[0] = a;
			for (int i = 1; i < hyp.length; i++)
				hyp[i] = (double) l[i - 1];
			return new KernelRbfARD(hyp);
		}
		throw new IllegalStateException("This sould not happen!");
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		Configuration config = new Configuration();
		FileInputStream file = new FileInputStream("example.txt");
		config.load(file);
	}

}
