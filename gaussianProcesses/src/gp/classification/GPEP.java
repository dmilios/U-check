package gp.classification;

import linalg.IMatrix;
import optim.LocalOptimisation;
import optim.PointValue;
import optim.methods.PowellMethodApache;

import org.apache.commons.math3.special.Erf;

import gp.AbstractGP;
import gp.GpDataset;
import gp.HyperparamLogLikelihood;
import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;

public class GPEP extends AbstractGP<ProbitRegressionPosterior> {

	private double eps_damp = 0.5;
	private int scale = 1;
	private double covarianceCorrection = 1e-4;

	public GPEP(KernelFunction kernel) {
		super(kernel);
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getScale() {
		return scale;
	}

	public void setCovarianceCorrection(double covarianceCorrection) {
		this.covarianceCorrection = covarianceCorrection;
	}

	private IMatrix invC;
	private IMatrix mu_tilde;

	public void doTraining() {
		Gauss gauss = expectationPropagation(1e-6);

		IMatrix v_tilde = gauss.Term.getColumn(0);
		IMatrix tau_tilde = gauss.Term.getColumn(1);

		IMatrix diagSigma_tilde = algebra.createZeros(tau_tilde.getLength(), 1);
		for (int i = 0; i < diagSigma_tilde.getLength(); i++)
			diagSigma_tilde.put(i, 1.0 / tau_tilde.get(i));
		mu_tilde = v_tilde.mul(diagSigma_tilde);
		IMatrix Sigma_tilde = algebra.createDiag(diagSigma_tilde.getData());

		invC = algebra.solvePositive(gauss.C.add(Sigma_tilde),
				algebra.createEye(mu_tilde.getLength()));
	}

	public ProbitRegressionPosterior getGpPosterior(GpDataset testSet) {
		final double[] mmK = testSet.calculateVariances(getKernel());
		final double[][] mnK = testSet.calculateCovariances(getKernel(),
				trainingSet);
		IMatrix ks = algebra.createMatrix(mnK);
		IMatrix kss = algebra.createMatrix(mmK);

		if (invC == null || mu_tilde == null || trainingSet.isModified())
			doTraining();
		IMatrix tmp = ks.mmul(invC);
		IMatrix fs = tmp.mmul(mu_tilde);
		IMatrix vfs = kss.sub(tmp.mmul(ks.transpose()).diag());
		return new ProbitRegressionPosterior(testSet, fs.getData(), vfs.getData());
	}

	@Deprecated
	public ProbitRegressionPosterior getGpPosterior_old(GpDataset testSet) {
		Gauss gauss = expectationPropagation(1e-6);

		IMatrix v_tilde = gauss.Term.getColumn(0);
		IMatrix tau_tilde = gauss.Term.getColumn(1);

		IMatrix diagSigma_tilde = algebra.createZeros(tau_tilde.getLength(), 1);
		for (int i = 0; i < diagSigma_tilde.getLength(); i++)
			diagSigma_tilde.put(i, 1.0 / tau_tilde.get(i));
		IMatrix mu_tilde = v_tilde.mul(diagSigma_tilde);
		IMatrix Sigma_tilde = algebra.createDiag(diagSigma_tilde.getData());

		final double[] mmK = testSet.calculateVariances(getKernel());
		final double[][] mnK = testSet.calculateCovariances(getKernel(),
				trainingSet);
		IMatrix ks = algebra.createMatrix(mnK);
		IMatrix kss = algebra.createMatrix(mmK);

		IMatrix inv = algebra.solvePositive(gauss.C.add(Sigma_tilde),
				algebra.createEye(mu_tilde.getLength()));
		IMatrix tmp = ks.mmul(inv);

		IMatrix fs = tmp.mmul(mu_tilde);
		IMatrix vfs = kss.sub(tmp.mmul(ks.transpose()).diag());

		return new ProbitRegressionPosterior(testSet, fs.getData(), vfs.getData());
	}

	@Override
	public double getMarginalLikelihood() {
		Gauss gauss = expectationPropagation(1e-3);
		return gauss.logZ;
	}

	@Override
	public double[] getMarginalLikelihoodGradient() {
		throw new IllegalAccessError("Not supported yet!");
	}

	double logdet_LC = 0;

	private Gauss expectationPropagation(final double tolerance) {
		double sum;
		Gauss gauss = new Gauss();
		final int n = trainingSet.getSize();

		gauss.C = algebra.createMatrix(trainingSet
				.calculateCovariances(getKernel()));

		double CORRECTION = covarianceCorrection;
		for (int i = 0; i < gauss.C.getRows(); i++)
			gauss.C.put(i, i, gauss.C.get(i, i) + CORRECTION);

		gauss.LC_t = algebra.cholesky(gauss.C);
		gauss.LC = gauss.LC_t.transpose();

		sum = 0;
		IMatrix gauss_LC_diag = gauss.LC.diag();
		for (int i = 0; i < gauss_LC_diag.getLength(); i++)
			sum += Math.log(gauss_LC_diag.get(i));
		logdet_LC = 2 * sum;
		double logZprior = 0.5 * (logdet_LC);

		IMatrix logZterms = algebra.createZeros(n, 1);
		IMatrix logZloo = algebra.createZeros(n, 1);
		IMatrix Term = algebra.createZeros(n, 2);
		computeMarginalMoments(gauss, Term);

		// Stuff related to the likelihood
		gauss.LikPar_p = algebra.createMatrix(trainingSet.getTargets()).mul(
				scale);
		gauss.LikPar_q = algebra.createOnes(n, 1).mul(scale)
				.sub(gauss.LikPar_p);
		final int NODES = 96;
		gauss.xGauss = algebra.createZeros(NODES, 1);
		gauss.wGauss = algebra.createZeros(NODES, 1);
		gausshermite(NODES, gauss.xGauss, gauss.wGauss);
		gauss.logwGauss = algebra.createZeros(gauss.wGauss.getLength(), 1);
		for (int i = 0; i < gauss.logwGauss.getLength(); i++)
			gauss.logwGauss.put(i, Math.log(gauss.wGauss.get(i)));

		// initialize cycle control
		int MaxIter = 1000;
		double tol = tolerance;
		double logZold = 0;
		double logZ = 2 * tol;
		int steps = 0;

		double logZappx = 0;
		while ((Math.abs(logZ - logZold) > tol) & (steps < MaxIter)) {
			// cycle control
			steps = steps + 1;
			logZold = logZ;

			CavGauss cavGauss = ComputeCavities(gauss, Term.neg());

			// [Term,logZterms,logZloo] = EPupdate(cavGauss,gauss.LikFunc,y,
			// Term, eps_damp);
			EPupdate update = ep_update(cavGauss, Term, eps_damp, null,
					gauss.LikPar_p, gauss.LikPar_q, gauss.xGauss,
					gauss.logwGauss);
			Term = update.TermNew;
			logZterms = update.logZterms;
			logZloo = update.logZ;

			logZappx = computeMarginalMoments(gauss, Term);
			logZ = logZterms.sum() + logZappx;
		}

		// finishing up
		logZ = logZ - logZprior;
		gauss.logZloo = logZloo.sum();
		gauss.logZappx = logZappx;
		gauss.logZterms = logZterms;
		gauss.logZ = logZ;
		gauss.Term = Term;

		// pi = normcdf(Gauss.m./sqrt(1+Gauss.diagV));

		return gauss;
	}

	@SuppressWarnings("unused")
	private double computeGradient(Gauss gauss, IMatrix Term) {

		// Gauss.beta = Gauss.LC'\(Gauss.LC\Gauss.m);
		IMatrix tmp = algebra.solve(gauss.LC, gauss.m);
		IMatrix gauss_beta = algebra.solve(gauss.LC_t, tmp);

		return 0;
	}

	private void gausshermite(int n, IMatrix x, IMatrix w) {
		IMatrix x0 = algebra.createZeros(x.getLength(), 1);
		IMatrix w0 = algebra.createZeros(w.getLength(), 1);
		int m = (n + 1) / 2;
		double z = 0, pp = 0, p1 = 0, p2 = 0, p3 = 0;
		for (int i = 0; i < m; i++) {
			if (i == 0)
				z = Math.sqrt(2 * n + 1) - 1.85575
						* Math.pow(2 * n + 1, -0.16667);
			else if (i == 1)
				z = z - 1.14 * Math.pow(n, 0.426) / z;
			else if (i == 2)
				z = 1.86 * z - 0.86 * x0.get(0);
			else if (i == 3)
				z = 1.91 * z - 0.91 * x0.get(1);
			else
				z = 2.0 * z - x0.get(i - 2);

			for (int its = 0; its < 10; its++) {
				p1 = 1 / Math.sqrt(Math.sqrt(Math.PI));
				p2 = 0;
				for (double j = 1; j <= n; j++) {
					p3 = p2;
					p2 = p1;
					final double a = z * Math.sqrt(2 / j) * p2;
					final double b = Math.sqrt((j - 1) / j) * p3;
					p1 = a - b;
					System.currentTimeMillis();
				}
				pp = Math.sqrt(2 * n) * p2;
				double z1 = z;
				z = z1 - p1 / pp;
				if (Math.abs(z - z1) < 2.2204e-16)
					break;
			}

			x0.put(i, z);
			x0.put(n - 1 - i, -z); // x(n+1-i) = -z;
			w0.put(i, 2 / (pp * pp)); // w(i) = 2/(pp*pp);
			w0.put(n - 1 - i, w0.get(i)); // w(n+1-i) = w(i);
		}

		w0 = w0.div(Math.sqrt(Math.PI)); // w = w/sqrt(pi);
		x0 = x0.mul(Math.sqrt(2));
		x0 = x0.sort(); // x = sort(x);
		x.copy(x0);
		w.copy(w0);
	}

	private CavGauss ComputeCavities(Gauss gauss, IMatrix Term) {

		CavGauss cavGauss = new CavGauss();

		// C = Gauss.diagV;
		IMatrix C = gauss.diagV;

		// s = 1./(1 + Term(:,2).*C);
		IMatrix s = algebra.createOnes(C.getLength(), 1).div(
				Term.getColumn(1).mul(C).add(1));

		// CavGauss.diagV = s.*C;
		cavGauss.diagV = s.mul(C);

		// CavGauss.m = s.*(Gauss.m + Term(:,1).*C);
		cavGauss.m = s.mul(gauss.m.add(Term.getColumn(0).mul(C)));

		return cavGauss;
	}

	private EPupdate ep_update(CavGauss cavGauss, IMatrix Term,
			double eps_damp, Object LogLikFunc, IMatrix LikPar_p,
			IMatrix LikPar_q, IMatrix xGauss, IMatrix wGauss) {
		EPupdate update = new EPupdate();

		IMatrix Cumul = algebra.createZeros(LikPar_p.getLength(), 2);
		IMatrix logZ = GaussHermiteNQ(LikPar_p, LikPar_q, cavGauss.m,
				cavGauss.diagV, xGauss, wGauss, Cumul);

		update.logZ = logZ;

		IMatrix m2 = algebra.createZeros(cavGauss.m.getLength(), 1);
		for (int i = 0; i < m2.getLength(); i++)
			m2.put(i, cavGauss.m.get(i) * cavGauss.m.get(i));

		IMatrix logV = algebra.createZeros(cavGauss.m.getLength(), 1);
		for (int i = 0; i < logV.getLength(); i++)
			logV.put(i, Math.log(cavGauss.diagV.get(i)));

		IMatrix cumul1 = algebra.createZeros(cavGauss.m.getLength(), 1);
		for (int i = 0; i < cumul1.getLength(); i++)
			cumul1.put(i, Cumul.getColumn(0).get(i) * Cumul.getColumn(0).get(i));

		IMatrix cumul2 = algebra.createZeros(cavGauss.m.getLength(), 1);
		for (int i = 0; i < cumul2.getLength(); i++)
			cumul2.put(i, Math.log(Cumul.getColumn(1).get(i)));

		// logZterms = logZ ...
		// + 0.5*( (CavGauss.m.^2./CavGauss.diagV + log(CavGauss.diagV)) -
		// (Cumul(:,1).^2./Cumul(:,2) + log(Cumul(:,2))));

		IMatrix tmp = m2.div(cavGauss.diagV).add(logV)
				.sub(cumul1.div(Cumul.getColumn(1)).add(cumul2));
		update.logZterms = logZ.add(tmp.mul(0.5));

		// TermNew = [
		// Cumul(:,1)./Cumul(:,2) - CavGauss.m./CavGauss.diagV,
		// 1./Cumul(:,2) - 1./CavGauss.diagV
		// ];

		IMatrix ones = algebra.createOnes(LikPar_p.getLength(), 1);
		IMatrix TermNew = algebra.createZeros(LikPar_p.getLength(), 2);
		IMatrix c1 = Cumul.getColumn(0).div(Cumul.getColumn(1))
				.sub(cavGauss.m.div(cavGauss.diagV));
		IMatrix c2 = ones.div(Cumul.getColumn(1)).sub(ones.div(cavGauss.diagV));
		TermNew.putColumn(0, c1);
		TermNew.putColumn(1, c2);

		// TermNew = (1-eps_damp)*Term + eps_damp*TermNew;
		TermNew = Term.mul(1 - eps_damp).add(TermNew.mul(eps_damp));

		update.TermNew = TermNew;
		return update;
	}

	private double computeMarginalMoments(Gauss gauss, IMatrix Term) {

		double sum;
		double logZappx;
		final int N = Term.getRows();
		IMatrix tmp;

		// (repmat(Term(:,2),1,N).*Gauss.LC)
		tmp = Term.getColumn(1).repmat(1, N).mul(gauss.LC);
		IMatrix A = gauss.LC_t.mmul(tmp).add(
				algebra.createEye(N).mul(CORRECTION_FACTOR));

		// Serious numerical stability issue with the calculation
		// of A (i.e. A = LC' * tmp + I)
		// as it does not appear to be PD for large amplitudes
		gauss.L = algebra.cholesky(A).transpose();

		//

		// Gauss.W = Gauss.L\(Gauss.LC');
		gauss.W = algebra.solve(gauss.L, gauss.LC_t);

		// Gauss.diagV = sum(Gauss.W.*Gauss.W,1)';
		tmp = gauss.W.mul(gauss.W);
		gauss.diagV = algebra.createZeros(N, 1);
		for (int i = 0; i < N; i++)
			gauss.diagV.put(i, tmp.getColumn(i).sum());
		// or
		// gauss.diagV = gauss.W.transpose().mmul(gauss.W).diag();

		// Gauss.m = Gauss.W'*(Gauss.W*Term(:,1));
		tmp = gauss.W.mmul(Term.getColumn(0));
		gauss.m = gauss.W.transpose().mmul(tmp);

		// logdet = -2*sum(log(diag(Gauss.L))) + 2*sum(log(diag(Gauss.LC)));
		double logdet = 0;
		sum = 0;
		tmp = gauss.L.diag();
		for (int i = 0; i < tmp.getLength(); i++)
			sum += Math.log(tmp.get(i));
		logdet += -2 * sum;

		// sum = 0;
		// tmp = gauss.LC.diag();
		// for (int i = 0; i < tmp.getLength(); i++)
		// sum += Math.log(tmp.get(i));

		logdet += logdet_LC;

		// logZappx = 0.5*(Gauss.m'*Term(:,1) + logdet);
		logZappx = 0.5 * (gauss.m.transpose().dot(Term.getColumn(0)) + logdet);

		return logZappx;
	}

	public class Gauss {
		IMatrix LikPar_p;
		IMatrix LikPar_q;
		IMatrix xGauss;
		IMatrix wGauss;
		IMatrix logwGauss;

		IMatrix C;
		IMatrix LC;
		IMatrix LC_t;
		IMatrix L;
		IMatrix W;
		IMatrix diagV;
		IMatrix m;
		double logZloo;
		double logZappx;
		IMatrix logZterms;
		double logZ;
		IMatrix Term;
	}

	public class CavGauss {
		IMatrix diagV;
		IMatrix m;
	}

	public class EPupdate {
		IMatrix TermNew;
		IMatrix logZterms;
		IMatrix logZ;
	}

	/**
	 * Gauss Hermite Numerical Quadrature for Gaussian integration and moment
	 * computation
	 */
	IMatrix GaussHermiteNQ(IMatrix FuncPar_p, IMatrix FuncPar_q, IMatrix m,
			IMatrix v, IMatrix xGH, IMatrix logwGH, IMatrix Cumul) {

		IMatrix stdv = algebra.createZeros(v.getLength(), 1);
		for (int i = 0; i < stdv.getLength(); i++)
			stdv.put(i, Math.sqrt(v.get(i)));

		int Nnodes = xGH.getLength();
		IMatrix tmp;

		// tmp = bsxfun(@times,stdv,xGH(:)')
		tmp = stdv.mmul(xGH.transpose());
		// Y = bsxfun(@plus, tmp, m(:));
		IMatrix Y = tmp.add(m.repmat(1, Nnodes));

		// tmp = feval(FuncName, Y, FuncPar);
		tmp = logprobitpow(Y, FuncPar_p, FuncPar_q);
		// G = bsxfun(@plus, tmp, logwGH(:)');
		IMatrix G = tmp.add(logwGH.transpose().repmat(tmp.getRows(), 1));

		// maxG = max(G,[],2);
		IMatrix maxG = algebra.createZeros(G.getRows(), 1);
		for (int i = 0; i < G.getRows(); i++)
			maxG.put(i, G.getRow(i).max());

		// G = G-repmat(maxG,1,Nnodes);
		// expG = exp(G);
		G = G.sub(maxG.repmat(1, Nnodes));
		IMatrix expG = algebra.createZeros(G.getRows(), G.getColumns());
		for (int i = 0; i < expG.getLength(); i++)
			expG.put(i, Math.exp(G.get(i)));

		// denominator = sum(expG,2);
		// logZ = maxG + log(denominator);
		IMatrix denominator = algebra.createZeros(expG.getRows(), 1);
		IMatrix logdenominator = algebra.createZeros(expG.getRows(), 1);
		for (int i = 0; i < expG.getRows(); i++)
			denominator.put(i, expG.getRow(i).sum());
		for (int i = 0; i < denominator.getLength(); i++)
			logdenominator.put(i, Math.log(denominator.get(i)));
		IMatrix logZ = maxG.add(logdenominator);

		// deltam = stdv.*(expG*xGH(:))./denominator;
		IMatrix deltam = stdv.mul(expG.mmul(xGH)).div(denominator);

		// Cumul(:,1) = m + deltam;
		Cumul.putColumn(0, m.add(deltam));

		// Cumul(:,2) = v.*(expG*xGH(:).^2)./denominator - deltam.^2;
		IMatrix xGH2 = algebra.createZeros(xGH.getRows(), xGH.getColumns());
		for (int i = 0; i < xGH2.getLength(); i++)
			xGH2.put(i, xGH.get(i) * xGH.get(i));

		IMatrix deltam2 = algebra.createZeros(deltam.getRows(),
				deltam.getColumns());
		for (int i = 0; i < deltam2.getLength(); i++)
			deltam2.put(i, deltam.get(i) * deltam.get(i));

		Cumul.putColumn(1, v.mul(expG.mmul(xGH2)).div(denominator).sub(deltam2));

		return logZ;
	}

	/**
	 * log likelihood evaluation for various probit power likelihoods
	 * 
	 * @param X
	 *            is a NxM matrix where N is the number of variables and M is
	 *            the number of Gauss-Hermite nodes
	 * @param LikPar_p
	 *            - a structure of all the parameters
	 * @param LikPar_q
	 *            - a structure of all the parameters
	 */
	IMatrix logprobitpow(IMatrix X, IMatrix LikPar_p, IMatrix LikPar_q) {
		final int m = X.getColumns();
		final IMatrix Y = algebra.createZeros(X.getRows(), X.getColumns());

		for (int i = 0; i < Y.getLength(); i++)
			Y.put(i, ncdflogbc(X.get(i)));
		IMatrix Za = Y.mul(LikPar_p.repmat(1, m));
		// Za = ncdflogbc(X).mul(LikPar_p.repmat(1, m));

		for (int i = 0; i < Y.getLength(); i++)
			Y.put(i, ncdflogbc(-X.get(i)));
		IMatrix Zb = Y.mul(LikPar_q.repmat(1, m));
		// Zb = ncdflogbc(X.neg()).mul(LikPar_q.repmat(1, m));

		return Za.add(Zb);
	}

	@Deprecated
	/**
	 * log of standard normal cdf by 10th order Taylor expansion in the negative
	 * domain
	 */
	IMatrix ncdflogbc(IMatrix x) {
		double sqrt2 = Math.sqrt(2);
		double log2 = Math.log(2);
		double treshold = -Math.sqrt(2) * 5;

		IMatrix y = algebra.createZeros(x.getRows(), x.getColumns());

		int z_i = 0;
		for (int i = 0; i < x.getLength(); i++)
			if (x.get(i) <= treshold)
				z_i++;
		IMatrix z = algebra.createZeros(z_i, 1);
		z_i = 0;
		for (int i = 0; i < x.getLength(); i++)
			if (x.get(i) <= treshold)
				z.put(z_i++, -x.get(i));

		z_i = 0;
		for (int i = 0; i < x.getLength(); i++)
			if (x.get(i) >= 0) // Ip
				y.put(i, Math.log(1 + erf(x.get(i) / sqrt2)) - log2);

			else if (treshold < x.get(i) && x.get(i) < 0) // In
				y.put(i, Math.log(1 - erf(-x.get(i) / sqrt2)) - log2);

			else if (x.get(i) <= treshold) { // Iz
				double tmp = -0.5
						* Math.log(Math.PI)
						- log2
						- 0.5
						* z.get(z_i)
						* z.get(z_i)
						- Math.log(z.get(z_i))
						+ Math.log(1 - 1 / z.get(z_i) + 3
								/ Math.pow(z.get(z_i), 4) - 15
								/ Math.pow(z.get(z_i), 6) + 105
								/ Math.pow(z.get(z_i), 8) - 945
								/ Math.pow(z.get(z_i), 10));
				y.put(i, tmp);
				z_i++;
			}

		return y;
	}

	final static private double sqrt2 = Math.sqrt(2);
	final static private double invSqrt2 = 1 / sqrt2;
	final static private double log2 = Math.log(2);

	static double ncdflogbc(final double x) {
		final double treshold = -sqrt2 * 5;
		final double z = -x;
		if (x >= 0)
			return Math.log(1 + erf(x * invSqrt2)) - log2;
		if (treshold < x) // (treshold < x && x < 0)
			return Math.log(1 - erf(-x * invSqrt2)) - log2;
		// (x <= treshold)
		return -0.5
				* Math.log(Math.PI)
				- log2
				- 0.5
				* z
				* z
				- Math.log(z)
				+ Math.log(1 - 1 / z + 3 / Math.pow(z, 4) - 15 / Math.pow(z, 6)
						+ 105 / Math.pow(z, 8) - 945 / Math.pow(z, 10));
	}

	@Deprecated
	/** WARNING: do not use this -- not same as in matlab/octave */
	protected static double __erf(double z) {
		final double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
		// use Horner's method
		final double ans = 1
				- t
				* Math.exp(-z
						* z
						- 1.26551223
						+ t
						* (1.00002368 + t
								* (0.37409196 + t
										* (0.09678418 + t
												* (-0.18628806 + t
														* (0.27886807 + t
																* (-1.13520398 + t
																		* (1.48851587 + t
																				* (-0.82215223 + t * (0.17087277))))))))));
		if (z >= 0)
			return ans;
		else
			return -ans;
	}

	public static double erf(double x) {
		return Erf.erf(x);
	}

	static public double CORRECTION_FACTOR = 1;

	@Deprecated
	public static void main(String[] args) {

		final int n = 8;
		final int m = 200;
		final int runs = 50;

		final double a = -2;
		final double b = 9;
		final double step_n = (b - a) / n;
		final double step_m = (b - a) / m;
		double current;

		current = a;
		double[][] X = new double[n][1];
		for (int i = 0; i < n; i++)
			X[i][0] = current += step_n;
		double[] Y = new double[n];
		for (int i = 0; i < n; i++) {
			Y[i] = (Math.sin(X[i][0]) + 1) / 2;
			Y[i] = Math.floor(Y[i] * runs) / runs;
		}

		current = a;
		double[][] Xt = new double[m][1];
		for (int i = 0; i < m; i++)
			Xt[i][0] = current += step_m;

		GpDataset xy = new GpDataset(1, 100);
		xy.set(X, Y);
		GpDataset xt = new GpDataset(1);
		xt.set(Xt);

		// GPEP gp = new GPEP(new KernelRBF(1.1, 0.81));
		GPEP gp = new GPEP(new KernelRBF(2.71, 0.81)); // amplitude 2.71 or 2.7
		gp.setTrainingSet(xy);
		gp.setScale(runs);

		boolean optimise = false;
		if (optimise) {
			HyperparamLogLikelihood func = new HyperparamLogLikelihood(gp);
			LocalOptimisation alg = new PowellMethodApache();
			final double init[] = gp.getKernel().getHypeparameters();
			PointValue best = alg.optimise(func, init);
			gp.getKernel().setHyperarameters(best.getPoint());
			System.out.println(best);
		}

		long t0;
		double elapsed;

		t0 = System.currentTimeMillis();
		ProbitRegressionPosterior post = gp.getGpPosterior(xt);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		for (int i = 0; i < m; i++) {
			System.out.print(post.getInputData().getInstance(i)[0] + "\t");
			System.out.print(post.getClassProbabilities()[i] + "\n");
		}
		System.out.println("\nelapsed: " + elapsed + " sec");
		System.out.println("log-likelihood: " + gp.getMarginalLikelihood());
	}
}
