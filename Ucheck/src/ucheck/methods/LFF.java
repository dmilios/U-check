package ucheck.methods;

import priors.Prior;
import priors.UniformPrior;
import ucheck.methods.UcheckModel;
import gpoMC.LFFOptions;
import gpoMC.Parameter;
import gpoptim.GPOptimisation;
import gpoptim.GpoResult;

public class LFF {

	private UcheckModel model;
	private Parameter[] params;
	private Prior[] priors;
	private LFFOptions options = new LFFOptions();

	public void setModel(UcheckModel model) {
		this.model = model;
	}

	public LFFOptions getOptions() {
		return options;
	}

	public void setOptions(LFFOptions options) {
		this.options = options;
	}

	/**
	 * Set the parameters to be examined.<br>
	 * By default, the parameter priors will be uniform.
	 */
	public void setParams(Parameter[] params) {
		this.params = params;
		this.priors = new Prior[params.length];
		for (int i = 0; i < priors.length; i++)
			priors[i] = new UniformPrior(params[i].getLowerBound(),
					params[i].getUpperBound());
	}

	public void setPriors(Prior[] priors) {
		this.priors = priors;
	}

	public GpoResult performInference(String[] formulae,
			boolean[][] observations) {

		final LFFLogPost post;
		post = new LFFLogPost(model, params, priors, formulae, observations);
		post.setOptions(options);

		GPOptimisation gpo = new GPOptimisation();
		gpo.setOptions(options.getGpoOptions());

		final double[] lbounds = new double[params.length];
		final double[] ubounds = new double[params.length];
		for (int i = 0; i < params.length; i++) {
			lbounds[i] = params[i].getLowerBound();
			ubounds[i] = params[i].getUpperBound();
		}
		return gpo.optimise(post, lbounds, ubounds);
	}

}
