package gpoptim;

import java.util.Arrays;

public final class GpoResult {

	private double[] solution;
	private double[][] covariance;
	private double fitness;
	private double hyperparamOptimTimeElapsed;
	private double gpOptimTimeElapsed;
	private double[] hyperparamsUsed;

	private int iterations;
	private int evaluations;

	@Override
	public String toString() {
		String str = "# Gaussian Process Optimisation --- Results\n";
		str += "Solution:  " + Arrays.toString(solution) + "\n";
		str += "Variances: " + Arrays.toString(getVariances()) + "\n";
		str += "Covariances: " + getCovarianceStr() + "\n";
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

	public double[] getVariances() {
		final double[] var = new double[covariance.length];
		for (int i = 0; i < var.length; i++)
			var[i] = covariance[i][i];
		return var;
	}

	public double[][] getCovariance() {
		return covariance;
	}

	public String getCovarianceStr() {
		String str = "[";
		str += covariance[0][0];
		for (int j = 1; j < covariance[0].length; j++)
			str += ", " + covariance[0][j];
		for (int i = 1; i < covariance.length; i++) {
			str += "; " + covariance[i][0];
			for (int j = 1; j < covariance[i].length; j++)
				str += ", " + covariance[i][j];
		}
		str += "]";
		return str;
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

	public void setCovariance(double[][] covariance) {
		this.covariance = covariance;
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
