package expr;

public enum ArithmeticFunctionType {

	LOG("log"), EXP("exp"), H("heavyside"), FLOOR("floor"), CEIL("ceiling"), TANH(
			"tanh"), ABS("abs");

	final private String name;

	private ArithmeticFunctionType(String name) {
		this.name = name;
	}

	public double evaluate(double value) {
		switch (this) {
		case LOG:
			return Math.log(value);
		case EXP:
			return Math.exp(value);
		case H:
			return (value > 0) ? 1 : 0;
		case FLOOR:
			return Math.floor(value);
		case CEIL:
			return Math.ceil(value);
		case TANH:
			return Math.tanh(value);
		case ABS:
			return Math.abs(value);
		}
		return 0;
	}

	@Override
	public String toString() {
		return name;
	}

}
