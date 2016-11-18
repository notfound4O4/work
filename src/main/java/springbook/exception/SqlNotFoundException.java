package springbook.exception;

public class SqlNotFoundException extends RuntimeException {

	public SqlNotFoundException(Throwable cause){
		super(cause);
	}

	public SqlNotFoundException(String string) {
		super(string);
	}
	
	public SqlNotFoundException(String string, Throwable cause) {
		super(string, cause);
	}
}
