package gp;

import linalg.NonPosDefMatrixException;
import gp.AbstractGP;
import optim.DifferentiableObjective;

final public class HyperparamLogLikelihood implements DifferentiableObjective {

	final private AbstractGP<?> gp;
	final private boolean logspace;

	public HyperparamLogLikelihood(AbstractGP<?> gp) {
		this(gp, false);
	}

	public HyperparamLogLikelihood(AbstractGP<?> gp, boolean logspace) {
		this.gp = gp;
		this.logspace = logspace;
	}

	@Override
	public double getValueAt(double... point) {
		final double[] vec = point.clone();
		if (logspace)
			for (int i = 0; i < vec.length; i++)
				vec[i] = Math.exp(vec[i]);
		gp.getKernel().setHyperarameters(vec);
		double lik = Double.NEGATIVE_INFINITY;
		try {
			lik = gp.getMarginalLikelihood();
		} catch (NonPosDefMatrixException e) {
			// System.out.println(e.getMessage());
		}
		return lik;
	}

	@Override
	public double[] getGradientAt(double... point) {
		final double[] vec = point.clone();
		if (logspace)
			for (int i = 0; i < vec.length; i++)
				vec[i] = Math.exp(vec[i]);
		gp.getKernel().setHyperarameters(vec);
		try {
			final double[] grad = gp.getMarginalLikelihoodGradient();
			return grad;
		} catch (NonPosDefMatrixException e) {
			throw new IllegalStateException("Don't know what to do here!", e);
		}

	}

}
