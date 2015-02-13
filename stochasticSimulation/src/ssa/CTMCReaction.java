package ssa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import expr.ArithmeticExpression;
import expr.Variable;

public class CTMCReaction {

	final private ArrayList<Variable> reactants;
	final private ArrayList<Variable> products;
	final private ArrayList<Integer> reactantStoichiometries;
	final private ArrayList<Integer> productStoichiometries;
	final private ArithmeticExpression rateExpression;

	public CTMCReaction(ArithmeticExpression rateExpression) {
		this.reactants = new ArrayList<Variable>();
		this.products = new ArrayList<Variable>();
		this.reactantStoichiometries = new ArrayList<Integer>();
		this.productStoichiometries = new ArrayList<Integer>();
		this.rateExpression = rateExpression;
	}

	public ArithmeticExpression getRateExpression() {
		return rateExpression;
	}

	public Set<Variable> getVariables() {
		return rateExpression.getVariables();
	}

	public Set<Variable> getReactants() {
		return new HashSet<Variable>(reactants);
	}

	public Set<Variable> getProducts() {
		return new HashSet<Variable>(products);
	}

	public void addReactant(Variable var, int stoichiometry) {
		if (stoichiometry < 0)
			throw new IllegalArgumentException("stoichiometry < 0");
		if (stoichiometry == 0)
			return;
		if (reactants.contains(var)) {
			final int index = reactants.indexOf(var);
			final int old = reactantStoichiometries.get(index);
			reactantStoichiometries.set(index, old + stoichiometry);
			return;
		}
		reactants.add(var);
		reactantStoichiometries.add(stoichiometry);
	}

	public void addProduct(Variable var, int stoichiometry) {
		if (stoichiometry < 0)
			throw new IllegalArgumentException("stoichiometry < 0");
		if (stoichiometry == 0)
			return;
		if (products.contains(var)) {
			final int index = products.indexOf(var);
			final int old = productStoichiometries.get(index);
			productStoichiometries.set(index, old + stoichiometry);
			return;
		}
		products.add(var);
		productStoichiometries.add(stoichiometry);
	}

	public int getReactantStoichiometry(Variable var) {
		final int index = reactants.indexOf(var);
		if (index < 0)
			return 0;
		return reactantStoichiometries.get(index);
	}

	public int getProductStoichiometry(Variable var) {
		final int index = products.indexOf(var);
		if (index < 0)
			return 0;
		return productStoichiometries.get(index);
	}

	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		if (reactants.isEmpty())
			bf.append('0');
		else
			for (int i = 0; i < reactants.size(); i++) {
				final int st = reactantStoichiometries.get(i);
				if (st != 1)
					bf.append(st);
				bf.append(reactants.get(i).getName());
				if (i < reactants.size() - 1)
					bf.append(" + ");
			}
		bf.append(' ');
		bf.append('-');
		bf.append('-');
		bf.append('>');
		bf.append(' ');
		if (products.isEmpty())
			bf.append('0');
		else
			for (int i = 0; i < products.size(); i++) {
				final int st = productStoichiometries.get(i);
				if (st != 1)
					bf.append(st);
				bf.append(products.get(i).getName());
				if (i < products.size() - 1)
					bf.append(" + ");
			}
		bf.append(",  [");
		bf.append(rateExpression);
		bf.append(']');
		return bf.toString();
	}

}
