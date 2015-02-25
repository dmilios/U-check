package config;

public class CollectionSpec extends PropertySpec {

	private PropertySpec spec;

	public CollectionSpec(String name, PropertySpec spec) {
		super(name, "");
		this.spec = spec;
	}

	@Override
	public boolean isValid(String value) {
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

}
