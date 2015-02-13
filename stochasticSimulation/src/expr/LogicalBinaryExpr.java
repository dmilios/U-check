package expr;

final public class LogicalBinaryExpr extends LogicalExpression {

	final private LogicalBinaryOperator operator;
	final private LogicalExpression leftOperant;
	final private LogicalExpression rightOperant;

	public LogicalBinaryExpr(LogicalBinaryOperator operator, LogicalExpression left,
			LogicalExpression right) {
		this.operator = operator;
		this.leftOperant = left;
		this.rightOperant = right;
	}

	@Override
	public int getPriority() {
		return operator.getPriority();
	}

	@Override
	public boolean evaluate() {
		final boolean lvalue = leftOperant.evaluate();
		final boolean rvalue = rightOperant.evaluate();
		return operator.evaluate(lvalue, rvalue);
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
