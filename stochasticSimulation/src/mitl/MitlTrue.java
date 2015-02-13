package mitl;

import ssa.Trajectory;

public final class MitlTrue extends MiTL {

	@Override
	public boolean evaluate(Trajectory x, double t) {
		return true;
	}

	@Override
	public String toString() {
		return "true";
	}

}
