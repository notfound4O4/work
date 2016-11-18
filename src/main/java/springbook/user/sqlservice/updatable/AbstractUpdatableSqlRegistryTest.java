package springbook.user.sqlservice.updatable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import springbook.exception.SqlNotFoundException;
import springbook.exception.SqlUpdateFailureException;
import springbook.issuetracker.sqlservice.UpdateableSqlRegistry;

public abstract class AbstractUpdatableSqlRegistryTest {
	UpdateableSqlRegistry sqlRegistry;

	@Before
	public void setUp() {
		sqlRegistry = createUpdatableSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}

	abstract protected UpdateableSqlRegistry createUpdatableSqlRegistry();

	@Test
	public void find() {
		checkFind("SQL1", "SQL2", "SQL3");
	}

	protected void checkFind(String expected1, String expected2, String expected3) {
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
	}

	@Test(expected = SqlNotFoundException.class)
	public void unknownKey() {
		sqlRegistry.findSql("SQL999!@#$");
	}

	@Test
	public void updateMulti() {
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");

		sqlRegistry.updateSql(sqlmap);
		checkFind("Modified1", "SQL2", "Modified3");
	}

	@Test(expected = SqlUpdateFailureException.class)
	public void updateWithNotExistingKey() {
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}
}
