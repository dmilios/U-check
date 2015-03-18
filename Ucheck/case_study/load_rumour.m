% ===== This file has been automatically produced by the
% ===== U-check model checking tool for uncertain systems
% 
%       It loads the Smoothed Model Checking results from 'rumour.csv'
%       Parameters explored: k_s, k_r

if exist('OCTAVE_VERSION', 'builtin')
	data = load('rumour.csv');
else
	data = csvread('rumour.csv', 1);
end

% 'paramValues'     is a Nx2 matrix (grid of values in the parameter space)
% 'probabilities'   contains the estimated satisfaction probabilities
% 'lowerConfBound'  Lower confidence bound for the satisfaction probabilities
% 'upperConfBound'  Upper confidence bound for the satisfaction probabilities

paramNames     = {'k_s', 'k_r'};
paramValues    = data(:, 1:2);
probabilities  = data(:, 3);
lowerConfBound = data(:, 4);
upperConfBound = data(:, 5);
