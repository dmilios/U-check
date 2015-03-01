package gpoMC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import parsers.MitlFactory;
import priors.Prior;
import biopepa.BiopepaFile;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import ssa.CTMCModel;
import ssa.GillespieSSA;
import ssa.StochasticSimulationAlgorithm;
import ssa.Trajectory;
import gpoptim.NoisyObjectiveFunction;

public class LFFLogPosterior implements NoisyObjectiveFunction {

	private CTMCModel model;
	final private Parameter[] params;
	final private Prior[] priors;

	private MiTL[] formulae;
	final private boolean[][] observations;
	private LFFOptions options = new LFFOptions();

	private double[] cachedPoint = new double[0];
	private double cachedVariance = 0;

	/** Need this for now... */
	@Deprecated
	BiopepaFile biopepa;
	/** Need this for now... */
	@Deprecated
	String mitlText;

	// Need this for now...
	@Deprecated
	public void setBiopepa(BiopepaFile biopepa) {
		this.biopepa = biopepa;
	}

	/** Need this for now... */
	@Deprecated
	public void setMitlText(String mitlText) {
		this.mitlText = mitlText;
	}

	public LFFLogPosterior(CTMCModel astModel, Parameter[] params,
			Prior[] priors, MiTL[] formulae, boolean[][] observations) {
		this.model = astModel;
		this.params = params;
		this.formulae = formulae;
		this.observations = observations;
		this.priors = priors;
	}

	public void setOptions(LFFOptions options) {
		this.options = options;
	}

	@Override
	public double getVarianceAt(double... point) {
		if (!Arrays.equals(cachedPoint, point))
			getValueAt(point);
		return cachedVariance;
	}

	@Override
	public double getValueAt(double... point) {
		for (final double p : point)
			if (p < 0)
				throw new IllegalArgumentException("Negative kinetic parameter");

		final int dim = point.length;
		for (int i = 0; i < dim; i++) {
			String name = params[i].getName();
			double value = point[i];
			// This does nothing for now...
			// PROBLEM:
			// Bio-PEPA compiled models do not contain parameter information
			model.setParameterValue(name, value);
		}

		// need this for now...
		String[] names = new String[dim];
		for (int i = 0; i < dim; i++)
			names[i] = params[i].getName();
		model = biopepa.getModel(names, point);

		MitlFactory factory = new MitlFactory(model.getStateVariables());
		MitlPropertiesList l = factory.constructProperties(mitlText);
		ArrayList<MiTL> list = l.getProperties();
		formulae = new MiTL[list.size()];
		list.toArray(formulae);
		//

		cachedPoint = point.clone();
		return logLikelihoodOfObservations(this.model) + logPriorAt(point);
	}

	public double logPriorAt(double[] point) {
		double logProbability = 0;
		for (int i = 0; i < point.length; i++)
			logProbability += priors[i].logProbability(point[i]);
		return logProbability;
	}

	final private boolean[][] createStatisticalModelCheckingData(
			StochasticSimulationAlgorithm ssa) {
		final int numberOfFormulae = observations[0].length;
		final int runs = options.getSimulationRuns();
		final double stopTime = options.getSimulationEndTime();
		final int timepoints = options.getSimulationTimepoints();
		final boolean[][] smcData = new boolean[runs][numberOfFormulae];
		for (int i = 0; i < runs; i++) {
			final Trajectory x = ssa
					.generateTimeseries(0, stopTime, timepoints);
			for (int j = 0; j < numberOfFormulae; j++)
				smcData[i][j] = formulae[j].evaluate(x, 0);
		}
		return smcData;
	}

	final private double logLikelihoodOfObservations(CTMCModel model) {
		StochasticSimulationAlgorithm ssa = new GillespieSSA(model);
		final boolean[][] smcData = createStatisticalModelCheckingData(ssa);
		final double l = estimateLogLikelihood(observations, smcData, true);
		return l;
	}

