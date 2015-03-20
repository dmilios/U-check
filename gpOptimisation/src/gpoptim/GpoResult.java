package gpoptim;

import gp.GpDataset;

import java.util.Arrays;

public final class GpoResult {

	private double[] solution;
	private double[][] covariance;
	private double fitness;
	private double hyperparamOptimTimeElapsed;
	private double gpOptimTimeElapsed;
	private double[] hyperparamsUsed;

	private GpDataset pointsExplored;

	private int iterations;
	private int evaluations;
	private String terminationCause;

	@Override
	public String toString() {
		String str = "# Gaussian Process Optimisation --- Results\n";
		str += "Solution:     " + Arrays.toString(solution) + "\n";
		str += "Standard Dev: " + Arrays.toString(getStandardDeviations());
		str += "\n";
		str += "Covariance matrix:\n" + getCovarianceStr() + "\n";
		str += "\n";
		str += "Hyperparams used: \n";
		str += "      amplitude: " + hyperparamsUsed[0] + "\n";
		for (int i = 1; i < hyperparamsUsed.length; i++)
			str += "    lengthscale: " + hyperparamsUsed[i] + "\n";
		str += "\n";
		str += "Fitness:  " + fitness + "\n";
		str += "Iterations: " + iterations + "\n";
		str += "Evaluations of the objective function: " + evaluations + "\n";
		str += "\n";
		str += "Time for hyperparam opt: " + hyperparamOptimTimeElapsed
				+ " sec\n";
		str += "Time for GP optimsation: " + gpOptimTimeElapsed + " sec\n";
		str += "Termination cause: " + getTerminationCause();
		return str;
	}

	public String getTerminationCause() {
		return terminationCause;
	}

	public void setTerminationCause(String terminationCause) {
		this.terminationCause = terminationCause;
	}

	public GpDataset getPointsExplored() {
		return pointsExplored;
	}

	public void setPointsExplored(GpDataset pointsExplored) {
		this.pointsExplored = pointsExplored;
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

	public double[] getStandardDeviations() {
		final double[] stdev = new double[covariance.length];
		for (int i = 0; i < stdev.length; i++)
			stdev[i] = Math.sqrt(covariance[i][i]);
		return stdev;
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
			str += ";\n " + covariance[i][0];
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
