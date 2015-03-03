package ucheck.config;

import java.util.Arrays;

public abstract class PropertySpec {

	final private String name;
	final private Object defaultValue;

	public PropertySpec(String name, Object defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	final public String getName() {
		return name;
	}

	final public Object getDefaultValue() {
		return defaultValue;
	}

	final public String getDefaultValueString() {
		if (defaultValue instanceof Object[])
			return Arrays.toString((Object[]) defaultValue);
		return defaultValue.toString();
	}

	public abstract boolean isValid(String value);

	public abstract Object getValueOf(String str);

	public abstract String getValidValues();

}
