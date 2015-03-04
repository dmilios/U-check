package lff;

import lff.LFFOptions;
import lff.Parameter;
import modelChecking.MitlModelChecker;
import priors.Prior;
import priors.UniformPrior;
import gpoptim.GPOptimisation;
import gpoptim.GpoResult;

public class LearnFromFormulae {

	private MitlModelChecker modelChecker;
	private Parameter[] params;
	private Prior[] priors;
	private LFFOptions options = new LFFOptions();

	public LearnFromFormulae(MitlModelChecker modelChecker) {
		this.modelChecker = modelChecker;
	}

	public void setModelChecker(MitlModelChecker modelChecker) {
		this.modelChecker = modelChecker;
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

	public GpoResult performInference(boolean[][] observations) {
		final LFFLogPosterior post;
		post = new LFFLogPosterior(modelChecker, params, priors, observations);
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
