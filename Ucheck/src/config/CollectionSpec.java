package config;

public class CollectionSpec extends PropertySpec {

	private PropertySpec spec;

	public CollectionSpec(String name, Object[] defaultValue, PropertySpec spec) {
		super(name, defaultValue);
		this.spec = spec;
	}

	@Override
	public boolean isValid(String value) {

		// remove parentheses, if exist
		if (value.startsWith("(")) {
			value = value.substring(1);
			if (value.endsWith(")"))
				value = value.substring(0, value.length() - 1);
			else
				return false;
		}

		String[] values = value.split(",");
		for (final String val : values) {
			if (!spec.isValid(val))
				return false;
		}
		return true;
	}

	@Override
	public String getValidValues() {
		return "VALUE [, VALUE]*  where VALUE is " + spec.getValidValues();
	}

	@Override
	public Object getValueOf(String string) {
		// remove parentheses, if exist
		if (string.startsWith("("))
			string = string.substring(1);
		if (string.endsWith(")"))
			string = string.substring(0, string.length() - 1);

		String[] words = string.split(",");
		Object[] values = new Object[words.length];
		for (int i = 0; i < values.length; i++)
			values[i] = spec.getValueOf(words[i]);
		return values;
	}

}
