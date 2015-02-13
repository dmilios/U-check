package expr;

public enum ArithmeticBinaryOperator {

	PLUS("+", 3), MINUS("-", 3), MULT("*", 2), DIVIDE("/", 2), POW("^", 1);

	final private String symbol;
	final private int priority;

	private ArithmeticBinaryOperator(String symbol, int priority) {
		this.symbol = symbol;
		this.priority = priority;
	}

	public double evaluate(double lvalue, double rvalue) {
		switch (this) {
		case PLUS:
			return lvalue + rvalue;
		case MINUS:
			return lvalue - rvalue;
		case MULT:
			return lvalue * rvalue;
		case DIVIDE:
			return lvalue / rvalue;
		case POW:
			return Math.pow(lvalue, rvalue);
		}
		return 0;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public String toString() {
		return symbol;
	}
}
