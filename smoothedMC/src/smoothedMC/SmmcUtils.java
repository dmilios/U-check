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

		final Options options = new Options();
		options.setN(n);
		options.setM(m);
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

		final Options options = new Options();
		options.setN(64);
		options.setM(256);
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
