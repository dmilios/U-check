package tests;

public class TestObservations {

	public static void main(String[] args) throws Exception {
		String model = "models/rumour.biopepa";
		String prop = "formulae/rumour.mtl";
		boolean[][] data = ObservationsGenerator.generate(model, prop, 5, 100);
		ObservationsGenerator.savetofile("formulae/rumour.dat", data);
	}

}
