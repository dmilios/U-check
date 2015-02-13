package ssa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import expr.Variable;

final class DependencyGraph {

	/** variables that are affected by each reaction */
	final private int[][] variableDependencies;
	/** reactions that are dependent on each reaction */
	final private int[][] reactionDependencies;

	DependencyGraph(CTMCModel model) {
		final int[][] stoichiometryMatrix = model.getStoichiometryMatrix();
		final CTMCReaction[] reactions = model.getReactions();
		final Variable[] variables = model.getStateVariables().getVariables();
		variableDependencies = constructVariableDependencies(stoichiometryMatrix);
		reactionDependencies = constructReactionDependencies(variables,
				reactions);
	}

	final private int[][] constructVariableDependencies(
			final int[][] stoichiometryMatrix) {
		final int variables = stoichiometryMatrix.length;
		final int reactions = stoichiometryMatrix[0].length;
		final int[][] variableDependencies = new int[reactions][];

		final int[] variableCountsPerReaction = new int[reactions];
		for (int i = 0; i < variables; i++)
			for (int j = 0; j < reactions; j++)
				if (stoichiometryMatrix[i][j] != 0)
					variableCountsPerReaction[j]++;

		for (int j = 0; j < reactions; j++) {
			final int variablesInReaction = variableCountsPerReaction[j];
			variableDependencies[j] = new int[variablesInReaction];
			int place = 0;
			for (int i = 0; i < variables; i++)
				if (stoichiometryMatrix[i][j] != 0)
					variableDependencies[j][place++] = i;
		}

		return variableDependencies;
	}

	final private int[][] constructReactionDependencies(Variable[] variables,
			CTMCReaction[] reactions) {
		final int numberOfReactions = reactions.length;
		final int[][] reactiondependencies = new int[numberOfReactions][];

		for (int r1 = 0; r1 < numberOfReactions; r1++) {
			Set<Integer> dependentSet = new HashSet<Integer>();
			for (int i : variableDependencies[r1]) {
				Variable dependentVar = variables[i];
				for (int r2 = 0; r2 < reactions.length; r2++) {
					if (reactions[r2].getVariables().contains(dependentVar))
						dependentSet.add(r2);
				}
			}
			int[] dependentIndices = new int[dependentSet.size()];
			int iter = 0;
			for (int j2 : dependentSet)
				dependentIndices[iter++] = j2;
			Arrays.sort(dependentIndices);
			reactiondependencies[r1] = dependentIndices;
		}

		return reactiondependencies;
	}

	protected int[] variableIndicesAffected(int r) {
		return variableDependencies[r];
	}

	protected int[] reactionIndicesAffected(int r) {
		return reactionDependencies[r];
	}

}
