package modelChecking;

import java.util.Set;

import expr.ArithmeticExpression;
import expr.Context;
import expr.Variable;

public abstract class SignalFunction extends ArithmeticExpression {

	private String name;
	private Variable variable;
	private Variable auxiliaryVariable = null;

	public SignalFunction(String name, Context context, Variable var) {
		this.name = name;
		this.variable = var;
		auxiliaryVariable = new Variable(var.getName() + "__" + name, context);
	}

	abstract public double[] evaluateSignal(final double[] t, final double[] x);

	public String getName() {
		return name;
	}

	public Variable getVariable() {
		return variable;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public double evaluate() {
		if (auxiliaryVariable == null)
			throw new IllegalStateException(
					"Auxiliary variable has not been initialised!");
		return auxiliaryVariable.evaluate();
	}

	@Override
	public Set<Variable> getVariables() {
		return null;
	}

	@Override
	public String toString() {
		return name + "(" + variable.getName() + ")";
	}

}
