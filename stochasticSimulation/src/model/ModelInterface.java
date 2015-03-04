package model;

import expr.Context;

public interface ModelInterface {

	public Context getModelVariables();

	public void loadModel(String modelFile) throws Exception;

	public void setParameters(String[] names, double[] values);

	/**
	 * @param tfinal
	 *            The simulation end time
	 * @param runs
	 *            Independent simulation runs
	 * @param timepoints
	 *            Number of points in the generated time-series
	 */
	public Trajectory[] generateTrajectories(double tfinal, int runs,
			int timepoints);

	/**
	 * No number of time-points are specified; this method should create a full
	 * trajectory, rather than a time-series.
	 * 
	 * @param tfinal
	 *            The simulation end time
	 * @param runs
	 *            Independent simulation runs
	 * 
	 */
	public Trajectory[] generateTrajectories(double tfinal, int runs);

}
