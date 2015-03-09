package ucheck.config;

public class StringSpec extends PropertySpec {

	public StringSpec(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public boolean isValid(String value) {
		return true;
	}

	@Override
	public String getValidValues() {
		return "";
	}

	@Override
	public Object getValueOf(String str) {
		return str;
	}
	
}
