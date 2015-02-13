package rand;

public abstract class RandomEngine {

	private long seed;

	public RandomEngine(long seed) {
		setSeed(seed);
	}

	final public long getSeed() {
		return seed;
	}

	final public void setSeed(long seed) {
		this.seed = seed;
		setSeedInternal(seed);
	}

	abstract protected void setSeedInternal(long seed);

	abstract public double nextDouble();

	final public double nextExponential(final double rate) {
		return -Math.log(1 - nextDouble()) / rate;
	}

	final public int nextCategorical(final double[] cumulativeValues) {
		int first = 0;
		int last = cumulativeValues.length - 1;
		final double roulette = cumulativeValues[last] * nextDouble();
		while (first != last) {
			final int mid = (first + last) >> 1;
			if (roulette < cumulativeValues[mid])
				last = mid;
			else if (roulette > cumulativeValues[mid])
				first = mid + 1;
			else
				return mid;
		}
		return last;
	}

}
