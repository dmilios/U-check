package config;

public class RangeSpec extends PropertySpec {

	private DoubleSpec spec = new DoubleSpec("", 0);

	public RangeSpec(String name, double[] defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public boolean isValid(String string) {
		// remove brackets
		if (string.startsWith("[")) {
			string = string.substring(1);
			if (string.endsWith("]"))
				string = string.substring(0, string.length() - 1);
			else
				return false;
		} else
			return false;

		String[] words = string.split(",");
		if (words.length != 2)
			return false;

		for (final String word : words)
			if (!spec.isValid(word))
				return false;

		double val0 = (double) spec.getValueOf(words[0]);
		double val1 = (double) spec.getValueOf(words[1]);
		if (val0 >= val1)
			return false;

		return true;
	}

	@Override
	public String getValidValues() {
		return "\"[number1, number2]\", where number1 < number2";
	}

	@Override
	public Object getValueOf(String string) {
		// remove brackets
		if (string.startsWith("["))
			string = string.substring(1);
		if (string.endsWith("]"))
			string = string.substring(0, string.length() - 1);

		String[] words = string.split(",");
		double[] values = new double[words.length];
		for (int i = 0; i < values.length; i++)
			values[i] = (double) spec.getValueOf(words[i]);
		return values;
	}

}
