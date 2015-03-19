package gp;

import linalg.NonPosDefMatrixException;
import gp.AbstractGP;
import optim.DifferentiableObjective;

final public class HyperparamLogLikelihood implements DifferentiableObjective {

	final private AbstractGP<?> gp;

	public HyperparamLogLikelihood(AbstractGP<?> gp) {
		this.gp = gp;
	}

	@Override
	public double getValueAt(double... point) {
		gp.getKernel().setHyperarameters(point);
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
		gp.getKernel().setHyperarameters(point);
		try {
			final double[] grad = gp.getMarginalLikelihoodGradient();
			return grad;
		} catch (NonPosDefMatrixException e) {
			throw new IllegalStateException("Don't know what to do here!", e);
		}

	}

}
