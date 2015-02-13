package smoothedMC.gridSampling;

import java.util.Random;

import smoothedMC.Parameter;

public interface GridSampler {

	final static Random rand = new Random();

	public double[][] sample(int n, Parameter[] params);

}
