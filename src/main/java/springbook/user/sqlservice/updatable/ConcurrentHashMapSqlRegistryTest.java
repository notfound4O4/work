package springbook.user.sqlservice.updatable;

import springbook.issuetracker.sqlservice.UpdateableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
	@Override
	protected UpdateableSqlRegistry createUpdatableSqlRegistry() {
		return new ConcurrentHashMapSqlRegistry();
	}
}
