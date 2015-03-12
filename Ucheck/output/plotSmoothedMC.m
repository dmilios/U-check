function plotSmoothedMC(filename, dimension)

[X, pr, lb, ub] = loadSmoothedMC(filename, dimension);
if dimension == 1
	plot(X, pr, 'r', X, lb, 'k', X, ub, 'k');
end
if dimension == 2
	n = length(pr);
	x1 = reshape(X(:, 1), sqrt(n), sqrt(n));
	x2 = reshape(X(:, 2), sqrt(n), sqrt(n));
	pr = reshape(pr, sqrt(n), sqrt(n));
	lb = reshape(lb, sqrt(n), sqrt(n));
	ub = reshape(ub, sqrt(n), sqrt(n));
	surf (x1, x2, pr); hold on;
	m=mesh (x1, x2, lb); set(m,'facecolor','none');
	m=mesh (x1, x2, ub); set(m,'facecolor','none'); 
	hold off;
end
