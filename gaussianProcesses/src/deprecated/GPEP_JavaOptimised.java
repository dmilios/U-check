package deprecated;

import org.apache.commons.math3.special.Erf;
import org.jblas.Decompose;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import gp.GpDataset;
import gp.classification.ClassificationPosterior;
import gp.classification.GPEP;
import gp.kernels.KernelFunction;

public class GPEP_JavaOptimised extends GPEP {

	private GpDataset trainingSet = new GpDataset(1);
	private double eps_damp = 0.5;
	private int scale = 1;
	private double covarianceCorrection = 1e-4;

	public GPEP_JavaOptimised(KernelFunction kernel) {
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

	public void setTrainingSet(GpDataset trainingSet) {
		this.trainingSet = trainingSet;
	}

	public GpDataset getTrainingSet() {
		return trainingSet;
	}

	@Override
	public ClassificationPosterior getGpPosterior(GpDataset testSet) {
		Gauss gauss = calculatePosterior(testSet, 1e-6);

		DoubleMatrix v_tilde = gauss.Term.getColumn(0);
		DoubleMatrix tau_tilde = gauss.Term.getColumn(1);

		DoubleMatrix diagSigma_tilde = new DoubleMatrix(tau_tilde.length);
		for (int i = 0; i < diagSigma_tilde.length; i++)
			diagSigma_tilde.put(i, 1.0 / tau_tilde.get(i));
		DoubleMatrix mu_tilde = v_tilde.mul(diagSigma_tilde);
		DoubleMatrix Sigma_tilde = DoubleMatrix.diag(diagSigma_tilde);

		final double[] mmK = testSet.calculateVariances(getKernel());
		final double[][] mnK = testSet.calculateCovariances(getKernel(),
				trainingSet);
		DoubleMatrix ks = new DoubleMatrix(mnK);
		DoubleMatrix kss = new DoubleMatrix(mmK);

		DoubleMatrix inv = Solve.solvePositive(gauss.C.add(Sigma_tilde),
				DoubleMatrix.eye(mu_tilde.length));
		DoubleMatrix tmp = ks.mmul(inv);

		DoubleMatrix fs = tmp.mmul(mu_tilde);
		DoubleMatrix vfs = kss.sub(tmp.mmul(ks.transpose()).diag());

		return new ClassificationPosterior(testSet, fs.toArray(), vfs.toArray());
	}

	@Override
	public double getMarginalLikelihood() {
		Gauss gauss = calculatePosterior(trainingSet, 1e-2);
		return gauss.logZ;
	}

	double logdet_LC = 0;

	private Gauss calculatePosterior(GpDataset testSet, final double tolerance) {
		if (trainingSet.getDimension() != testSet.getDimension())
			throw new IllegalArgumentException(
					"The training and test sets must have the same dimension!");

		double sum;
		Gauss gauss = new Gauss();
		final int n = trainingSet.getSize();

		gauss.C = new DoubleMatrix(
				trainingSet.calculateCovariances(getKernel()));

		double CORRECTION = covarianceCorrection;
		for (int i = 0; i < gauss.C.rows; i++)
			gauss.C.put(i, i, gauss.C.get(i, i) + CORRECTION);

		gauss.invC = Solve.solvePositive(gauss.C, DoubleMatrix.eye(n));
		gauss.LC_t = Decompose.cholesky(gauss.C);
		gauss.LC = gauss.LC_t.transpose();

		sum = 0;
		DoubleMatrix gauss_LC_diag = gauss.LC.diag();
		for (int i = 0; i < gauss_LC_diag.length; i++)
			sum += Math.log(gauss_LC_diag.get(i));
		logdet_LC = 2 * sum;
		double logZprior = 0.5 * (logdet_LC);

		DoubleMatrix logZterms = new DoubleMatrix(n, 1);
		DoubleMatrix logZloo = new DoubleMatrix(n, 1);
		DoubleMatrix Term = new DoubleMatrix(n, 2);
		computeMarginalMoments(gauss, Term);

		// Stuff related to the likelihood
		gauss.LikPar_p = new DoubleMatrix(trainingSet.getTargets()).mul(scale);
		gauss.LikPar_q = DoubleMatrix.ones(n).mul(scale).sub(gauss.LikPar_p);
		final int NODES = 33;
		gauss.xGauss = new DoubleMatrix(NODES);
		gauss.wGauss = new DoubleMatrix(NODES);
		gausshermite(NODES, gauss.xGauss, gauss.wGauss);
		gauss.logwGauss = new DoubleMatrix(gauss.wGauss.length);
		for (int i = 0; i < gauss.logwGauss.length; i++)
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

	static private void gausshermite(int n, DoubleMatrix x, DoubleMatrix w) {
		DoubleMatrix x0 = new DoubleMatrix(x.length);
		DoubleMatrix w0 = new DoubleMatrix(w.length);
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

	private CavGauss ComputeCavities(Gauss gauss, DoubleMatrix Term) {

		CavGauss cavGauss = new CavGauss();

		// C = Gauss.diagV;
		DoubleMatrix C = gauss.diagV;

		// s = 1./(1 + Term(:,2).*C);
		DoubleMatrix s = DoubleMatrix.ones(C.length).div(
				Term.getColumn(1).mul(C).add(1));

		// CavGauss.diagV = s.*C;
		cavGauss.diagV = s.mul(C);

		// CavGauss.m = s.*(Gauss.m + Term(:,1).*C);
		cavGauss.m = s.mul(gauss.m.add(Term.getColumn(0).mul(C)));

		return cavGauss;
	}

	private EPupdate ep_update(CavGauss cavGauss, DoubleMatrix Term,
			double eps_damp, Object LogLikFunc, DoubleMatrix LikPar_p,
			DoubleMatrix LikPar_q, DoubleMatrix xGauss, DoubleMatrix wGauss) {
		EPupdate update = new EPupdate();

		DoubleMatrix Cumul = new DoubleMatrix(LikPar_p.length, 2);
		DoubleMatrix logZ = GaussHermiteNQ(LikPar_p, LikPar_q, cavGauss.m,
				cavGauss.diagV, xGauss, wGauss, Cumul);

		update.logZ = logZ;

		DoubleMatrix m2 = new DoubleMatrix(cavGauss.m.length);
		for (int i = 0; i < m2.length; i++)
			m2.put(i, cavGauss.m.get(i) * cavGauss.m.get(i));

		DoubleMatrix logV = new DoubleMatrix(cavGauss.m.length);
		for (int i = 0; i < logV.length; i++)
			logV.put(i, Math.log(cavGauss.diagV.get(i)));

		DoubleMatrix cumul1 = new DoubleMatrix(cavGauss.m.length);
		for (int i = 0; i < cumul1.length; i++)
			cumul1.put(i, Cumul.getColumn(0).get(i) * Cumul.getColumn(0).get(i));

		DoubleMatrix cumul2 = new DoubleMatrix(cavGauss.m.length);
		for (int i = 0; i < cumul2.length; i++)
			cumul2.put(i, Math.log(Cumul.getColumn(1).get(i)));

		// logZterms = logZ ...
		// + 0.5*( (CavGauss.m.^2./CavGauss.diagV + log(CavGauss.diagV)) -
		// (Cumul(:,1).^2./Cumul(:,2) + log(Cumul(:,2))));

		DoubleMatrix tmp = m2.div(cavGauss.diagV).add(logV)
				.sub(cumul1.div(Cumul.getColumn(1)).add(cumul2));
		update.logZterms = logZ.add(tmp.mul(0.5));

		// TermNew = [
		// Cumul(:,1)./Cumul(:,2) - CavGauss.m./CavGauss.diagV,
		// 1./Cumul(:,2) - 1./CavGauss.diagV
		// ];

		DoubleMatrix ones = DoubleMatrix.ones(LikPar_p.length);
		DoubleMatrix TermNew = new DoubleMatrix(LikPar_p.length, 2);
		DoubleMatrix c1 = Cumul.getColumn(0).div(Cumul.getColumn(1))
				.sub(cavGauss.m.div(cavGauss.diagV));
		DoubleMatrix c2 = ones.div(Cumul.getColumn(1)).sub(
				ones.div(cavGauss.diagV));
		TermNew.putColumn(0, c1);
		TermNew.putColumn(1, c2);

		// TermNew = (1-eps_damp)*Term + eps_damp*TermNew;
		TermNew = Term.mul(1 - eps_damp).add(TermNew.mul(eps_damp));

		update.TermNew = TermNew;
		return update;
	}

	private double computeMarginalMoments(Gauss gauss, DoubleMatrix Term) {

		double sum;
		double logZappx;
		final int N = Term.getRows();
		DoubleMatrix tmp;

		DoubleMatrix A = gauss.invC.add(DoubleMatrix.eye(N).mul(
				CORRECTION_FACTOR));
		for (int i = 0; i < A.rows; i++)
			A.put(i, i, A.get(i, i) + Term.getColumn(1).get(i));
		DoubleMatrix V = Solve.solvePositive(A, DoubleMatrix.eye(N));

		//

		// (repmat(Term(:,2),1,N).*Gauss.LC)
		tmp = Term.getColumn(1).repmat(1, N).mul(gauss.LC);
		A = gauss.LC_t.mmul(tmp)
				.add(DoubleMatrix.eye(N).mul(CORRECTION_FACTOR));

		// Serious numerical stability issue with the calculation
		// of A (i.e. A = LC' * tmp + I)
		// as it does not appear to be PD for large amplitudes
		gauss.L = Decompose.cholesky(A).transpose();

		//

		gauss.diagV = V.diag();
		gauss.m = V.mmul(Term.getColumn(0));

		// logdet = -2*sum(log(diag(Gauss.L))) + 2*sum(log(diag(Gauss.LC)));
		double logdet = 0;
		sum = 0;
		tmp = gauss.L.diag();
		for (int i = 0; i < tmp.length; i++)
			sum += Math.log(tmp.get(i));
		logdet += -2 * sum;

		sum = 0;
		tmp = gauss.LC.diag();
		for (int i = 0; i < tmp.length; i++)
			sum += Math.log(tmp.get(i));

		logdet += logdet_LC;

		// logZappx = 0.5*(Gauss.m'*Term(:,1) + logdet);
		logZappx = 0.5 * (gauss.m.transpose().dot(Term.getColumn(0)) + logdet);

		return logZappx;
	}

	public class Gauss {
		DoubleMatrix LikPar_p;
		DoubleMatrix LikPar_q;
		DoubleMatrix xGauss;
		DoubleMatrix wGauss;
		DoubleMatrix logwGauss;

		DoubleMatrix C;
		DoubleMatrix invC;
		@Deprecated
		DoubleMatrix LC;
		@Deprecated
		DoubleMatrix LC_t;
		DoubleMatrix L;
		DoubleMatrix W;
		DoubleMatrix diagV;
		DoubleMatrix m;
		double logZloo;
		double logZappx;
		DoubleMatrix logZterms;
		double logZ;
		DoubleMatrix Term;
	}

	public class CavGauss {
		DoubleMatrix diagV;
		DoubleMatrix m;
	}

	public class EPupdate {
		DoubleMatrix TermNew;
		DoubleMatrix logZterms;
		DoubleMatrix logZ;
	}

	/**
	 * Gauss Hermite Numerical Quadrature for Gaussian integration and moment
	 * computation
	 */
	static DoubleMatrix GaussHermiteNQ(DoubleMatrix FuncPar_p,
			DoubleMatrix FuncPar_q, DoubleMatrix m, DoubleMatrix v,
			DoubleMatrix xGH, DoubleMatrix logwGH, DoubleMatrix Cumul) {

		DoubleMatrix stdv = new DoubleMatrix(v.getLength());
		for (int i = 0; i < stdv.length; i++)
			stdv.put(i, Math.sqrt(v.get(i)));

		int Nnodes = xGH.length;
		DoubleMatrix tmp;

		// tmp = bsxfun(@times,stdv,xGH(:)')
		tmp = stdv.mmul(xGH.transpose());
		// Y = bsxfun(@plus, tmp, m(:));
		DoubleMatrix Y = tmp.add(m.repmat(1, Nnodes));

		// tmp = feval(FuncName, Y, FuncPar);
		tmp = logprobitpow(Y, FuncPar_p, FuncPar_q);
		// G = bsxfun(@plus, tmp, logwGH(:)');
		DoubleMatrix G = tmp.add(logwGH.transpose().repmat(tmp.rows, 1));

		// maxG = max(G,[],2);
		DoubleMatrix maxG = new DoubleMatrix(G.rows);
		for (int i = 0; i < G.rows; i++)
			maxG.put(i, G.getRow(i).max());

		// G = G-repmat(maxG,1,Nnodes);
		// expG = exp(G);
		G = G.sub(maxG.repmat(1, Nnodes));
		DoubleMatrix expG = new DoubleMatrix(G.rows, G.columns);
		for (int i = 0; i < expG.length; i++)
			expG.put(i, Math.exp(G.get(i)));

		// denominator = sum(expG,2);
		// logZ = maxG + log(denominator);
		DoubleMatrix denominator = new DoubleMatrix(expG.rows);
		DoubleMatrix logdenominator = new DoubleMatrix(expG.rows);
		for (int i = 0; i < expG.rows; i++)
			denominator.put(i, expG.getRow(i).sum());
		for (int i = 0; i < denominator.length; i++)
			logdenominator.put(i, Math.log(denominator.get(i)));
		DoubleMatrix logZ = maxG.add(logdenominator);

		// deltam = stdv.*(expG*xGH(:))./denominator;
		DoubleMatrix deltam = stdv.mul(expG.mmul(xGH)).div(denominator);

		// Cumul(:,1) = m + deltam;
		Cumul.putColumn(0, m.add(deltam));

		// Cumul(:,2) = v.*(expG*xGH(:).^2)./denominator - deltam.^2;
		DoubleMatrix xGH2 = new DoubleMatrix(xGH.rows, xGH.columns);
		for (int i = 0; i < xGH2.length; i++)
			xGH2.put(i, xGH.get(i) * xGH.get(i));

		DoubleMatrix deltam2 = new DoubleMatrix(deltam.rows, deltam.columns);
		for (int i = 0; i < deltam2.length; i++)
			deltam2.put(i, deltam.get(i) * deltam.get(i));

		Cumul.putColumn(1, v.mul(expG.mmul(xGH2)).div(denominator).sub(deltam2));

		return logZ;
	}

	/**
	 * log likelihood evaluation for various probit power likelihoods
	 * 
	 * @param X
	 *            is a nXm matrix where n is the number of variables and m is
	 *            the number of Gauss-Hermite nodes
	 * @param LikPar_p
	 *            - a structure of all the parameters
	 * @param LikPar_q
	 *            - a structure of all the parameters
	 */
	static DoubleMatrix logprobitpow(DoubleMatrix X, DoubleMatrix LikPar_p,
			DoubleMatrix LikPar_q) {
		final int m = X.columns;
		final DoubleMatrix Y = new DoubleMatrix(X.rows, X.columns);

		for (int i = 0; i < Y.length; i++)
			Y.put(i, ncdflogbc(X.get(i)));
		DoubleMatrix Za = Y.mul(LikPar_p.repmat(1, m));
		// Za = ncdflogbc(X).mul(LikPar_p.repmat(1, m));

		for (int i = 0; i < Y.length; i++)
			Y.put(i, ncdflogbc(-X.get(i)));
		DoubleMatrix Zb = Y.mul(LikPar_q.repmat(1, m));
		// Zb = ncdflogbc(X.neg()).mul(LikPar_q.repmat(1, m));

		return Za.add(Zb);
	}

	@Deprecated
	/**
	 * log of standard normal cdf by 10th order Taylor expansion in the negative
	 * domain
	 */
	static DoubleMatrix ncdflogbc(DoubleMatrix x) {
		double sqrt2 = Math.sqrt(2);
		double log2 = Math.log(2);
		double treshold = -Math.sqrt(2) * 5;

		DoubleMatrix y = new DoubleMatrix(x.rows, x.columns);

		int z_i = 0;
		for (int i = 0; i < x.length; i++)
			if (x.get(i) <= treshold)
				z_i++;
		DoubleMatrix z = new DoubleMatrix(z_i);
		z_i = 0;
		for (int i = 0; i < x.length; i++)
			if (x.get(i) <= treshold)
				z.put(z_i++, -x.get(i));

		z_i = 0;
		for (int i = 0; i < x.length; i++)
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

	final static double ncdflogbc(final double x) {
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

}
