package tests;

import gp.kernels.KernelRBF;
import gpoptim.GpoResult;
import gridSampling.LatinHypercubeSampler;

import java.io.IOException;

import lff.LearnFromFormulae;
import lff.Parameter;
import modelChecking.MitlModelChecker;
import simhya.model.flat.parser.ParseException;
import simhya.model.flat.parser.TokenMgrError;
import ucheck.SimhyaModel;

public class TestRobust {

	public static void main(String[] args) throws IOException,
			NumberFormatException, ParseException, TokenMgrError {

		SimhyaModel model = new SimhyaModel();
		model.loadModel("models/RUMORS.txt");
		MitlModelChecker modelChecker = new MitlModelChecker(model);
		modelChecker.loadProperties("formulae/rumour.mtl");

		Parameter[] params = new Parameter[2];
		params[0] = new Parameter("k_s", 0.001, 1);
		params[1] = new Parameter("k_r", 0.001, 1);

		LearnFromFormulae lff = new LearnFromFormulae(modelChecker);
		lff.setParams(params);
		
		model.setODE();
		lff.getOptions().setSimulationRuns(1);

		lff.getOptions().setSimulationEndTime(5);
		lff.getOptions().setSimulationTimepoints(1000);

		lff.getOptions().getGpoOptions()
				.setInitialSampler(new LatinHypercubeSampler(4));
		lff.getOptions().getGpoOptions().setInitialObservtions(100);
		lff.getOptions().getGpoOptions().setGridSampleNumber(50);
		lff.getOptions().getGpoOptions().setLogspace(false);
		lff.getOptions().getGpoOptions().setMaxIterations(600);
		lff.getOptions().getGpoOptions().setMaxFailedAttempts(100);
		lff.getOptions().getGpoOptions().setHeteroskedastic(true);
		lff.getOptions().getGpoOptions().setHyperparamOptimisation(!true);
		lff.getOptions().getGpoOptions().setUseDefaultHyperparams(true);
		lff.getOptions().getGpoOptions().setKernelGP(new KernelRBF());

		GpoResult result = lff.robustSystemDesign();
		System.out.println(result);
	}

}
