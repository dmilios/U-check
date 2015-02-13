package expr;

abstract public class LogicalExpression {

	abstract public int getPriority();
	
	abstract public boolean evaluate();

	@Override
	abstract public String toString();

}
