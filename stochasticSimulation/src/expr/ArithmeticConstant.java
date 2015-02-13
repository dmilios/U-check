package expr;

import java.util.HashSet;
import java.util.Set;

public final class ArithmeticConstant extends ArithmeticExpression {

	final private double value;

	public ArithmeticConstant(double value) {
		this.value = value;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public double evaluate() {
		return value;
	}
	
	@Override
	public Set<Variable> getVariables() {
		return new HashSet<Variable>();
	}

	@Override
	public String toString() {
		if ((int) value == value)
			return Integer.toString((int) value);
		return Double.toString(value);
	}

}
