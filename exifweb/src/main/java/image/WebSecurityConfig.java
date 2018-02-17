package image;

import image.exifweb.util.frameworks.spring.security.AuthFailureHandler;
import image.exifweb.util.frameworks.spring.security.AuthSuccessHandler;
import image.exifweb.util.frameworks.spring.security.LogoutSuccessHandler;
import image.exifweb.util.frameworks.spring.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Created by adr on 2/17/18.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,
		mode = AdviceMode.ASPECTJ, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Inject
	private DataSource dataSource;
	@Inject
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	@Inject
	private AuthSuccessHandler authSuccessHandler;
	@Inject
	private AuthFailureHandler authFailureHandler;
	@Inject
	private LogoutSuccessHandler logoutSuccessHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requiresChannel()
				.antMatchers("/app/https/*").requiresSecure();
		http.authorizeRequests()
				.antMatchers("/app/https/*").authenticated()
				.antMatchers("/app/secure/*").authenticated();
		http.httpBasic()
				.authenticationEntryPoint(restAuthenticationEntryPoint);
		http.formLogin()
				.loginProcessingUrl("/app/login")
				.passwordParameter("password").usernameParameter("userName")
				.successHandler(authSuccessHandler).failureHandler(authFailureHandler);
		http.rememberMe()
				.userDetailsService(userDetailsServiceBean())
				.tokenValiditySeconds(1296000)
				.key("kOQoW357t8HwbeRh7oxSXoXSGmVERKMcGENOxm2qrLZFOF8bxJB4GaIqSoTu9yy");
		http.logout()
				.logoutUrl("/app/logout").logoutSuccessHandler(logoutSuccessHandler)
				.deleteCookies("JSESSIONID", "SPRING_SECURITY_REMEMBER_ME_COOKIE");
	}

	@Bean
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return super.userDetailsServiceBean();
	}

	@Override
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
