package ucheck.config;

public class IDSpec extends PropertySpec {

	public IDSpec(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public boolean isValid(String value) {
		return value.matches("[a-zA-Z_][a-zA-Z_0-9]*");
	}

	@Override
	public String getValidValues() {
		return "";
	}

	@Override
	public Object getValueOf(String str) {
		return str.trim();
	}

}
