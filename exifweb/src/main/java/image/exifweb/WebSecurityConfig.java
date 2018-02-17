package image.exifweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import javax.sql.DataSource;

/**
 * Created by adr on 2/17/18.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource dataSource;

	protected void configure(HttpSecurity http) throws Exception {
	}

	protected UserDetailsService userDetailsService() {
		JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
		jdbcDao.setUsersByUsernameQuery("select username,password,enabled from user where username = ?");
		jdbcDao.setGroupAuthoritiesByUsernameQuery("select g.id, g.group_name, ga.authority from groups g JOIN group_members gm ON gm.group_id = g.id JOIN group_authorities ga ON ga.group_id = g.id JOIN user u ON u.id = gm.user_id WHERE u.username = ?");
		jdbcDao.setEnableGroups(true);
		jdbcDao.setEnableAuthorities(false);
		jdbcDao.setDataSource(dataSource);
		return jdbcDao;
	}
}
