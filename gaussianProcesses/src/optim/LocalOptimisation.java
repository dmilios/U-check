package optim;


public abstract class LocalOptimisation {

	abstract public PointValue optimise(ObjectiveFunction func,
			double[] init);

}
