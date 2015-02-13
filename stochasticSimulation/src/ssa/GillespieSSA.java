package ssa;



public class GillespieSSA extends StochasticSimulationAlgorithm {

	final protected double[] rates;
	final protected double[] cumulativeRates;

	public GillespieSSA(CTMCModel model) {
		super(model);
		this.rates = new double[numberOfReactions];
		this.cumulativeRates = new double[numberOfReactions];
	}

	@Override
	protected void initialiseSimulation() {
		for (int reaction = 0; reaction < numberOfReactions; reaction++)
			rates[reaction] = rateExpressions[reaction].evaluate();
	}

	@Override
	protected double updateSimulationTime() {
		double accumulated = 0;
		for (int reaction = 0; reaction < numberOfReactions; reaction++)
			cumulativeRates[reaction] = accumulated += rates[reaction];
		if (accumulated == 0)
			return Double.POSITIVE_INFINITY;
		return getRandomEngine().nextExponential(accumulated);
	}

	@Override
	protected void updateSimulationState() {
		final int selected = getRandomEngine().nextCategorical(cumulativeRates);
		final int[] variablesAffected = dependencyGraph
				.variableIndicesAffected(selected);
		final int[] reactionsAffected = dependencyGraph
				.reactionIndicesAffected(selected);

		for (final int i : variablesAffected)
			currentState.setValue(i, currentState.getValue(i)
					+ stoichiometries[i][selected]);
		for (final int reaction : reactionsAffected)
			rates[reaction] = rateExpressions[reaction].evaluate();
	}

}
