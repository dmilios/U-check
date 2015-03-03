package ucheck.config;

public final class CategoricalSpec extends PropertySpec {

	final private String[] values;

	public CategoricalSpec(String name, String defaultValue, String... values) {
		super(name, defaultValue);
		this.values = values;
	}

	@Override
	public boolean isValid(String value) {
		for (final String str : values)
			if (str.equals(value))
				return true;
		return false;
	}

	@Override
	public String getValidValues() {
		String str = "[" + values[0];
		for (int i = 1; i < values.length; i++)
			str += " | " + values[i];
		str += "]";
		return str;
	}

	@Override
	public Object getValueOf(String str) {
		return str;
	}
	
}
