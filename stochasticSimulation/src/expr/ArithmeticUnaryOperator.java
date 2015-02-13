package expr;

public enum ArithmeticUnaryOperator {

	UMINUS("-");

	final private String symbol;

	private ArithmeticUnaryOperator(String symbol) {
		this.symbol = symbol;
	}

	public double evaluate(double value) {
		switch (this) {
		case UMINUS:
			return -value;
		}
		return 0;
	}

	@Override
	public String toString() {
		return symbol;
	}
}
