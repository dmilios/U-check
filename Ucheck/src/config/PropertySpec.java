package config;

public abstract class PropertySpec {

	final private String name;
	final private String defaultValue;

	public PropertySpec(String name, String defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	final public String getName() {
		return name;
	}

	final public String getDefaultValue() {
		return defaultValue;
	}

	public abstract boolean isValid(String value);

	public abstract Object getValueOf(String str);

	public abstract String getValidValues();

}
