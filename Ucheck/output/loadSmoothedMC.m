function [X, pr, lb, ub] = loadSmoothedMC(filename, dimension)
data = csvread (filename, 1);
X = data(:, 1:dimension);
pr = data(:, dimension+1);
lb = data(:, dimension+2);
ub = data(:, dimension+3);
