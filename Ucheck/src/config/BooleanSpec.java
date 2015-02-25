package config;


public class BooleanSpec extends PropertySpec {

	public BooleanSpec(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public boolean isValid(String value) {
		return value.equals("true") || value.equals("false");
	}

	@Override
	public String getValidValues() {
		return "[true | false]";
	}

}
