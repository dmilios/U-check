package config;

public class StringSpec extends PropertySpec {

	public StringSpec(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public boolean isValid(String value) {
		return value.startsWith("\"") && value.endsWith("\"");
	}

	@Override
	public String getValidValues() {
		return "";
	}

}
