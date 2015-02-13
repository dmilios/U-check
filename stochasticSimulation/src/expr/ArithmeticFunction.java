package expr;

import java.util.Set;

final public class ArithmeticFunction extends ArithmeticExpression {

	final private ArithmeticFunctionType function;
	final private ArithmeticExpression argument;

	public ArithmeticFunction(ArithmeticFunctionType function, ArithmeticExpression argument) {
		this.function = function;
		this.argument = argument;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public double evaluate() {
		return function.evaluate(argument.evaluate());
	}

	@Override
	public Set<Variable> getVariables() {
		return argument.getVariables();
	}
	
	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		bf.append(function);
		bf.append('(');
		bf.append(argument);
		bf.append(')');
		return bf.toString();
	}

}
