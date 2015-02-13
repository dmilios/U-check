package gp;

import gp.AbstractGP;
import optim.ObjectiveFunction;

final public class HyperparamLogLikelihood implements ObjectiveFunction {

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
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return lik;
	}

}
