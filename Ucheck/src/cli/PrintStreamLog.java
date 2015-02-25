package cli;

import java.io.PrintStream;

public final class PrintStreamLog extends Log {

	final private PrintStream stream;
	
	public PrintStreamLog(PrintStream stream) {
		this.stream = stream;
	}

	@Override
	public void print(String s) {
		stream.print(s);
	}

	@Override
	public void println(String s) {
		stream.println(s);
	}

	@Override
	public void println() {
		stream.println();
	}

}
