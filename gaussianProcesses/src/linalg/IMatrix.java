package linalg;

public interface IMatrix {

	public double[] getData();
	
	public int getRows();

	public int getColumns();
	
	public int getLength();

	public double get(int i, int j);

	public void put(int i, int j, double v);

	public double get(int i);

	public void put(int i, double v);

	public IMatrix getColumn(int i);
	
	public void putColumn(int i, IMatrix column);

	public IMatrix getRow(int i);
	
	public void putRow(int i, IMatrix row);

	public void copy(IMatrix arg);
	
	public IMatrix duplicate();
	
	public IMatrix transpose();

	public IMatrix add(IMatrix arg);

	public IMatrix add(double arg);

	public IMatrix sub(IMatrix arg);

	public IMatrix sub(double arg);

	public IMatrix rsub(IMatrix arg);

	public IMatrix rsub(double arg);

	public IMatrix mul(IMatrix arg);

	public IMatrix mul(double arg);

	public IMatrix mmul(IMatrix arg);

	public double dot(IMatrix arg);

	public IMatrix div(IMatrix arg);

	public IMatrix div(double arg);

	public IMatrix rdiv(IMatrix arg);

	public IMatrix rdiv(double arg);

	public IMatrix neg();
	
	public double sum();
	
	public IMatrix diag();
	
	public IMatrix repmat(int rows, int columns);
	
	public double max();
	
	public double min();
	
	public IMatrix sort();

}