	private final double estimateLogLikelihood(boolean[][] data,
			boolean[][] smcData, boolean doVariance) {

		final int mitlDim = data[0].length;
		final int jointDim = (int) Math.pow(2, mitlDim);

		final double[] prior = new double[jointDim];
		for (int i = 0; i < jointDim; i++)
			prior[i] = 1;

		final double[] smcCount = new double[jointDim];
		final double[] obsCount = new double[jointDim];

		for (int i = 0; i < smcData.length; i++)
			smcCount[encode(smcData[i])]++;
		for (int i = 0; i < data.length; i++)
			obsCount[encode(data[i])]++;

		final double[] posterior = dirichletPosterior(smcCount, prior);
		final double[] prob = dirichletMean(posterior);
		final double l = logLikelihoodMultinomial(obsCount, prob);
		if (doVariance)
			varianceBootstrap(obsCount, smcData, prior, 100);
		return l;
	}

	static private final double[] dirichletPosterior(double[] counts,
			double[] priorCounts) {
		final int dim = counts.length;
		final double[] posterior = new double[dim];
		for (int i = 0; i < dim; i++)
			posterior[i] = counts[i] + priorCounts[i];
		return posterior;
	}

	static private final double[] dirichletMean(double[] a) {
		final int dim = a.length;
		double a0 = 0;
		for (int i = 0; i < dim; i++)
			a0 += a[i];
		final double[] mean = new double[dim];
		for (int i = 0; i < dim; i++)
			mean[i] = a[i] / a0;
		return mean;
	}

	static private final double logLikelihoodMultinomial(double[] counts,
			double[] prob) {
		final int dim = counts.length;
		final double[] estLogProb = new double[dim];
		for (int i = 0; i < dim; i++)
			estLogProb[i] = Math.log(prob[i]);
		final double logl = dot(counts, estLogProb);

		if (Double.isNaN(logl))
			System.currentTimeMillis();

		return logl;
	}

	private final void varianceBootstrap(double[] obsCount,
			boolean[][] smcData, double[] prior, int bootstrapSize) {
		final int jointDim = obsCount.length;
		final double[] logl = new double[bootstrapSize];
		for (int b = 0; b < bootstrapSize; b++) {
			boolean[][] smcResampled = dataSample(smcData, smcData.length);
			final double[] resampledCount = new double[jointDim];
			for (int i = 0; i < smcResampled.length; i++)
				resampledCount[encode(smcResampled[i])]++;

			final double[] post = dirichletPosterior(resampledCount, prior);
			final double[] prob = dirichletMean(post);
			logl[b] = logLikelihoodMultinomial(obsCount, prob);
		}
		cachedVariance = variance(logl);
	}

	final static private double dot(final double[] x1, final double[] x2) {
		final int n = x1.length;
		double sum = 0;
		for (int i = 0; i < n; i++)
			sum += x1[i] * x2[i];
		return sum;
	}

	static private final int encode(final boolean[] bits) {
		int n = 0;
		for (final boolean bit : bits)
			n = (n << 1) + (bit ? 1 : 0);
		return n;
	}

	final static private double variance(double[] vector) {
		double mean = 0;
		for (double v : vector)
			mean += v;
		mean /= (double) vector.length;
		double sum = 0;
		for (int i = 0; i < vector.length; i++) {
			final double aux = vector[i] - mean;
			sum += aux * aux;
		}
		if (vector.length > 1)
			return sum / (double) (vector.length - 1);
		return 0;
	}

	final private Random rand = new Random();

	/**
	 * Returns k observations sampled uniformly at random, with replacement,
	 * from data.
	 * */
	final private boolean[][] dataSample(boolean[][] data, int k) {
		final int n = data.length;
		final int m = data[0].length;
		final boolean[][] sample = new boolean[k][m];
		for (int i = 0; i < k; i++) {
			final int j = rand.nextInt(n);
			System.arraycopy(data[j], 0, sample[i], 0, m);
		}
		return sample;
	}

}
