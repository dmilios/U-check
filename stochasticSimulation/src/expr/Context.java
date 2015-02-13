package expr;

final public class Context {

	private double[] values = new double[0];
	private Variable[] variables = new Variable[0];

	protected int addVariable(Variable var) {
		final int index = values.length;
		final double[] newValues = new double[index + 1];
		System.arraycopy(values, 0, newValues, 0, index);
		values = newValues;
		final Variable[] newVariables = new Variable[index + 1];
		System.arraycopy(variables, 0, newVariables, 0, index);
		variables = newVariables;
		variables[index] = var;
		return index;
	}

	public Variable[] getVariables() {
		return variables;
	}

	public Variable getVariable(String name) {
		for (Variable var : variables)
			if (var.getName().equals(name))
				return var;
		return null;
	}

	public boolean containsVariable(String name) {
		return getVariable(name) != null;
	}

	public void setValue(Variable var, double value) {
		if (variables[var.indexInNamespace] != var)
			throw new IllegalStateException(
					"Variable has not been added to Context");
		values[var.indexInNamespace] = value;
	}

	public double getValue(Variable var) {
		if (variables[var.indexInNamespace] != var)
			throw new IllegalStateException(
					"Variable has not been added to Context");
		return values[var.indexInNamespace];
	}

	public void setValue(int index, double value) {
		values[index] = value;
	}

	public double getValue(int index) {
		return values[index];
	}

}
