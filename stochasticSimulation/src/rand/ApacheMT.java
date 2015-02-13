package rand;

import org.apache.commons.math3.random.MersenneTwister;

public class ApacheMT extends RandomEngine {

	private MersenneTwister rand;

	public ApacheMT() {
		super(System.nanoTime());
	}

	public ApacheMT(long seed) {
		super(seed);
	}

	@Override
	protected void setSeedInternal(long seed) {
		rand = new MersenneTwister(seed);
	}

	@Override
	public double nextDouble() {
		return rand.nextDouble();
	}

}
