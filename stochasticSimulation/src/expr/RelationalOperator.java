package expr;

public enum RelationalOperator {
	LT("<"), LE("<="), GT(">"), GE(">="), EQ("="), NEQ("!=");

	final private String symbol;

	private RelationalOperator(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	public boolean evaluate(double value1, double value2) {
		switch (this) {
		case LT:
			return value1 < value2;
		case LE:
			return value1 <= value2;
		case GT:
			return value1 > value2;
		case GE:
			return value1 >= value2;
		case EQ:
			return value1 == value2;
		case NEQ:
			return value1 != value2;
		default:
			return false;
		}
	}

    public double evaluateValue(double value1, double value2) {
        double value = Math.abs(value2 - value1);
       return evaluate(value1,value2) ? value:-value;
    }
}
