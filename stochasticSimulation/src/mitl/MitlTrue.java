package mitl;

import model.Trajectory;

public final class MitlTrue extends MiTL {

	@Override
	public boolean evaluate(Trajectory x, double t) {
		return true;
	}

    @Override
    public double evaluateValue(Trajectory x, double t) {
        return Double.MAX_VALUE;
    }

    @Override
	public String toString() {
		return "true";
	}

}
