package cli;
public class UndefinedOptionException extends Exception {

	private static final long serialVersionUID = -6355627751694186899L;

	public UndefinedOptionException(String msg) {
		super(msg);
	}
}
