
load SIR_timeseries.csv; 
load SIR_trajectory.csv;

plot (SIR_trajectory(:, 1), SIR_trajectory(:, 2), 'x', SIR_timeseries(:,1), SIR_timeseries(:, 2));
legend ('Trajectory', 'Timeseries');
