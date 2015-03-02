package gp.classification;

import linalg.IMatrix;
import gp.AbstractGP;
import gp.GpDataset;
import gp.kernels.KernelFunction;
import gp.kernels.KernelRBF;

public class GPCEP extends AbstractGP<ClassificationPosterior> {

	private double eps_damp = 0.5;

	public GPCEP(KernelFunction kernel) {
		super(kernel);
	}

	@Override
	public ClassificationPosterior getGpPosterior(GpDataset testSet) {
		Gauss gauss = calculatePosterior(testSet);
		return new ClassificationPosterior(testSet, gauss.m.getData(),
				gauss.diagV.getData());
	}

	@Override
	public double getMarginalLikelihood() {
		Gauss gauss = calculatePosterior(trainingSet);
		return gauss.logZ;
	}

	@Override
	public double[] getMarginalLikelihoodGradient() {
		throw new IllegalAccessError("Not supported yet!");
	}

	double logdet_LC = 0;

	private Gauss calculatePosterior(GpDataset testSet) {
		if (trainingSet.getDimension() != testSet.getDimension())
			throw new IllegalArgumentException(
					"The training and test sets must have the same dimension!");

		double sum;
		Gauss gauss = new Gauss();
		final int n = trainingSet.getSize();
		IMatrix y = algebra.createMatrix(trainingSet.getTargets());

		gauss.C = algebra.createMatrix(trainingSet
				.calculateCovariances(getKernel()));

		double CORRECTION = 1e-4;
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

		// initialize cycle control
		int MaxIter = 1000;
		double tol = 1e-6;
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
			EPupdate update = ep_update(cavGauss, null, y, Term, eps_damp);
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

	private EPupdate ep_update(CavGauss cavGauss, Object likFunc, IMatrix y,
			IMatrix Term, double eps_damp) {
		EPupdate update = new EPupdate();

		IMatrix Cumul = algebra.createZeros(y.getLength(), 2);
		IMatrix logZ = ProbitS(y, cavGauss.m, cavGauss.diagV, Cumul);

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

		IMatrix ones = algebra.createOnes(y.getLength(), 1);
		IMatrix TermNew = algebra.createZeros(y.getLength(), 2);
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
		IMatrix tmpA = Term.getColumn(1).repmat(1, N).mul(gauss.LC);
		tmpA = gauss.LC_t.mmul(tmpA).add(algebra.createEye(N));
		gauss.L = algebra.cholesky(tmpA).transpose();

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

	IMatrix ProbitS(IMatrix y, IMatrix m, IMatrix v, IMatrix Cumul) {

		IMatrix sqrt1v = algebra.createOnes(y.getLength(), 1).add(v);
		for (int i = 0; i < sqrt1v.getLength(); i++)
			sqrt1v.put(i, Math.sqrt(sqrt1v.get(i)));

		// w = (y.*m)./sqrt(1+v);
		IMatrix w = y.mul(m).div(sqrt1v);

		// Z = normcdf(w);
		// logZ = log(Z);
		IMatrix Z = algebra.createZeros(y.getLength(), 1);
		for (int i = 0; i < Z.getLength(); i++)
			Z.put(i, standardNormalCDF(w.get(i)));
		IMatrix logZ = algebra.createZeros(y.getLength(), 1);
		for (int i = 0; i < logZ.getLength(); i++)
			logZ.put(i, Math.log(Z.get(i)));

		// Cumul = zeros(length(m), 2);

		// a = y.*v./sqrt(1+v);
		// b = normpdf(w)./Z;
		IMatrix a = y.mul(v).div(sqrt1v);
		IMatrix b = algebra.createZeros(w.getLength(), 1);
		for (int i = 0; i < b.getLength(); i++)
			b.put(i, standardNormalPDF(w.get(i)) / Z.get(i));

		// Cumul(:,1) = m + a.*b;
		Cumul.putColumn(0, m.add(a.mul(b)));

		// Cumul(:,2) = v - (a.^2).*b.*(w + b);
		IMatrix a2 = algebra.createZeros(a.getLength(), 1);
		for (int i = 0; i < a2.getLength(); i++)
			a2.put(i, a.get(i) * a.get(i));
		Cumul.putColumn(1, v.sub(a2.mul(b).mul(w.add(b))));

		return logZ;
	}

	final private static double invSqrt2pi = 1 / Math.sqrt(2 * Math.PI);
	final private static double invSqrt2 = 1 / Math.sqrt(2);

	static final double standardNormalPDF(double x) {
		return invSqrt2pi * Math.exp(-0.5 * x * x);
	}

	static final double standardNormalCDF(double x) {
		return 0.5 + 0.5 * erf(x * invSqrt2);
	}

	/**
	 * fractional error in math formula less than 1.2 * 10 ^ -7. although
	 * subject to catastrophic cancellation when z in very close to 0 from
	 * Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
	 */
	private static double erf(double z) {
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

	@Deprecated
	public static void main(String[] args) {

		final int n = 400;
		final int m = 400;

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
		for (int i = 0; i < n; i++)
			Y[i] = Math.sin(X[i][0]) > 0 ? 1 : -1;

		current = a;
		double[][] Xt = new double[m][1];
		for (int i = 0; i < m; i++)
			Xt[i][0] = current += step_m;

		GpDataset xy = new GpDataset(1, 100);
		xy.set(X, Y);
		GpDataset xt = new GpDataset(1);
		xt.set(Xt);

		GPCEP gp = new GPCEP(new KernelRBF());
		gp.setTrainingSet(xy);

		long t0;
		double elapsed;

		t0 = System.currentTimeMillis();
		ClassificationPosterior post = gp.getGpPosterior(xt);
		elapsed = (System.currentTimeMillis() - t0) / 1000d;
		for (int i = 0; i < m; i++) {
			System.out.print(X[i][0] + "\t");
			System.out.print(Y[i] + "\t");
			System.out.print(post.getClassProbabilities()[i] + "\n");
		}
		System.out.println("\nelapsed: " + elapsed + " sec");
		System.out.println("log-likelihood: " + gp.getMarginalLikelihood());
	}

}
