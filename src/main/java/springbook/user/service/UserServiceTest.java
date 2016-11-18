package springbook.user.service;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import springbook.exception.TestUserServiceException;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
@Transactional
//@TransactionConfiguration(defaultRollback=false)
public class UserServiceTest {

	@Autowired
	UserService userService;
	
	@Autowired
	UserService testUserService;

	@Autowired
	private UserDao userDao;

	@Autowired
	DataSource dataSource;

	@Autowired
	MailSender mailSender;

	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	ApplicationContext context;

	List<User> users;

	static class TestUserService extends UserServiceImpl {
		private String id = "ddd";

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) {
				throw new TestUserServiceException();
			}
			super.upgradeLevel(user);
		}
		
		public List<User> getAll(){
			for(User user : super.getAll()){
				super.update(user);
			}
			return null;
		}

	}

	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("aaa", "테스트1", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0,
						"to.escape.or.go.insane@gmail.com"),
				new User("bbb", "테스트2", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "punk3500@naver.com"),
				new User("ccc", "테스트3", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, "punk3500@naver.com"),
				new User("ddd", "테스트4", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "punk3500@naver.com"),
				new User("eee", "테스트5", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "punk3500@naver.com"));
	}

	@Test
	@Transactional
	public void transactionSync(){
		userService.deleteAll();
		userService.add(users.get(0));
		userService.add(users.get(1));
	}
	
	@Test
	public void upgradeLevels() throws Exception {

		UserServiceImpl userServiceImpl = new UserServiceImpl();

		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();

		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));

		checkUserAndLevel(updated.get(0), "bbb", Level.SILVER);
		checkUserAndLevel(updated.get(1), "ddd", Level.GOLD);

		List<String> requests = mockMailSender.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(requests.get(0), is(users.get(1).getEmail()));
		assertThat(requests.get(1), is(users.get(3).getEmail()));
	}

	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}

	}

	/**
	 * [mockito 적용 법칙.]
	 * 
	 * 1.인터페이스를 이용해 목 오브젝트를 만든다. 2.목 오브젝트가 리턴할 값이 있으면 이를 지정해준다. 메소드가 호출되면 예외를
	 * 강제로 던지게 만들 수도 있다. 3.테스트 대상 오브젝트에 DI해서 목 오브젝트가 테스트중에 사용되도록 만든다. 4.테스트 대상
	 * 오브젝트를 사용한 후에 목 오브젝트의 특정 메소드가 호출됐는지, 어떤 값을 가지고 몇 번 호출됐는지를 검증한다.
	 * 
	 * @throws Exception
	 */
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();

		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));

		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());

		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(3).getEmail()));
	}

	@Test
	@Rollback(false)
	public void add() {
		userDao.deleteAll();

		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));

	}

	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}

	@Test
	public void upgradeAllOrNothing() throws Exception {

		userDao.deleteAll();

		for (User user : users) {
			userDao.add(user);
		}

		try {
			this.testUserService.upgradeLevels();
			fail("TestUserServiceException excepted");
		} catch (TestUserServiceException e) {

		}

		checkLevelUpgraded(users.get(1), false);
	}
	
	@Test
	public void advisorAutoProxyCreator(){
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}

	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList();

		private MockUserDao(List<User> users) {
			this.users = users;
		}

		public List<User> getUpdated() {
			return updated;
		}

		public List<User> getAll() {
			return this.users;
		}

		public void update(User user) {
			updated.add(user);
		}

		public void add(User user) {
			throw new UnsupportedOperationException();
		}

		public User get(String id) {
			throw new UnsupportedOperationException();
		}

		public void deleteAll() {
			throw new UnsupportedOperationException();
		}

		public int getCount() {
			throw new UnsupportedOperationException();
		}
	}
	
	@Test(expected=TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute(){
		testUserService.getAll();
	}

}
