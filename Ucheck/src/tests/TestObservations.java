package tests;

import ucheck.prism.PrismCtmcModel;
import model.ModelInterface;

public class TestObservations {

	public static void main(String[] args) throws Exception {
		String file = "models/rumour.sm";
		String prop = "formulae/rumour_sm.mtl";
		ModelInterface model = new PrismCtmcModel();
		model.loadModel(file);
		boolean[][] data = ObservationsGenerator.generate(model, prop, 5, 100);
		ObservationsGenerator.savetofile("formulae/rumour.dat", data);
	}

}
