package gridSampling;

import java.util.Random;

public interface GridSampler {

	final static Random rand = new Random();

	public double[][] sample(int n, double[] lbounds, double[] ubounds);

}
