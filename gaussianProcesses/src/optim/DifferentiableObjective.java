package optim;

public interface DifferentiableObjective extends ObjectiveFunction {

	public double[] getGradientAt(double... point);

}
