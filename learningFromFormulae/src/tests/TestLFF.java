package tests;

import gp.kernels.KernelRBF;
import gpoptim.GpoResult;
import gridSampling.LatinHypercubeSampler;

import java.io.FileInputStream;
import java.io.IOException;

import lff.LearnFromFormulae;
import lff.Parameter;
import modelChecking.MitlModelChecker;
import biopepa.BiopepaModel;

public class TestLFF {

	public static void main(String[] args) throws Exception {

		final String modelFile = "models/rumour.biopepa";
		final String mitlFile = "formulae/rumour.mtl";

		BiopepaModel biopepa = new BiopepaModel();
		biopepa.loadModel(modelFile);
		MitlModelChecker modelChecker = new MitlModelChecker(biopepa);
		modelChecker.loadProperties(mitlFile);

		Parameter[] params = new Parameter[2];
		params[0] = new Parameter("k_s", 0.001, 1);
		params[1] = new Parameter("k_r", 0.001, 1);

		LearnFromFormulae lff = new LearnFromFormulae(modelChecker);
		lff.setParams(params);

		lff.getOptions().setSimulationEndTime(5);
		lff.getOptions().setSimulationRuns(100);
		lff.getOptions().setSimulationTimepoints(200);

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

		final boolean[][] observations = ObservationsGenerator.generate(
				modelFile, mitlFile, lff.getOptions().getSimulationEndTime(),
				lff.getOptions().getSimulationRuns());

		GpoResult result = lff.performInference(observations);
		System.out.println(result);
	}

	protected static final String readFile(String filename) {
		FileInputStream input;
		byte[] fileData;
		try {
			input = new FileInputStream(filename);
			fileData = new byte[input.available()];
			input.read(fileData);
			input.close();
		} catch (IOException e) {
			fileData = new byte[0];
		}
		return new String(fileData);
	}

}
