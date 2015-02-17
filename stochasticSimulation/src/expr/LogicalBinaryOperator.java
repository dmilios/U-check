package expr;

public enum LogicalBinaryOperator {

	OR("||", 3), AND("&&", 2);

	final private String symbol;
	final private int priority;

	private LogicalBinaryOperator(String symbol, int priority) {
		this.symbol = symbol;
		this.priority = priority;
	}

	@Override
	public String toString() {
		return symbol;
	}

	public int getPriority() {
		return priority;
	}

	public boolean evaluate(boolean lvalue, boolean rvalue) {
		switch (this) {
		case OR:
			return lvalue || rvalue;
		case AND:
			return lvalue && rvalue;
		}
		return false;
	}

    public double evaluateValue(boolean lvalue, boolean rvalue) {
        return evaluate(lvalue,rvalue) ? 1.0:0.0;
    }
}
