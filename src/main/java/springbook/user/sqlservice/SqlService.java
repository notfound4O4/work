package springbook.user.sqlservice;

import org.springframework.context.annotation.Lazy;


public interface SqlService {
	
	String getSql(String key) throws SqlRetrievalFailureException ;
}
