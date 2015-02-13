package optim;

import java.util.Arrays;

public final class PointValue {

	final private double[] point;
	final private double value;

	public PointValue(double[] point, double value) {
		this.point = point;
		this.value = value;
	}

	public double[] getPoint() {
		return point;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Arrays.toString(point) + ": " + value;
	}

}
