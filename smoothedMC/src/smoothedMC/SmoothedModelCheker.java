package smoothedMC;

import gp.GpDataset;
import gp.HyperparamLogLikelihood;
import gp.classification.GPEP;
import gp.classification.ClassificationPosterior;
import gp.kernels.KernelRbfARD;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import optim.LocalOptimisation;
import optim.PointValue;
import optim.methods.PowellMethodApache;

import mitl.MiTL;
import mitl.MitlPropertiesList;
import parsers.MitlFactory;
import smoothedMC.gridSampling.GridSampler;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import ssa.Trajectory;
import biopepa.BiopepaFile;

public final class SmoothedModelCheker {

	public ClassificationPosterior performSmoothedModelChecking(
			String modelFile, String mitlFile, Parameter[] parameters,
			SmMCOptions options) throws IOException {
		long t0;
		double elapsed;

		t0 = System.currentTimeMillis();
		final GpDataset data = performStatisticalModelChecking(modelFile,
				mitlFile, parameters, options);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		if (options.isDebugEnabled())
			System.out.println("Statistical Model Checking:  " + elapsed
					+ " sec");

		return performSmoothedModelChecking(data, parameters, options);
	}

	public ClassificationPosterior performSmoothedModelChecking(GpDataset data,
			Parameter[] parameters, SmMCOptions options) {
		GPEP gp = new GPEP(options.getKernelGP());
		gp.setTrainingSet(data);
		gp.setScale(options.getSimulationRuns());
		gp.setCovarianceCorrection(options.getCovarianceCorrection());

		long t0;
		double elapsed;
		if (options.getHyperparamOptimisation()) {
			t0 = System.currentTimeMillis();
			optimiseGPHyperParameters(gp, options);
			elapsed = (System.currentTimeMillis() - t0) / 1000d;
			if (options.isDebugEnabled())
				System.out.println("Hyperparameter optimisation: " + elapsed
						+ " sec");
		}
		if (options.isDebugEnabled()) {
			System.out.println("amplitude:   "
					+ options.getKernelGP().getHyperarameters()[0]);
			System.out.println("lengthscale: "
					+ options.getKernelGP().getHyperarameters()[1]);
			if (options.getKernelGP() instanceof KernelRbfARD) {
				int dim = options.getKernelGP().getHyperarameters().length - 1;
				for (int i = 1; i < dim; i++)
					System.out.println("lengthscale: "
							+ options.getKernelGP().getHyperarameters()[i + 1]);
			}
		}

		t0 = System.currentTimeMillis();
		final double[][] paramValueSet = options.getSampler().sample(
				options.getM(), parameters);
		final GpDataset testSet = new GpDataset(paramValueSet);
		final ClassificationPosterior post = gp.getGpPosterior(testSet);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		if (options.isDebugEnabled()) {
			System.out.println("Smoothed Model Checking:     " + elapsed
					+ " sec");
			final double lik = gp.getMarginalLikelihood();
			System.out.println("log-likelihood: " + lik);
		}
		return post;
	}

	public GpDataset performStatisticalModelChecking(String modelFile,
			String mitlFile, Parameter[] parameters, SmMCOptions options) throws IOException {

		BiopepaFile biopepaFile = new BiopepaFile(modelFile);
		final String mitlText = readFile(mitlFile);
		for (final Parameter param : parameters)
			if (!biopepaFile.containsVariable(param.getName()))
				throw new IllegalArgumentException("The is no variable \""
						+ param + "\" in the model");

		final GridSampler sampler = options.getSampler();
		final double simulationEndTime = options.getSimulationEndTime();

		final String[] paramNames = new String[parameters.length];
		for (int i = 0; i < paramNames.length; i++)
			paramNames[i] = parameters[i].getName();

		final double[][] paramValueSet = sampler.sample(options.getN(),
				parameters);
		final int datapoints = paramValueSet.length;
		final double[] paramValueOutputs = new double[datapoints];

		final boolean doTimeseries = options.isTimeseriesEnabled();
		final int timepoints = options.getSimulationTimepoints();

		for (int i = 0; i < datapoints; i++) {
			CTMCModel model = biopepaFile
					.getModel(paramNames, paramValueSet[i]);

			MitlFactory factory = new MitlFactory(model.getStateVariables());
			MitlPropertiesList l = factory.constructProperties(mitlText);
			ArrayList<MiTL> list = l.getProperties();
			final MiTL property = list.get(0);

			StochasticSimulationAlgorithm ssa = new GillespieSSA(model);

			for (int run = 0; run < options.getSimulationRuns(); run++) {
				final Trajectory x;
				if (doTimeseries)
					x = ssa.generateTimeseries(0, simulationEndTime, timepoints);
				else
					x = ssa.generateTrajectory(0, simulationEndTime);
				paramValueOutputs[i] += property.evaluate(x, 0) ? 1 : 0;
			}
			paramValueOutputs[i] /= options.getSimulationRuns();
		}
		return new GpDataset(paramValueSet, paramValueOutputs);
	}

	private void optimiseGPHyperParameters(GPEP gp, SmMCOptions options) {
		HyperparamLogLikelihood func = new HyperparamLogLikelihood(gp);
		LocalOptimisation alg = new PowellMethodApache();
		final double init[] = gp.getKernel().getHyperarameters();
		PointValue best = alg.optimise(func, init);

		for (int r = 0; r < options.getHyperparamOptimisationRestarts(); r++) {
			final double[] currnetInit = new double[init.length];
			for (int i = 0; i < currnetInit.length; i++)
				currnetInit[i] = Math.random() * init[i] * 2;
			final PointValue curr = alg.optimise(func, currnetInit);
			if (curr.getValue() > best.getValue())
				best = curr;
		}
		gp.getKernel().setHyperarameters(best.getPoint());
	}

	protected static final String readFile(String filename) {
		FileInputStream input;
		byte[] fileData;
		try {
			input = new FileInputStream(filename);
			fileData = new byte[input.available()];
			input.read(fileData);
			input.close();
		} catch (IOException e) {
			fileData = new byte[0];
		}
		return new String(fileData);
	}

}
