package springbook.user.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {
	
	@Autowired
	SimpleDriverDataSource dataSource;
	
//	@Bean
//	public UserDao userDao(){
//		UserDao userDao = new UserDao();
//		userDao.setDataSource(dataSource());
//		
//		return userDao;
//	}
	
	@Bean
	public ConnectionMaker connectionMaker(){
		return new DConnectionMaker();
	}
	
	@Bean
	public DataSource dataSource(){
		//SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/work?characterEncoding=UTF-8");
		dataSource.setUsername("user1");
		dataSource.setPassword("1234");
		
		return dataSource;
	}
}
