package expr;

final public class RelationalExpression extends LogicalExpression {

	final private RelationalOperator operator;
	final private ArithmeticExpression leftOperant;
	final private ArithmeticExpression rightOperant;

	public RelationalExpression(RelationalOperator operator,
			ArithmeticExpression left, ArithmeticExpression right) {
		this.operator = operator;
		this.leftOperant = left;
		this.rightOperant = right;
	}
	
	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public boolean evaluate() {
		final double value1 = leftOperant.evaluate();
		final double value2 = rightOperant.evaluate();
		return operator.evaluate(value1, value2);
	}

    @Override
    public double evaluateValue() {
        final double value1 = leftOperant.evaluate();
        final double value2 = rightOperant.evaluate();
        return operator.evaluateValue(value1, value2);
    }

    @Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		bf.append(leftOperant);
		bf.append(' ');
		bf.append(operator);
		bf.append(' ');
		bf.append(rightOperant);
		return bf.toString();
	}

}
