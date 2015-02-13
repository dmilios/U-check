package mitl;

import ssa.Trajectory;

public final class MitlFalse extends MiTL {

	@Override
	public boolean evaluate(Trajectory x, double t) {
		return false;
	}

	@Override
	public String toString() {
		return "false";
	}

}
