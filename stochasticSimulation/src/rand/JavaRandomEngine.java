package rand;

import java.util.Random;

public class JavaRandomEngine extends RandomEngine {

	private Random rand = new Random();

	public JavaRandomEngine() {
		super(System.nanoTime());
	}

	public JavaRandomEngine(long seed) {
		super(seed);
	}

	@Override
	protected void setSeedInternal(long seed) {
		rand = new Random(seed);
	}

	@Override
	public double nextDouble() {
		return rand.nextDouble();
	}

}
