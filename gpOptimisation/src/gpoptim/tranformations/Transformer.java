package gpoptim.tranformations;

public interface Transformer {

	public double[] applyTransformation(double[] x);

	public double[] invertTransformation(double[] x);

}
