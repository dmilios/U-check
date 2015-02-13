package expr;

public final class LogicalNegation extends LogicalExpression {

	final private LogicalExpression argument;

	public LogicalNegation(LogicalExpression argument) {
		this.argument = argument;
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public boolean evaluate() {
		return !argument.evaluate();
	}

	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		bf.append("¬");
		if (argument.getPriority() > getPriority()) {
			bf.append('(');
			bf.append(argument);
			bf.append(')');
		} else
			bf.append(argument);
		return bf.toString();
	}

}
