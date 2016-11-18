package springbook.user.sqlservice;

import springbook.exception.SqlNotFoundException;

public class SqlRetrievalFailureException extends RuntimeException {
	
	public SqlRetrievalFailureException(SqlNotFoundException e){
		super(e);
	}
	
	public SqlRetrievalFailureException(String msg){
		super(msg);
	}
	
	public SqlRetrievalFailureException(String message, Throwable cause){
		super(message, cause);
	}
	
	
}
