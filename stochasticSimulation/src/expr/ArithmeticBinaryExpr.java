package expr;

import java.util.HashSet;
import java.util.Set;

final public class ArithmeticBinaryExpr extends ArithmeticExpression {

	final private ArithmeticBinaryOperator operator;
	final private ArithmeticExpression leftOperant;
	final private ArithmeticExpression rightOperant;

	public ArithmeticBinaryExpr(ArithmeticBinaryOperator operator,
			ArithmeticExpression left, ArithmeticExpression right) {
		this.operator = operator;
		this.leftOperant = left;
		this.rightOperant = right;
	}

	@Override
	public int getPriority() {
		return operator.getPriority();
	}

	@Override
	public double evaluate() {
		final double lvalue = leftOperant.evaluate();
		final double rvalue = rightOperant.evaluate();
		return operator.evaluate(lvalue, rvalue);
	}

	@Override
	public Set<Variable> getVariables() {
		final Set<Variable> union = new HashSet<Variable>();
		union.addAll(leftOperant.getVariables());
		union.addAll(rightOperant.getVariables());
		return union;
	}

	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		if (leftOperant.getPriority() > getPriority()) {
			bf.append('(');
			bf.append(leftOperant);
			bf.append(')');
		} else
			bf.append(leftOperant);
		bf.append(' ');
		bf.append(operator);
		bf.append(' ');
		if (rightOperant.getPriority() > getPriority()) {
			bf.append('(');
			bf.append(rightOperant);
			bf.append(')');
		} else
			bf.append(rightOperant);
		return bf.toString();
	}

}
