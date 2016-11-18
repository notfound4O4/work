package springbook.user.sqlservice.updatable;

import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import springbook.exception.SqlUpdateFailureException;
import springbook.issuetracker.sqlservice.UpdateableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
	EmbeddedDatabase db;

	@Override
	protected UpdateableSqlRegistry createUpdatableSqlRegistry() {
		db = new EmbeddedDatabaseBuilder().setType(HSQL)
				.addScript("classpath:springbook/user/sqlservice/updatable/sqlRegistrySchema.sql").build();
		
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		
		return embeddedDbSqlRegistry;
	}
	
	@After
	public void tearDown(){
		db.shutdown();
	}
	
	@Test
	public void transactionUpdate(){
		checkFind("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY9999!@#$", "Modified9999");
		
		try {
			sqlRegistry.updateSql(sqlmap);
			fail();
		} catch (SqlUpdateFailureException e) {
			// TODO: handle exception
		}
		
		checkFind("SQL1", "SQL2", "SQL3");
	}

}
