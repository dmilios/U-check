package gpoptim;

import optim.ObjectiveFunction;

public interface NoisyObjectiveFunction extends ObjectiveFunction {
	
	public double getVarianceAt(double... point);

}
