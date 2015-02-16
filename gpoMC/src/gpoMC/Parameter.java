package gpoMC;

final public class Parameter {

	final private String name;
	final private double lowerBound;
	final private double upperBound;

	public Parameter(final String name, final double lBound, final double uBound) {
		this.name = name;
		this.lowerBound = lBound;
		this.upperBound = uBound;
		if (lBound >= uBound)
			throw new IllegalArgumentException("lBound >= uBound");
	}

	public String getName() {
		return name;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(": lB=");
		sb.append(lowerBound);
		sb.append(", uB=");
		sb.append(upperBound);
		return sb.toString();
	}

}
