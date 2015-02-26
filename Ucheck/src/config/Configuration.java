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
			properties.put(property, spec.getValueOf(defaultVal));
		}
	}

	private void readParameter(String name, String value) {
		final RangeSpec spec = new RangeSpec(name, "[0, 1]");
		final double[] range;
		if (spec.isValid(value))
			range = (double[]) spec.getValueOf(value);
		else
			range = null;
		parameters.put(name, range);
		parameterNames.add(name);
	}

	/**
	 * @param value
	 *            can be ["rbfiso" | "rbfard"]
	 */
	private KernelFunction readKernel(String value) {
		if (value.equals("rbfiso")) {
			return new KernelRBF();
		} else

		if (value.equals("rbfard")) {
			final int dim = parameterNames.size();
			return new KernelRbfARD(dim);
		}

		else
			throw new IllegalStateException("This should not happen!");
	}

	private double[] parseDoubles(String string) {
		string = string.trim();
		if (string.startsWith("("))
			string = string.substring(1);
		if (string.endsWith(")"))
			string = string.substring(0, string.length() - 1);

		if (string.startsWith("\\W"))
			throw new IllegalAccessError();
		if (string.endsWith("\\W"))
			throw new IllegalAccessError();

		final String[] strValues = string.split(",");
		final double[] values = new double[strValues.length];
		for (int i = 0; i < values.length; i++)
			values[i] = Double.parseDouble(strValues[i]);

		return values;
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
			final String errormsg = "Invalid bounds for \"" + name + "\"; "
					+ "should be of the form: \"[number1, number2]\", "
					+ "where number1 < number2";

			String valueString = parameters.get(name).toString();
			if (valueString.startsWith("["))
				valueString = valueString.substring(1);
			else {
				log.printError(errormsg);
				continue;
			}
			if (valueString.endsWith("]"))
				valueString = valueString
						.substring(0, valueString.length() - 1);
			else {
				log.printError(errormsg);
				continue;
			}

			double[] vals = null;
			try {
				vals = parseDoubles(valueString);
			} catch (NumberFormatException e) {
				log.printError(errormsg);
				continue;
			}
			if (vals.length == 2) {
				final double lb = vals[0];
				final double ub = vals[1];

				try {
					params[i] = new smoothedMC.Parameter(name, lb, ub);
				} catch (IllegalArgumentException e) {
					log.printError(errormsg);
				}
			} else {
				log.printError(errormsg);
				continue;
			}
		}
		return params;
	}

	public gpoMC.Parameter[] getLFFParameters() {
		gpoMC.Parameter[] params = new gpoMC.Parameter[parameterNames.size()];
		for (int i = 0; i < params.length; i++) {
			final String name = parameterNames.get(i);
			final String errormsg = "Invalid bounds for \"" + name + "\"; "
					+ "should be of the form: \"[number1, number2]\", "
					+ "where number1 < number2";

			String valueString = parameters.get(name).toString();
			if (valueString.startsWith("["))
				valueString = valueString.substring(1);
			else {
				log.printError(errormsg);
				continue;
			}
			if (valueString.endsWith("]"))
				valueString = valueString
						.substring(0, valueString.length() - 1);
			else {
				log.printError(errormsg);
				continue;
			}

			double[] vals = null;
			try {
				vals = parseDoubles(valueString);
			} catch (NumberFormatException e) {
				log.printError(errormsg);
				continue;
			}
			if (vals.length == 2) {
				final double lb = vals[0];
				final double ub = vals[1];

				try {
					params[i] = new gpoMC.Parameter(name, lb, ub);
				} catch (IllegalArgumentException e) {
					log.printError(errormsg);
				}
			} else {
				log.printError(errormsg);
				continue;
			}
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
				;// options.setKernelGP(readKernel(value));

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
				; // options.getGpoOptions().setKernelGP(readKernel(value));

			else
				;
		}
		return options;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		Configuration config = new Configuration();
		FileInputStream file = new FileInputStream("example.txt");
		config.load(file);
	}

}
