package mitl;

import ssa.Trajectory;

public final class MitlGlobally extends MiTL {

	final private double t1;
	final private double t2;
	final private MiTL formula;

	public MitlGlobally(final double t1, final double t2, final MiTL f) {
		this.t1 = t1;
		this.t2 = t2;
		this.formula = f;
	}

	@Override
	public boolean evaluate(Trajectory x, double t) {
		final double t1 = this.t1 + t;
		final double t2 = this.t2 + t;
		final double[] times = x.getTimes();
		final int index1 = timeIndexAfter(times, t1);
		final int index2 = timeIndexUntil(times, t2);
		for (int i = index1; i <= index2; i++)
			if (!formula.evaluate(x, times[i]))
				return false;
		return true;
	}

    @Override
    public double evaluateValue(Trajectory x, double t) {
        double minValue = Double.MAX_VALUE;
        final double t1 = this.t1 + t;
        final double t2 = this.t2 + t;
        final double[] times = x.getTimes();
        final int index1 = timeIndexAfter(times, t1);
        final int index2 = timeIndexUntil(times, t2);
        for (int i = index1; i <= index2; i++) {
            double value =     formula.evaluateValue(x, times[i]);
            if (value < minValue) {
                minValue = value;
            }
        }
        return minValue;
    }

    @Override
	public String toString() {
		return "G[" + t1 + ", " + t2 + "]" + " (" + formula + ")";
	}
}
