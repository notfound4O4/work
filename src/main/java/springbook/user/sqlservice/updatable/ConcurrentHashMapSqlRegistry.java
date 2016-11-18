package springbook.user.sqlservice.updatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import springbook.exception.SqlNotFoundException;
import springbook.exception.SqlUpdateFailureException;
import springbook.issuetracker.sqlservice.UpdateableSqlRegistry;

public class ConcurrentHashMapSqlRegistry implements UpdateableSqlRegistry {

	private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();
	
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null){
			throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		}
		
		return sql;
	}
	
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}
	
	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		if(sqlMap.get(key) == null){
			throw new SqlUpdateFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		}
		
		sqlMap.put(key, sql);
	}

	@Override
	public void updateSql(Map<String, String> sqlMap) throws SqlUpdateFailureException {
		for(Map.Entry<String, String> entry : sqlMap.entrySet()){
			updateSql(entry.getKey(), entry.getValue());
		}
	}

}
