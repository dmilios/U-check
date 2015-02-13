package expr;

import java.util.Set;

public abstract class ArithmeticExpression {

	abstract public int getPriority();

	abstract public double evaluate();

	abstract public Set<Variable> getVariables();

	@Override
	abstract public String toString();

}
