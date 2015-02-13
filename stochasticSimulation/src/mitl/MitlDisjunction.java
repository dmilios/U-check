package mitl;

import ssa.Trajectory;

public class MitlDisjunction extends MiTL {

	final private MiTL formula1;
	final private MiTL formula2;

	public MitlDisjunction(final MiTL formula1, final MiTL formula2) {
		this.formula1 = formula1;
		this.formula2 = formula2;
	}

	@Override
	public boolean evaluate(Trajectory x, double t) {
		return this.formula1.evaluate(x, t) || this.formula2.evaluate(x, t);
	}

	@Override
	public String toString() {
		return "(" + formula1 + ") | (" + formula2 + ")";
	}

}
