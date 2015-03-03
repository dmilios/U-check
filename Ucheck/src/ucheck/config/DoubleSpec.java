package ucheck.config;

public class DoubleSpec extends PropertySpec {

	final private double min;
	final private double max;
	final private boolean minInclusive;
	final private boolean maxInclusive;

	public DoubleSpec(String name, double defaultValue) {
		this(name, defaultValue, Double.MIN_VALUE, true);
	}

	public DoubleSpec(String name, double defaultValue, double min,
			boolean minInclusive) {
		this(name, defaultValue, min, minInclusive, Double.MAX_VALUE, true);
	}

	public DoubleSpec(String name, double defaultValue, double min,
			boolean minInclusive, double max, boolean maxInclusive) {
		super(name, defaultValue);
		this.min = min;
		this.max = max;
		this.minInclusive = minInclusive;
		this.maxInclusive = maxInclusive;
	}

	@Override
	public boolean isValid(String value) {
		boolean valid = true;
		try {
			final double v = Double.parseDouble(value);
			if (minInclusive) {
				if (v < min)
					valid = false;
			} else if (v <= min)
				valid = false;
			if (maxInclusive) {
				if (v > max)
					valid = false;
			} else if (v >= max)
				valid = false;
		} catch (NumberFormatException e) {
			valid = false;
		}
		return valid;
	}

	@Override
	public String getValidValues() {
		String str = "double";
		if (min != Double.MIN_VALUE)
			if (max != Double.MAX_VALUE)
				str += " between " + min
						+ (minInclusive ? " (inclusive)" : " (exclusive)")
						+ " and " + max
						+ (maxInclusive ? " (inclusive)" : " (exclusive)");
			else
				str += (minInclusive ? " >= " : " > ") + min;
		else if (max != Double.MAX_VALUE)
			str += (maxInclusive ? " <= " : " < ") + max;
		return str;
	}

	@Override
	public Object getValueOf(String str) {
		return Double.parseDouble(str);
	}

}
