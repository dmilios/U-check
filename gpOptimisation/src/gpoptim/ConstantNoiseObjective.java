package gpoptim;

import optim.ObjectiveFunction;

final class ConstantNoiseObjective implements NoisyObjectiveFunction {

	final private ObjectiveFunction func;
	final private double variance;

	public ConstantNoiseObjective(ObjectiveFunction func, double variance) {
		this.func = func;
		this.variance = variance;
	}

	@Override
	public double getValueAt(double... point) {
		return func.getValueAt(point);
	}

	@Override
	public double getVarianceAt(double... point) {
		return variance;
	}

}
