package parsers;

import java.io.FileInputStream;
import java.io.IOException;

import ssa.Trajectory;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import expr.Context;
import expr.Variable;

public class ParserTestMiTL {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 179960378037349804L;

	public static final String readFile(String filename) {
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

	public static void testModelChecking() {
		String data = readFile("src/parsers/traj.csv");
		String lines[] = data.split("\n");
		int n = lines.length;

		double[] times = new double[n];
		double[] values = new double[n];
		for (int i = 0; i < lines.length; i++) {
			String[] words = lines[i].trim().split(" ");
			times[i] = Double.parseDouble(words[0]);
			values[i] = Double.parseDouble(words[1]);
		}

		Context ns = new Context();
		new Variable("LacZ", ns);
		Trajectory x = new Trajectory(times, ns, new double[][] { values });
		System.out.println(x);
		
		String s = "src/parsers/test_lacz.mtl";
		String text = readFile(s);
		MitlFactory factory = new MitlFactory(ns);
		MitlPropertiesList l = factory.constructProperties(text);
		
		MiTL prop = l.getProperties().get(0);
		System.out.println(prop);
		
		boolean b = prop.evaluate(x, 0);
		System.out.println("\nvalue: " + b);
	}

	public static void main(String args[]) throws Exception {

		testModelChecking();
		System.exit(0);

		Context ns = new Context();
		new Variable("LacZ", ns);
		// new Variable("I", ns);
		// new Variable("S", ns);
		// new Variable("R", ns);

		// String s = "src/parsers/rumours_qest2014.mtl";
		// String s = "src/parsers/test2.mtl";

		String s = "src/parsers/test_lacz.mtl";
		String text = readFile(s);

		// String data = readFile(s.substring(0, s.length() - 4) + ".dat");

		MitlFactory factory = new MitlFactory(ns);

		MitlPropertiesList l = factory.constructProperties(text);
		// factory.constructProperty("G[3.0, 5.0] (I>0.0)");
		// factory.constructDeclaration("const double T;");

		System.out.println(l);
	}

}
