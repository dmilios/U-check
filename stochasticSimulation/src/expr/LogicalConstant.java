package expr;

public final class LogicalConstant extends LogicalExpression {

	final private boolean value;

	public LogicalConstant(boolean value) {
		this.value = value;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public boolean evaluate() {
		return value;
	}

    @Override
    public double evaluateValue() {
        return value ? Double.MAX_VALUE:-Double.MAX_VALUE;
    }

    @Override
	public String toString() {
		return Boolean.toString(value);
	}

}
