package mitl;

import model.Trajectory;

public final class MitlFalse extends MiTL {

	@Override
	public boolean evaluate(Trajectory x, double t) {
		return false;
	}

    @Override
    public double evaluateValue(Trajectory x, double t) {
        return -Double.MAX_VALUE;
    }

    @Override
	public String toString() {
		return "false";
	}

}
