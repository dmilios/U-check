package cli;

public abstract class Log {

	private int warnings = 0;
	private int errors = 0;

	public abstract void print(String s);

	public abstract void println(String s);

	public abstract void println();

	final public int getWarnings() {
		return warnings;
	}
	
	final public int getErrors() {
		return errors;
	}
	
	final public void printWarning(String s) {
		warnings++;
		println("Warning: " + s);
	}

	final public void printError(String s) {
		errors++;
		println("Error: " + s);
	}

}
