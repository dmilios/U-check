package gpoptim;

import java.util.Arrays;

public final class GpoResult {

	private double[] solution;
	private double fitness;
	private double hyperparamOptimTimeElapsed;
	private double gpOptimTimeElapsed;
	private double[] hyperparamsUsed;
	
	private int iterations;
	private int evaluations;

	@Override
	public String toString() {
		String str = "# Gaussian Process Optimisation --- Results\n";
		str += "Solution: " + Arrays.toString(solution) + "\n";
		str += "Fitness:  " + fitness + "\n";
		str += "Time for hyperparam opt: " + hyperparamOptimTimeElapsed
				+ " sec\n";
		str += "Time for GP optimsation: " + gpOptimTimeElapsed + " sec\n";
		str += "Hyperparams used: " + Arrays.toString(hyperparamsUsed) + "\n";
		str += " Iterations: " + iterations + "\n";
		str += "Evaluations: " + evaluations + "\n";
		return str;
	}

	public double[] getSolution() {
		return solution;
	}

	public double getFitness() {
		return fitness;
	}

	public int getEvaluations() {
		return evaluations;
	}
	
	public int getIterations() {
		return iterations;
	}

	public double getHyperparamOptimTimeElapsed() {
		return hyperparamOptimTimeElapsed;
	}

	public double getGpOptimTimeElapsed() {
		return gpOptimTimeElapsed;
	}

	public final double[] getHyperparamsUsed() {
		return hyperparamsUsed;
	}

	final void setSolution(double[] solution) {
		this.solution = solution;
	}

	final void setFitness(double fitness) {
		this.fitness = fitness;
	}

	final void setEvaluations(int evaluations) {
		this.evaluations = evaluations;
	}
	
	final void setIterations(int iterations) {
		this.iterations = iterations;
	}

	final void setHyperparamOptimTimeElapsed(double hyperparamOptimTimeElapsed) {
		this.hyperparamOptimTimeElapsed = hyperparamOptimTimeElapsed;
	}

	final void setGpOptimTimeElapsed(double gpOptimTimeElapsed) {
		this.gpOptimTimeElapsed = gpOptimTimeElapsed;
	}

	final void setHyperparamsUsed(double[] hyperparamsUsed) {
		this.hyperparamsUsed = hyperparamsUsed;
	}

}
