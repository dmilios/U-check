package tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import mitl.MiTL;
import mitl.MitlPropertiesList;
import parsers.MitlFactory;
import ssa.CTMCModel;
import gp.kernels.KernelRBF;
import gpoMC.LearnFromFormulae;
import gpoMC.Parameter;
import gpoptim.GpoResult;
import gridSampling.LatinHypercubeSampler;
import biopepa.BiopepaFile;

public class TestLFF {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		final String modelFile = "models/rumour.biopepa";
		BiopepaFile biopepa = new BiopepaFile(modelFile);

		final String mitlFile = "formulae/rumour.mtl";
		final String mitlText = readFile(mitlFile);

		CTMCModel model = biopepa.getModel();
		Parameter[] params = new Parameter[2];
		params[0] = new Parameter("k_s", 0.001, 1);
		params[1] = new Parameter("k_r", 0.001, 1);

		MitlFactory factory = new MitlFactory(model.getStateVariables());
		MitlPropertiesList l = factory.constructProperties(mitlText);
		ArrayList<MiTL> list = l.getProperties();
		MiTL[] formulae = new MiTL[list.size()];
		list.toArray(formulae);

		LearnFromFormulae lff = new LearnFromFormulae();
		lff.setModel(model);
		lff.setParams(params);

		lff.getOptions().setSimulationEndTime(5);
		lff.getOptions().setSimulationRuns(100);
		lff.getOptions().setSimulationTimepoints(200);

		lff.getOptions().getGpoOptions()
				.setInitialSampler(new LatinHypercubeSampler(4));
		lff.getOptions().getGpoOptions().setInitialObservtions(100);
		lff.getOptions().getGpoOptions().setGridSampleNumber(50);
		lff.getOptions().getGpoOptions().setLogspace(!false);
		lff.getOptions().getGpoOptions().setMaxIterations(600);
		lff.getOptions().getGpoOptions().setMaxFailedAttempts(100);
		lff.getOptions().getGpoOptions().setHeteroskedastic(true);
		lff.getOptions().getGpoOptions().setHyperparamOptimisation(!true);
		lff.getOptions().getGpoOptions().setUseDefaultHyperparams(true);
		lff.getOptions().getGpoOptions().setKernelGP(new KernelRBF());

		lff.setBiopepa(biopepa);
		lff.setMitlText(mitlText);

		ObservationsGenerator gen = new ObservationsGenerator();
		final boolean[][] observations = gen.generate(model, formulae, lff
				.getOptions().getSimulationEndTime());

		GpoResult result = lff.performInference(formulae, observations);
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