package expr;

import java.util.Set;

final public class ArithmeticUnaryExpr extends ArithmeticExpression {

	final private ArithmeticUnaryOperator operator;
	final private ArithmeticExpression operant;

	public ArithmeticUnaryExpr(ArithmeticUnaryOperator operator, ArithmeticExpression operant) {
		this.operator = operator;
		this.operant = operant;
	}
	
	public ArithmeticExpression getOperant() {
		return operant;
	}
	
	public ArithmeticUnaryOperator getOperator() {
		return operator;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public double evaluate() {
		return operator.evaluate(operant.evaluate());
	}

	@Override
	public Set<Variable> getVariables() {
		return operant.getVariables();
	}
	
	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		bf.append(operator);
		if (operant.getPriority() > getPriority()) {
			bf.append('(');
			bf.append(operant);
			bf.append(')');
		} else
			bf.append(operant);
		return bf.toString();
	}

}
