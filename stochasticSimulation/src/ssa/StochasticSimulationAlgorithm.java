package ssa;

import java.util.ArrayList;

import rand.JavaRandomEngine;
import rand.RandomEngine;

import expr.ArithmeticExpression;
import expr.Context;

public abstract class StochasticSimulationAlgorithm {

	final protected CTMCModel model;
	final protected DependencyGraph dependencyGraph;
	final protected Context currentState;
	final protected ArithmeticExpression[] rateExpressions;
	final protected int[][] stoichiometries;
	final protected int numberOfReactions;
	final private int[] initialState;
	private double simulationTime;
	private RandomEngine randEngine = new JavaRandomEngine();

	abstract protected void initialiseSimulation();

	abstract protected double updateSimulationTime();

	abstract protected void updateSimulationState();

	public StochasticSimulationAlgorithm(CTMCModel model) {
		this.model = model;
		this.dependencyGraph = new DependencyGraph(model);
		this.initialState = model.getInitialState();
		this.currentState = model.getStateVariables();
		final CTMCReaction[] reactions = model.getReactions();
		this.rateExpressions = new ArithmeticExpression[reactions.length];
		for (int i = 0; i < rateExpressions.length; i++)
			rateExpressions[i] = reactions[i].getRateExpression();
		this.stoichiometries = model.getStoichiometryMatrix();
		this.numberOfReactions = rateExpressions.length;
	}

	final public RandomEngine getRandomEngine() {
		return randEngine;
	}

	public void setRandomEngine(RandomEngine randEngine) {
		this.randEngine = randEngine;
	}

	final protected double getSimulationTime() {
		return simulationTime;
	}

	final public Trajectory generateTimeseries(double startTime,
			double stopTime, int timepoints) {
		return generateTimeseries(startTime, stopTime, timepoints, 1);
	}

	final public Trajectory generateTimeseries(double startTime,
			double stopTime, int timepoints, int simulationRuns) {
		if (timepoints < 2)
			throw new IllegalArgumentException("timepoints < 2");
		if (startTime >= stopTime)
			throw new IllegalArgumentException("startTime >= stopTime");
		if (startTime < 0)
			throw new IllegalArgumentException("startTime < 0");
		if (simulationRuns < 1)
			throw new IllegalArgumentException("simulationRuns < 1");

		final double step = (stopTime - startTime) / (timepoints - 1);
		final double[] times = new double[timepoints];
		double time = times[0] = startTime;
		for (int i = 1; i < times.length; i++)
			times[i] = time += step;
		return generateTimeseries(startTime, stopTime, times, simulationRuns);
	}

	final public Trajectory generateTimeseries(double startTime,
			double stopTime, double[] times) {
		return generateTimeseries(startTime, stopTime, times, 1);
	}

	final public Trajectory generateTimeseries(double startTime,
			double stopTime, double[] times, int simulationRuns) {
		if (simulationRuns < 1)
			throw new IllegalArgumentException("simulationRuns < 1");

		final int variables = initialState.length;
		final double[][] results = new double[variables][times.length];
		final double[][] values = new double[variables][times.length];

		if (simulationRuns > 1) {
			for (int run = 0; run < simulationRuns; run++) {
				performSimulation(startTime, stopTime, times, results);
				for (int i = 0; i < variables; i++)
					for (int j = 0; j < times.length; j++)
						values[i][j] += results[i][j];
			}
			for (int i = 0; i < variables; i++)
				for (int j = 0; j < times.length; j++)
					values[i][j] /= (double) simulationRuns;
		}

		else
			performSimulation(startTime, stopTime, times, values);

		Trajectory trajectory = new Trajectory(times, currentState, values);
		return trajectory;
	}

	final private void performSimulation(double startTime, double stopTime,
			double[] times, final double[][] results) {
		final int variables = initialState.length;
		simulationTime = 0;
		for (int i = 0; i < variables; i++)
			currentState.setValue(i, initialState[i]);

		initialiseSimulation();
		int timeIndex = 0;
		final int points = times.length;

		simulationLoop: while (simulationTime < stopTime) {
			simulationTime += updateSimulationTime();
			if (simulationTime < times[timeIndex]) {
				updateSimulationState();
				continue simulationLoop;
			}

			while (timeIndex != points && simulationTime > times[timeIndex]) {
				for (int i = 0; i < variables; i++)
					results[i][timeIndex] = currentState.getValue(i);
				timeIndex++;
			}
			if (simulationTime <= stopTime)
				updateSimulationState();
		}
	}

	final public Trajectory generateTrajectory(double startTime, double stopTime) {
		final int variables = initialState.length;
		simulationTime = 0;
		for (int i = 0; i < variables; i++)
			currentState.setValue(i, initialState[i]);

		initialiseSimulation();
		while (simulationTime < startTime) {
			simulationTime += updateSimulationTime();
			updateSimulationState();
		}

		final ArrayList<Double> timeSequence = new ArrayList<Double>();
		final ArrayList<ArrayList<Double>> stateSequences;
		stateSequences = new ArrayList<ArrayList<Double>>(variables);
		for (int i = 0; i < variables; i++)
			stateSequences.add(new ArrayList<Double>());

		while (simulationTime < stopTime) {
			timeSequence.add(simulationTime);
			for (int i = 0; i < variables; i++)
				stateSequences.get(i).add(currentState.getValue(i));
			simulationTime += updateSimulationTime();
			if (simulationTime <= stopTime)
				updateSimulationState();
		}

		final double lastTime = timeSequence.get(timeSequence.size() - 1);
		if (lastTime < stopTime) {
			timeSequence.add(stopTime);
			for (int i = 0; i < variables; i++)
				stateSequences.get(i).add(currentState.getValue(i));
		}

		final int n = timeSequence.size();
		final double[] times = new double[n];
		for (int i = 0; i < n; i++)
			times[i] = timeSequence.get(i);

		final double[][] values = new double[variables][];
		for (int c = 0; c < variables; c++) {
			values[c] = new double[n];
			for (int i = 0; i < n; i++)
				values[c][i] = stateSequences.get(c).get(i);
		}

		Trajectory trajectory = new Trajectory(times, currentState, values);
		return trajectory;
	}

}
