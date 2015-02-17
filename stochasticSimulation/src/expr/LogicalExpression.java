package expr;

abstract public class LogicalExpression {

	abstract public int getPriority();
	
	abstract public boolean evaluate();

    abstract public double evaluateValue();

	@Override
	abstract public String toString();

}
