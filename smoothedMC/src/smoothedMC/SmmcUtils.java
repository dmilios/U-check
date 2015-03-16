package smoothedMC;

import gp.classification.ClassificationPosterior;
import gp.kernels.KernelRBF;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import smoothedMC.gridSampling.RegularSampler;

public class SmmcUtils {

	public static final void runSmMC(String modelFile, String mitlFile, int n,
			int m, int runs, double tfinal, int simPoints, double amplitude,
			double lengthscale, boolean optimise, String[] parameter,
			double[] lb, double[] ub) throws IOException {

		final SmmcOptions options = new SmmcOptions();
		options.setN(n);
		options.setNumberOfTestPoints(m);
		options.setSimulationRuns(runs);
		options.setSimulationEndTime(tfinal);
		options.setTimeseriesEnabled(true);
		if (simPoints > 0)
			options.setSimulationTimepoints(simPoints);
		else
			options.setTimeseriesEnabled(false);
		options.setSampler(new RegularSampler());
		options.setKernelGP(new KernelRBF(amplitude, lengthscale));
		options.setHyperparamOptimisation(optimise);
		options.setHyperparamOptimisationRestarts(3);
		options.setDebugEnabled(true);

		final Parameter[] params = new Parameter[parameter.length];
		for (int i = 0; i < params.length; i++)
			params[i] = new Parameter(parameter[i], lb[i], ub[i]);

		final SmoothedModelCheker smc = new SmoothedModelCheker();
		final ClassificationPosterior post = smc.performSmoothedModelChecking(
				modelFile, mitlFile, params, options);
		results2matlab(post, modelFile);
	}

	/**
	 * The first line is a comment that contains the headers. <br>
	 * This is automatically ignored in octave by simply using:
	 * {@code load 'example.csv'}<br>
	 * In matlab, the following command has to be used: <br>
	 * {@code csvread ('example.csv', 1)} <br>
	 * (which ignores the first line)
	 */
	public static final String results2csv(ClassificationPosterior post,
			double beta) {
		final StringBuilder str = new StringBuilder();
		final int dim = post.getInputData().getDimension();
		str.append("# ");
		for (int d = 0; d < dim; d++)
			str.append("x" + (d + 1) + ",\t");
		str.append("probability,\t");
		str.append("lower bound,\t");
		str.append("upper bound\n");
		for (int i = 0; i < post.getSize(); i++) {
			for (int d = 0; d < dim; d++)
				str.append(post.getInputData().getInstance(i)[d] + ",\t");
			str.append(post.getClassProbabilities()[i] + ",\t");
			str.append(post.getLowerBound(beta)[i] + ",\t");
			str.append(post.getUpperBound(beta)[i] + "\n");
		}
		return str.toString();
	}

	/** Do not use that; it is ugly... will be removed, when it is not needed */
	public static final void results2matlab(ClassificationPosterior post,
			String modelFile) throws IOException {
		String outDir = "output";
		if (!new File(outDir).exists()) {
			new File(outDir).mkdir();
		} else if (!new File(outDir).isDirectory()) {
			System.err.println(outDir + " is not a directory!");
			return;
		}

		final String[] path = modelFile.split(File.separator);
		final String modelName = path[path.length - 1].split("\\.")[0];
		FileWriter fw;

		fw = new FileWriter(outDir + "/" + modelName + "_grid.csv");
		final int dim = post.getInputData().getDimension();
		for (int i = 0; i < post.getSize(); i++) {
			for (int d = 0; d < dim - 1; d++)
				fw.append(post.getInputData().getInstance(i)[d] + ",\t");
			fw.append(post.getInputData().getInstance(i)[dim - 1] + "\n");
		}
		fw.close();

		fw = new FileWriter(outDir + "/" + modelName + "_pred.csv");
		for (int i = 0; i < post.getSize(); i++) {
			fw.append(post.getClassProbabilities()[i] + ",\t");
			fw.append(post.getLowerBound(2)[i] + ",\t");
			fw.append(post.getUpperBound(2)[i] + "\n");
		}
		fw.close();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		final String modelFile = "models/SIR.biopepa";
		final String mitlFile = "formulae/SIR.mtl";

		final SmmcOptions options = new SmmcOptions();
		options.setN(64);
		options.setNumberOfTestPoints(256);
		options.setSimulationRuns(10);
		options.setSimulationEndTime(200);
		options.setSampler(new RegularSampler());
		options.setDebugEnabled(true);

		options.setTimeseriesEnabled(true);
		options.setSimulationTimepoints(200);

		options.setKernelGP(new KernelRBF(1, 0.11547)); // 1 / 8.66
		options.setHyperparamOptimisation(!true);
		options.setHyperparamOptimisationRestarts(0);

		final Parameter[] params = new Parameter[1];
		params[0] = new Parameter("ki", 0.005, 0.3);

		final SmoothedModelCheker smc = new SmoothedModelCheker();
		final ClassificationPosterior post = smc.performSmoothedModelChecking(
				modelFile, mitlFile, params, options);
		results2matlab(post, modelFile);
	}
}
