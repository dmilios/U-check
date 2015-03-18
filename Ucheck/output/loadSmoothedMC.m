function [X, pr, lb, ub] = loadSmoothedMC(filename, dimension)
if exist('OCTAVE_VERSION', 'builtin')
	data = load(filename);
else
	data = csvread(filename, 1);
end
X = data(:, 1:dimension);
pr = data(:, dimension+1);
lb = data(:, dimension+2);
ub = data(:, dimension+3);
