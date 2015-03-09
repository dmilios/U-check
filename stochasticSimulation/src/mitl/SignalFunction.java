package mitl;

import java.util.Set;

import model.Trajectory;
import expr.ArithmeticExpression;
import expr.Context;
import expr.Variable;

public class SignalFunction extends ArithmeticExpression {

	private String name;
	private ArithmeticExpression args[];

	public SignalFunction(String name, ArithmeticExpression... args) {
		this.name = name;
		this.args = args;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	public double[] evaluate(Trajectory x) {
		Context c = new Context();
		
		Trajectory y = new Trajectory(x.getTimes(), x.getContext(), x.getValues());
		return null;
	}

	@Override
	public double evaluate() {
		throw new IllegalStateException(
				"This function can only be evaluated in a signal context!");
	}

	@Override
	public Set<Variable> getVariables() {
		return null;
	}

	@Override
	public String toString() {
		String str = name + "(" + args[0];
		for (int i = 1; i < args.length; i++)
			str += ", " + args[i];
		str += ")";
		return str;
	}

}
