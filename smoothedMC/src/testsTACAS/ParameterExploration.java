package testsTACAS;

import java.io.FileWriter;
import java.io.IOException;

import expr.Variable;
import biopepa.BiopepaFile;
import smoothedMC.Parameter;
import smoothedMC.gridSampling.GridSampler;
import smoothedMC.gridSampling.RegularSampler;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import ssa.Trajectory;

public class ParameterExploration {

	public static void main(String[] args) {

		final Parameter k2 = new Parameter("k2", 10, 5000);
		final Parameter k7 = new Parameter("k7", 0.45, 450);
		Parameter[] par_k2k7 = new Parameter[] { k2, k7 };

		double tfinal = 21000;
		final String file = "models/lacz.biopepa";

		performExploration(file, par_k2k7, "LacZ", 16, tfinal);
	}

	private static void performExploration(String file, Parameter[] parameters,
			String variableName, int points, double stopTime) {
		BiopepaFile biopepaFile = new BiopepaFile(file);
		final String[] paramNames = new String[parameters.length];
		for (int i = 0; i < paramNames.length; i++)
			paramNames[i] = parameters[i].getName();

		final GridSampler sampler = new RegularSampler();
		final double[][] paramValueSet = sampler.sample(points, parameters);

		CTMCModel model;
		for (int i = 0; i < points; i++) {
			String outputFile = "parameter_exploration/";
			for (int par_i = 0; par_i < paramNames.length; par_i++) {
				if (par_i > 0)
					outputFile += "__";
				outputFile += paramNames[par_i] + "_" + paramValueSet[i][par_i];
			}
			outputFile += ".csv";

			model = biopepaFile.getModel(paramNames, paramValueSet[i]);
			StochasticSimulationAlgorithm sim = new GillespieSSA(model);

			int variableIndex = -1;
			final Variable[] variables = model.getStateVariables()
					.getVariables();
			for (int c = 0; c < variables.length; c++)
				if (variables[c].getName().equals(variableName)) {
					variableIndex = c;
					break;
				}

			final int runs = 3;
			final int timepoints = 61;
			final double[][] trajectories = new double[runs][];
			
			Trajectory x;
			x = sim.generateTimeseries(0, stopTime, timepoints);
			final double[] t = x.getTimes();
			trajectories[0] = x.getValues(variableIndex);
			for (int c = 1; c < runs; c++) {
				x = sim.generateTimeseries(0, stopTime, timepoints);
				trajectories[c] = x.getValues(variableIndex);
			}

			FileWriter fw = null;
			try {
				fw = new FileWriter(outputFile);
				for (int c = 0; c < t.length; c++) {
					fw.append(t[c] + ",\t");
					for (int run = 0; run < runs - 1; run++)
						fw.append(trajectories[run][c] + ",\t");
					fw.append(trajectories[runs - 1][c] + "\n");
				}
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
