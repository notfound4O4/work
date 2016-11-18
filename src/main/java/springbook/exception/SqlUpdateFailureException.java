package springbook.exception;

public class SqlUpdateFailureException extends RuntimeException {

	public SqlUpdateFailureException(Throwable cause){
		super(cause);
	}

	public SqlUpdateFailureException(String string) {
		super(string);
	}
}
