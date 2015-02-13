package mitl;

import ssa.Trajectory;

public final class MitlUntil extends MiTL {

	final private MiTL formula1;
	final private double t1;
	final private double t2;
	final private MiTL formula2;

	public MitlUntil(final MiTL f1, final double t1, final double t2,
			final MiTL f2) {
		this.formula1 = f1;
		this.t1 = t1;
		this.t2 = t2;
		this.formula2 = f2;
	}

	@Override
	public boolean evaluate(Trajectory x, double t) {
		final double t1 = this.t1 + t;
		final double t2 = this.t2 + t;
		double[] times;
		int index1, index2;

		double tprime = 0;
		boolean f2true = false;
		times = x.getTimes();
		index1 = timeIndexAfter(times, t1);
		index2 = timeIndexUntil(times, t2);
		for (int i = index1; i <= index2; i++)
			if (formula2.evaluate(x, times[i])) {
				f2true = true;
				tprime = times[i];
				break;
			}
		if (!f2true)
			return false;

		// FIXME: Not sure if 't' or 'this.t1 + t'
		// In the CAV paper, it is 't'
		// but I think 'this.t1 + t' is correct instead
		index1 = timeIndexAfter(times, t);
				
		index2 = timeIndexAfter(times, tprime);
		for (int i = index1; i <= index2; i++)
			if (!formula1.evaluate(x, times[i]))
				return false;

		return true;
	}

	@Override
	public String toString() {
		if (formula1.toString().endsWith(" "))
			return formula1 + "U[" + t1 + ", " + t2 + "]" + " (" + formula2
					+ ")";
		return formula1 + " U[" + t1 + ", " + t2 + "]" + " (" + formula2 + ")";
	}

}
