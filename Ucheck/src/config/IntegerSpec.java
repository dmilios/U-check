package config;


public class IntegerSpec extends PropertySpec {

	final private int min;
	final private int max;

	public IntegerSpec(String name, int defaultValue) {
		this(name, defaultValue, Integer.MIN_VALUE);
	}

	public IntegerSpec(String name, int defaultValue, int min) {
		this(name, defaultValue, min, Integer.MAX_VALUE);
	}

	public IntegerSpec(String name, int defaultValue, int min, int max) {
		super(name, defaultValue);
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean isValid(String value) {
		boolean valid = true;
		try {
			final int v = Integer.parseInt(value);
			if (v < min || v > max)
				valid = false;
		} catch (NumberFormatException e) {
			valid = false;
		}
		return valid;
	}

	@Override
	public String getValidValues() {
		String str = "integer";
		if (min != Integer.MIN_VALUE)
			if (max != Integer.MAX_VALUE)
				str += " between " + min + " and " + max;
			else
				str += " >= " + min;
		else if (max != Integer.MAX_VALUE)
			str += " =< " + max;
		return str;
	}

	@Override
	public Object getValueOf(String str) {
		return Integer.parseInt(str);
	}
	
}
