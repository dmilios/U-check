package testsTACAS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import mitl.MiTL;
import mitl.MitlPropertiesList;
import parsers.MitlFactory;
import smoothedMC.Options;
import smoothedMC.Parameter;
import smoothedMC.gridSampling.GridSampler;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import ssa.Trajectory;
import biopepa.BiopepaFile;
import expr.Context;
import expr.Variable;
import gp.GpDataset;
import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;
import gp.kernels.KernelRbfARD;

/**
 * This class contains static methods that perform certain statistical model
 * checking operations that require some post-processing (i.e. calculating means
 * or derivatives). <br>
 * <br>
 * This functionality may be incorporated ion the tool in some future version.
 */
public final class SMCExtra_OLD {

	private SMCExtra_OLD() {
	}

	public static int MOVING_WINDOW_HALF_SIZE = 4;

	/**
	 * This method performs statistical model checking and returns the results
	 * in a form that can be used by a GP in terms of smoothedMC.<br>
	 * <br>
	 * NOTE: The time-series that are checked also contain mean information. The
	 * means are approximated via simulation. <br>
	 * <br>
	 * WANRING: Only works with time-series (no raw trajectories)
	 * 
	 * @param biopepaFile
	 *            The model in biopepa spec
	 * @param mitlText
	 *            A string containing the formula to be checked
	 * @param parameters
	 * @param options
	 */
	public static GpDataset statisticalMCwithMeans(BiopepaFile biopepaFile,
			String mitlText, Parameter[] parameters, Options options) {

		long t0 = System.currentTimeMillis();
		double elapsed;

		long t0_mean;
		double elapsed_mean = 0;

		final int timepoints = options.getSimulationTimepoints();
		final double simulationEndTime = options.getSimulationEndTime();

		// sample over the parameter space
		// and reserve space for the observations
		final GridSampler sampler = options.getSampler();
		final double[][] paramValueSet = sampler.sample(options.getN(),
				parameters);
		final int datapoints = paramValueSet.length;
		final double[] paramValueObservations = new double[datapoints];

		final String[] paramNames = new String[parameters.length];
		for (int i = 0; i < paramNames.length; i++)
			paramNames[i] = parameters[i].getName();

		// --------------------------------------------------------------------
		// create a new context that contains both the model variables
		// and the variables that hold the means
		Context context = new Context();
		CTMCModel model = biopepaFile.getModel();
		Variable[] modelVariables = model.getStateVariables().getVariables();
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName(), context);
		// naming convention: the variables that hold the means
		// have suffix '_m'
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName() + "_m", context);
		// --------------------------------------------------------------------

		for (int i = 0; i < datapoints; i++) {

			// extract the CTMCModel for the current parameter values
			model = biopepaFile.getModel(paramNames, paramValueSet[i]);

			// parse the property
			MitlFactory factory = new MitlFactory(context);
			MitlPropertiesList l = factory.constructProperties(mitlText);
			ArrayList<MiTL> list = l.getProperties();
			final MiTL property = list.get(0);

			StochasticSimulationAlgorithm ssa = new GillespieSSA(model);

			// approximate average behaviour via simulation
			// (ODEs are VERY slow for the LacZ model!)
			t0_mean = System.currentTimeMillis();
			Trajectory m = ssa.generateTimeseries(0, simulationEndTime,
					timepoints, options.getSimulationRuns());
			elapsed_mean += (System.currentTimeMillis() - t0_mean) / 1000d;

			double[][] values = new double[2 * modelVariables.length][];
			final Trajectory enhanced = new Trajectory(m.getTimes(), context,
					values);

			// copy the information about the means in the enhanced time-series
			for (int c = 0; c < modelVariables.length; c++)
				values[c + modelVariables.length] = m.getValues(c);

			for (int run = 0; run < options.getSimulationRuns(); run++) {
				final Trajectory x;
				x = ssa.generateTimeseries(0, simulationEndTime, m.getTimes());

				// copy the information about the current run in the enhanced
				// time-series
				for (int c = 0; c < modelVariables.length; c++)
					values[c] = x.getValues(c);

				// everything is now in place to do the model checking
				final boolean satisfied = property.evaluate(enhanced, 0);
				paramValueObservations[i] += satisfied ? 1 : 0;
			}
			paramValueObservations[i] /= options.getSimulationRuns();
		}

		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		if (options.isDebugEnabled()) {
			System.out.println("          Mean calculation:  " + elapsed_mean
					+ " sec");
			System.out.println("Statistical Model Checking:  "
					+ (elapsed - elapsed_mean) + " sec");
		}
		return new GpDataset(paramValueSet, paramValueObservations);
	}

	/**
	 * This method performs statistical model checking and returns the results
	 * in a form that can be used by a GP in terms of smoothedMC.<br>
	 * <br>
	 * NOTE: The time-series that are checked also contain derivative
	 * information. The derivatives are approximated by the slope of subsequent
	 * points. <br>
	 * <br>
	 * WANRING: Only works with time-series (no raw trajectories)
	 * 
	 * @param biopepaFile
	 *            The model in biopepa spec
	 * @param mitlText
	 *            A string containing the formula to be checked
	 * @param parameters
	 * @param options
	 */
	public static GpDataset statisticalMCwithDerivatives(
			BiopepaFile biopepaFile, String mitlText, Parameter[] parameters,
			Options options) {

		long t0 = System.currentTimeMillis();
		double elapsed;

		final int timepoints = options.getSimulationTimepoints();
		final double simulationEndTime = options.getSimulationEndTime();

		// sample over the parameter space
		// and reserve space for the observations
		final GridSampler sampler = options.getSampler();
		final double[][] paramValueSet = sampler.sample(options.getN(),
				parameters);
		final int datapoints = paramValueSet.length;
		final double[] paramValueObservations = new double[datapoints];

		final String[] paramNames = new String[parameters.length];
		for (int i = 0; i < paramNames.length; i++)
			paramNames[i] = parameters[i].getName();

		// --------------------------------------------------------------------
		// create a new context that contains both the model variables
		// and the variables that hold the derivatives
		Context context = new Context();
		CTMCModel model = biopepaFile.getModel();
		Variable[] modelVariables = model.getStateVariables().getVariables();
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName(), context);
		// naming convention: the variables that hold the derivatives
		// have suffix '_d'
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName() + "_d", context);
		// --------------------------------------------------------------------

		for (int i = 0; i < datapoints; i++) {

			// extract the CTMCModel for the current parameter values
			model = biopepaFile.getModel(paramNames, paramValueSet[i]);

			// parse the property
			MitlFactory factory = new MitlFactory(context);
			MitlPropertiesList l = factory.constructProperties(mitlText);
			ArrayList<MiTL> list = l.getProperties();
			final MiTL property = list.get(0);

			StochasticSimulationAlgorithm ssa = new GillespieSSA(model);
			double[][] values = new double[2 * modelVariables.length][timepoints];

			for (int run = 0; run < options.getSimulationRuns(); run++) {
				Trajectory x;
				x = ssa.generateTimeseries(0, simulationEndTime, timepoints);
				final Trajectory enhanced = new Trajectory(x.getTimes(),
						context, values);

				// copy the information about the current run in the enhanced
				// time-series
				for (int c = 0; c < modelVariables.length; c++)
					values[c] = x.getValues(c);

				// copy the information about the derivatives in the enhanced
				// time-series
				for (int c = 0; c < modelVariables.length; c++) {
					values[c + modelVariables.length][0] = 0;
					for (int timeIdx = 1; timeIdx < timepoints; timeIdx++)
						values[c + modelVariables.length][timeIdx] = (values[c][timeIdx] - values[c][timeIdx - 1])
								/ (x.getTimes()[timeIdx] - x.getTimes()[timeIdx - 1]);
				}

				// everything is now in place to do the model checking
				final boolean satisfied = property.evaluate(enhanced, 0);
				paramValueObservations[i] += satisfied ? 1 : 0;
			}
			paramValueObservations[i] /= options.getSimulationRuns();
		}

		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		if (options.isDebugEnabled())
			System.out.println("Statistical Model Checking:  " + elapsed
					+ " sec");
		return new GpDataset(paramValueSet, paramValueObservations);
	}

	/**
	 * This method performs statistical model checking and returns the results
	 * in a form that can be used by a GP in terms of smoothedMC.<br>
	 * <br>
	 * NOTE: The time-series that are checked also contain moving-average
	 * information. <br>
	 * 
	 * @param biopepaFile
	 *            The model in biopepa spec
	 * @param mitlText
	 *            A string containing the formula to be checked
	 * @param parameters
	 * @param options
	 */
	public static GpDataset statisticalMCwithMovingAvg(BiopepaFile biopepaFile,
			String mitlText, Parameter[] parameters, Options options) {

		long t0 = System.currentTimeMillis();
		double elapsed;

		final int timepoints = options.getSimulationTimepoints();
		final double simulationEndTime = options.getSimulationEndTime();

		// sample over the parameter space
		// and reserve space for the observations
		final GridSampler sampler = options.getSampler();
		final double[][] paramValueSet = sampler.sample(options.getN(),
				parameters);
		final int datapoints = paramValueSet.length;
		final double[] paramValueObservations = new double[datapoints];

		final String[] paramNames = new String[parameters.length];
		for (int i = 0; i < paramNames.length; i++)
			paramNames[i] = parameters[i].getName();

		// --------------------------------------------------------------------
		// create a new context that contains both the model variables
		// and the variables that hold the derivatives
		Context context = new Context();
		CTMCModel model = biopepaFile.getModel();
		Variable[] modelVariables = model.getStateVariables().getVariables();
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName(), context);
		// naming convention: the variables that hold the derivatives
		// have suffix '_mavg'
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName() + "_mavg", context);
		// --------------------------------------------------------------------

		for (int i = 0; i < datapoints; i++) {

			// extract the CTMCModel for the current parameter values
			model = biopepaFile.getModel(paramNames, paramValueSet[i]);

			// parse the property
			MitlFactory factory = new MitlFactory(context);
			MitlPropertiesList l = factory.constructProperties(mitlText);
			ArrayList<MiTL> list = l.getProperties();
			final MiTL property = list.get(0);

			StochasticSimulationAlgorithm ssa = new GillespieSSA(model);

			for (int run = 0; run < options.getSimulationRuns(); run++) {
				Trajectory x;
				x = ssa.generateTimeseries(0, simulationEndTime, timepoints);
				Trajectory mavg = extractMovingAvg(x);
				x = combine(x, mavg);
				x = new Trajectory(x.getTimes(), context, x.getValues());

				// everything is now in place to do the model checking
				final boolean satisfied = property.evaluate(x, 0);
				paramValueObservations[i] += satisfied ? 1 : 0;
			}
			paramValueObservations[i] /= options.getSimulationRuns();
		}

		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		if (options.isDebugEnabled())
			System.out.println("Statistical Model Checking:  " + elapsed
					+ " sec");
		return new GpDataset(paramValueSet, paramValueObservations);
	}

	public static GpDataset statisticalMCwithMovingVar(BiopepaFile biopepaFile,
			String mitlText, Parameter[] parameters, Options options) {

		long t0 = System.currentTimeMillis();
		double elapsed;

		final int timepoints = options.getSimulationTimepoints();
		final double simulationEndTime = options.getSimulationEndTime();

		// sample over the parameter space
		// and reserve space for the observations
		final GridSampler sampler = options.getSampler();
		final double[][] paramValueSet = sampler.sample(options.getN(),
				parameters);
		final int datapoints = paramValueSet.length;
		final double[] paramValueObservations = new double[datapoints];

		final String[] paramNames = new String[parameters.length];
		for (int i = 0; i < paramNames.length; i++)
			paramNames[i] = parameters[i].getName();

		// --------------------------------------------------------------------
		// create a new context that contains both the model variables
		// and the variables that hold the derivatives
		Context context = new Context();
		CTMCModel model = biopepaFile.getModel();
		Variable[] modelVariables = model.getStateVariables().getVariables();
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName(), context);
		// naming convention: the variables that hold the derivatives
		// have suffix '_mvar'
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName() + "_mvar", context);
		// --------------------------------------------------------------------

		for (int i = 0; i < datapoints; i++) {

			// extract the CTMCModel for the current parameter values
			model = biopepaFile.getModel(paramNames, paramValueSet[i]);
			// parse the property
			MitlFactory factory = new MitlFactory(context);
			MitlPropertiesList l = factory.constructProperties(mitlText);
			ArrayList<MiTL> list = l.getProperties();
			final MiTL property = list.get(0);

			StochasticSimulationAlgorithm ssa = new GillespieSSA(model);

			for (int run = 0; run < options.getSimulationRuns(); run++) {
				Trajectory x;
				x = ssa.generateTimeseries(0, simulationEndTime, timepoints);
				Trajectory mavg = extractMovingVar(x);
				x = combine(x, mavg);
				x = new Trajectory(x.getTimes(), context, x.getValues());

				// everything is now in place to do the model checking
				final boolean satisfied = property.evaluate(x, 0);
				paramValueObservations[i] += satisfied ? 1 : 0;

				if (run == 0)
					if (satisfied)
						System.currentTimeMillis();
			}
			paramValueObservations[i] /= options.getSimulationRuns();
		}

		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		if (options.isDebugEnabled())
			System.out.println("Statistical Model Checking:  " + elapsed
					+ " sec");
		return new GpDataset(paramValueSet, paramValueObservations);
	}

	public static final Trajectory combine(Trajectory x, Trajectory y) {
		final double[] times = x.getTimes();
		if (!Arrays.equals(times, y.getTimes()))
			throw new IllegalArgumentException("Incompatible trajectories");

		final Context context = new Context();
		Variable[] variables;
		variables = x.getContext().getVariables();
		final int n1 = variables.length;
		for (int c = 0; c < n1; c++)
			new Variable(variables[c].getName(), context);
		variables = y.getContext().getVariables();
		final int n2 = variables.length;
		for (int c = 0; c < n2; c++)
			new Variable(variables[c].getName(), context);

		final double[][] values = new double[n1 + n2][];
		for (int i = 0; i < n1; i++)
			values[i] = x.getValues(i).clone();
		for (int i = 0; i < n2; i++)
			values[i + n1] = y.getValues(i).clone();
		return new Trajectory(times, context, values);
	}

	public static final Trajectory extractMovingAvg(Trajectory x) {
		Context context = new Context();

		Variable[] modelVariables = x.getContext().getVariables();
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName() + "_mavg", context);
		// naming convention: the variables that hold the moving variance info
		// have suffix '_mavg'

		final double[][] values = new double[modelVariables.length][];
		for (int c = 0; c < modelVariables.length; c++)
			values[c] = extractMovingAvg(x.getValues(c),
					MOVING_WINDOW_HALF_SIZE);

		final Trajectory extract = new Trajectory(x.getTimes(), context, values);
		return extract;
	}

	private static final double[] extractMovingAvg(final double[] x, int n) {
		final double[] y = new double[x.length];
		final ArrayList<Double> collection = new ArrayList<Double>(n);

		for (int i = 0; i < y.length; i++) {
			collection.clear();
			collection.add(x[i]);
			for (int j = 1; j <= n; j++)
				if (i + j < x.length)
					collection.add(x[i + j]);
			for (int j = 1; j <= n; j++)
				if (i - j >= 0)
					collection.add(x[i - j]);
			y[i] = average(collection);
		}
		return y;
	}

	@Deprecated
	public static final Trajectory extractMovingVar(Trajectory x) {
		Context context = new Context();

		Variable[] modelVariables = x.getContext().getVariables();
		for (int c = 0; c < modelVariables.length; c++)
			new Variable(modelVariables[c].getName() + "_mavg", context);
		// naming convention: the variables that hold the moving variance info
		// have suffix '_mavg'

		final double[][] values = new double[modelVariables.length][];
		for (int c = 0; c < modelVariables.length; c++)
			values[c] = extractMovingVar(x.getValues(c),
					MOVING_WINDOW_HALF_SIZE);

		final Trajectory extract = new Trajectory(x.getTimes(), context, values);
		return extract;
	}

	@Deprecated
	private static final double[] extractMovingVar(final double[] x, int n) {
		final double[] y = new double[x.length];
		final ArrayList<Double> collection = new ArrayList<Double>(2 * n + 1);

		for (int i = 0; i < y.length; i++) {
			collection.clear();
			collection.add(x[i]);
			for (int j = 1; j <= n; j++) {
				if (i + j < x.length)
					collection.add(x[i + j]);
				if (i - j >= 0)
					collection.add(x[i - j]);
			}

			final double EX = average(collection);
			for (int j = 0; j < collection.size(); j++)
				collection.set(j, collection.get(j) * collection.get(j));
			final double EX2 = average(collection);
			y[i] = EX2 - EX * EX;

			y[i] = Math.sqrt(y[i]);
			if (EX == 0)
				y[i] = 0;
			else
				y[i] /= EX;
		}
		return y;
	}

	private static double sum(ArrayList<Double> list) {
		double sum = 0;
		for (int i = 0; i < list.size(); i++)
			sum = sum + list.get(i);
		return sum;
	}

	private static double average(ArrayList<Double> list) {
		return sum(list) / list.size();
	}

	public static final void binomial2matlab(GpDataset result, int n,
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
		final int dim = result.getDimension();
		for (int i = 0; i < result.getSize(); i++) {
			for (int d = 0; d < dim - 1; d++)
				fw.append(result.getInstance(i)[d] + ",\t");
			fw.append(result.getInstance(i)[dim - 1] + "\n");
		}
		fw.close();

		final double alpha = 0.05;
		final double z = 1 - 0.5 * alpha;

		fw = new FileWriter(outDir + "/" + modelName + "_pred.csv");
		for (int i = 0; i < result.getSize(); i++) {
			final double p = result.getTargets()[i];
			final double c = z * Math.sqrt(1d / ((double) 100) * p * (1 - p));
			fw.append(p + ",\t");
			fw.append(p - c + ",\t");
			fw.append(p + c + "\n");
		}
		fw.close();
	}

	/**
	 * Convenience method to create a RBF kernel with "reasonable"
	 * hyper-parameter values.
	 */
	public static KernelFunction defaultKernelRBF(Parameter[] params,
			final boolean ARD) {
		final double[] lengthscales = new double[params.length];
		for (int i = 0; i < lengthscales.length; i++)
			lengthscales[i] = (params[i].getUpperBound() - params[i]
					.getLowerBound()) / 10.0;
		if (ARD) {
			final double[] hyp = new double[lengthscales.length + 1];
			hyp[0] = 1;
			System.arraycopy(lengthscales, 0, hyp, 1, lengthscales.length);
			return new KernelRbfARD(hyp);
		}
		double sum = 0;
		for (int i = 0; i < lengthscales.length; i++)
			sum += lengthscales[i];
		return new KernelRBF(1, sum / lengthscales.length);
	}

}
