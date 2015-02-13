package expr;

import java.util.HashSet;
import java.util.Set;

final public class Variable extends ArithmeticExpression {

	final private String name;
	final private Context context;
	final protected int indexInNamespace;

	public Variable(String name, Context context) {
		if (context == null)
			throw new NullPointerException("Null Context for Variable");
		this.name = name;
		this.context = context;
		this.indexInNamespace = context.addVariable(this);
	}

	final public String getName() {
		return name;
	}

	@Override
	final public int getPriority() {
		return 0;
	}

	final public int getId() {
		return indexInNamespace;
	}

	@Override
	final public double evaluate() {
		return context.getValue(indexInNamespace);
	}

	@Override
	public Set<Variable> getVariables() {
		Set<Variable> set = new HashSet<Variable>();
		set.add(this);
		return set;
	}

	@Override
	final public String toString() {
		return getName();
	}

}
