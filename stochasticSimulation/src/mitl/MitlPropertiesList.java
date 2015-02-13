package mitl;

import java.util.ArrayList;

import expr.Context;
import expr.Variable;

public class MitlPropertiesList {

	private ArrayList<String> constantsNames = new ArrayList<String>();
	private ArrayList<String> constantsTypes = new ArrayList<String>();
	private ArrayList<String> constantsValues = new ArrayList<String>();
	private Context constContext = new Context();
	private ArrayList<MiTL> properties = new ArrayList<MiTL>();

	public void addConstant(String name, String type) {
		constantsNames.add(name);
		constantsTypes.add(type);
		constantsValues.add("");
		new Variable(name, constContext);
	}

	public Variable getConstant(String name) {
		if (constantsNames.contains(name))
			return constContext.getVariable(name);
		return null;
	}

	public void setConstant(String name, String value) {
		if (constantsNames.contains(name)) {
			final int index = constantsNames.indexOf(name);
			constantsValues.set(index, value);
			final String type = constantsTypes.get(index);
			double val = 0;
			if (type.equals("double") || type.equals("int"))
				val = Double.parseDouble(value);
			else if (type.equals("bool"))
				if (value.equals("true"))
					val = 1;
				else
					val = 0;
			constContext.setValue(constContext.getVariable(name), val);
		}
	}

	public void removeConstant(int index) {
		constantsNames.remove(index);
	}

	public ArrayList<MiTL> getProperties() {
		return properties;
	}

	public void addProperty(MiTL mitl) {
		properties.add(mitl);
	}

	public void removeProperty(int index) {
		properties.remove(index);
	}

	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		final int constants = constantsNames.size();
		for (int i = 0; i < constants; i++) {
			String name = constantsNames.get(i);
			bf.append("const ");
			bf.append(constantsTypes.get(i));
			bf.append(' ');
			bf.append(name);
			if (!constantsValues.get(i).isEmpty()) {
				bf.append(" = ");
				bf.append(constantsValues.get(i));
			}
			bf.append(';');
			bf.append('\n');
		}
		bf.append('\n');

		for (MiTL property : properties) {
			bf.append(property.toString());
			bf.append('\n');
		}
		return bf.toString();
	}

}
