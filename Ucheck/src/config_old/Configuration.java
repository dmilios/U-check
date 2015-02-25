package config_old;

import gp.kernels.KernelFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import priors.Prior;
import priors.UniformPrior;

public final class Configuration {

	private String modelFile;
	private String propertiesFile;
	private String observationsFile = "";
	private Mode mode = Mode.none;

	private ArrayList<String> parameters = new ArrayList<String>();
	private ArrayList<Double> paramLBs = new ArrayList<Double>();
	private ArrayList<Double> paramUBs = new ArrayList<Double>();
	private ArrayList<Prior> priors = new ArrayList<Prior>();

	private KernelFunction kernel;
	private Map<String, Double> optionMap = new HashMap<String, Double>();

	public enum Mode {
		none, inference, smoothedmc
	};

	@Override
	public String toString() {
		String out = "";
		out += "model = " + "\"" + modelFile + "\";\n";
		out += "properties = " + "\"" + propertiesFile + "\";\n";
		out += "observations = " + "\"" + observationsFile + "\";\n";

		out += "\n";
		for (int i = 0; i < parameters.size(); i++) {
			out += "parameter " + parameters.get(i) + " = [" + paramLBs.get(i)
					+ ", " + paramUBs.get(i) + "];\n";
		}
		out += "\n\n";
		for (String key : optionMap.keySet())
			out += key + " = " + optionMap.get(key) + ";\n";
		return out;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Map<String, Double> getOptionMap() {
		return optionMap;
	}

	public String getModelFile() {
		return modelFile;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public String getObservationsFile() {
		return observationsFile;
	}

	public void setObservationsFile(String observationsFile) {
		this.observationsFile = observationsFile;
	}

	public void addParameter(String name, double lb, double ub) {
		parameters.add(name);
		paramLBs.add(lb);
		paramUBs.add(ub);
		priors.add(new UniformPrior(lb, ub));
	}

	public void setPrior(String paramName, Prior prior) {
		final int index = parameters.indexOf(paramName);
		if (index >= 0)
			priors.set(index, prior);
	}
	
	public ArrayList<String> getParameters() {
		return parameters;
	}

	public ArrayList<Double> getParamLBs() {
		return paramLBs;
	}

	public ArrayList<Double> getParamUBs() {
		return paramUBs;
	}

	public KernelFunction getKernel() {
		return kernel;
	}
	
	public void setKernel(KernelFunction kernel) {
		this.kernel = kernel;
	}
	
}
