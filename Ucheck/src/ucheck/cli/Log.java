package ucheck.cli;

public abstract class Log {

	private int warnings = 0;
	private int errors = 0;

	public int getWarnings() {
		return warnings;
	}

	public int getErrors() {
		return errors;
	}

	final public void printWarning(String s) {
		warnings++;
		this.println("Warning: " + s);
	}

	final public void printError(String s) {
		errors++;
		this.println("Error: " + s);
	}

	abstract public void print(String s);

	abstract public void println(String s);

	abstract public void println();

}
