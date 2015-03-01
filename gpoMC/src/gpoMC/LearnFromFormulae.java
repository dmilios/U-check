package gpoMC;

import priors.Prior;
import priors.UniformPrior;
import gpoptim.GPOptimisation;
import gpoptim.GpoResult;
import biopepa.BiopepaFile;
import mitl.MiTL;
import ssa.CTMCModel;

public final class LearnFromFormulae {

	private CTMCModel model;
	private Parameter[] params;
	private Prior[] priors;
	private LFFOptions options = new LFFOptions();

	/** Need this for now... */
	@Deprecated
	private BiopepaFile biopepa;

	@Deprecated
	/** Need this for now... */
	String mitlText;

	/** Need this for now... */
	@Deprecated
	public void setBiopepa(BiopepaFile biopepa) {
		this.biopepa = biopepa;
	}

	/** Need this for now... */
	@Deprecated
	public void setMitlText(String mitlText) {
		this.mitlText = mitlText;
	}

	public LFFOptions getOptions() {
		return options;
	}

	public void setOptions(LFFOptions options) {
		this.options = options;
	}

	public void setModel(CTMCModel model) {
		this.model = model;
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

	@SuppressWarnings("deprecation")
	public GpoResult performInference(MiTL[] formulae, boolean[][] observations) {
		LFFLogPosterior logPosterior = new LFFLogPosterior(model, params,
				priors, formulae, observations);

		logPosterior.setBiopepa(biopepa);
		logPosterior.setMitlText(mitlText);

		GPOptimisation gpo = new GPOptimisation();
		logPosterior.setOptions(options);
		gpo.setOptions(options.getGpoOptions());

		final double[] lbounds = new double[params.length];
		final double[] ubounds = new double[params.length];
		for (int i = 0; i < params.length; i++) {
			lbounds[i] = params[i].getLowerBound();
			ubounds[i] = params[i].getUpperBound();
		}
		return gpo.optimise(logPosterior, lbounds, ubounds);
	}

}
