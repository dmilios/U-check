load_rumour;

n = length(probabilities);
x1 = reshape(paramValues(:, 1), sqrt(n), sqrt(n));
x2 = reshape(paramValues(:, 2), sqrt(n), sqrt(n));
pr = reshape(probabilities, sqrt(n), sqrt(n));
ub = reshape(upperConfBound, sqrt(n), sqrt(n));
surf (x1, x2, pr); hold on;
m=mesh (x1, x2, ub); set(m,'facecolor','none'); 
hold off;

xlabel(paramNames(1));
ylabel(paramNames(2));
zlabel('Satisfaction Probability');

rotation = 340;
[az, el] = view;
view(az + rotation, el);


set(findall(gcf,'type','text'),'FontSize',16)
fileStr = 'rumour_smmc.eps';
print('-depsc2', fileStr);
