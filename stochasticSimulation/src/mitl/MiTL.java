package mitl;

import model.Trajectory;

public abstract class MiTL {

	abstract public boolean evaluate(Trajectory x, double t);
	abstract public double evaluateValue(Trajectory x, double t);

	final protected int timeIndexAfter(final double[] times, final double t) {
		for (int i = 0; i < times.length; i++)
			if (times[i] >= t)
				return i;
		return times.length - 1;
	}

	final protected int timeIndexUntil(final double[] times, final double t) {
		for (int i = 0; i < times.length; i++)
			if (times[i] > t)
				return i - 1;
			else if (times[i] == t)
				return i;
		return times.length - 1;
	}

}
