package ssa;

import java.util.Set;
import expr.*;

public class CTMCModel {

	final private Context stateVariables;
	final private int[] initialState;
	final private CTMCReaction[] reactions;
	final private int[][] stoichiometryMatrix;

	public CTMCModel(Context stateVariables, CTMCReaction[] reactions,
			int[] initialState) {
		if (stateVariables.getVariables().length != initialState.length)
			throw new IllegalArgumentException("Incompatible initial state");
		this.stateVariables = stateVariables;
		this.reactions = reactions;
		this.initialState = initialState;
		stoichiometryMatrix = constructStoichiometryMatrix(stateVariables,
				reactions);
	}

	final static private int[][] constructStoichiometryMatrix(
			Context stateVariables, CTMCReaction[] reactions) {
		final int nVariables = stateVariables.getVariables().length;
		final int nReactions = reactions.length;
		final int[][] stoichiometryMatrix = new int[nVariables][nReactions];

		for (int j = 0; j < reactions.length; j++) {
			final CTMCReaction reaction = reactions[j];
			Set<Variable> set;
			set = reaction.getReactants();
			for (final Variable var : set)
				stoichiometryMatrix[var.getId()][j] -= reaction
						.getReactantStoichiometry(var);
			set = reaction.getProducts();
			for (final Variable var : set)
				stoichiometryMatrix[var.getId()][j] += reaction
						.getProductStoichiometry(var);
		}
		return stoichiometryMatrix;
	}

	public int[] getInitialState() {
		return initialState;
	}

	public int[][] getStoichiometryMatrix() {
		return stoichiometryMatrix;
	}

	public Context getStateVariables() {
		return stateVariables;
	}

	public CTMCReaction[] getReactions() {
		return reactions;
	}

	@Override
	public String toString() {
		final StringBuffer bf = new StringBuffer();
		for (CTMCReaction reaction : reactions)
			bf.append(reaction + "\n");
		return bf.toString();
	}

}
