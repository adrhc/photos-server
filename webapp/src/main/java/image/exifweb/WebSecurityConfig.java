package image.exifweb;

import image.exifweb.web.security.AuthFailureHandler;
import image.exifweb.web.security.AuthSuccessHandler;
import image.exifweb.web.security.LogoutSuccessHandler;
import image.exifweb.web.security.RestAuthenticationEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static exifweb.util.PropertiesUtils.propertiesOf;

/**
 * Created by adr on 2/17/18.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private ApplicationContext ac;
	@Value("${users.file}")
	private String usersFile;
	@Value("${servlet-mapping.app:}")
	private String appMapp;
	@Value("${servlet-mapping.jsp:}")
	private String jspMapp;

	@Bean
	RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}

	@Bean
	AuthSuccessHandler authSuccessHandler() {
		return new AuthSuccessHandler();
	}

	@Bean
	AuthFailureHandler authFailureHandler() {
		return new AuthFailureHandler();
	}

	@Bean
	LogoutSuccessHandler logoutSuccessHandler() {
		return new LogoutSuccessHandler();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("configuring security (appMapp=\"" + this.appMapp + "\", jspMapp=\"" + this.jspMapp + "\")");
		http.csrf().disable();
		http.authorizeRequests()
				.antMatchers(this.jspMapp + "/**").permitAll();
		http.httpBasic()
				.authenticationEntryPoint(this.restAuthenticationEntryPoint());
		http.formLogin()
				.loginProcessingUrl(this.appMapp + "/login")
				.passwordParameter("password").usernameParameter("userName")
				.successHandler(this.authSuccessHandler()).failureHandler(this.authFailureHandler());
		http.rememberMe()
				.tokenValiditySeconds(1296000)
				.key("rememberMeKeyForExifWeb");
		http.logout()
				.logoutUrl(this.appMapp + "/logout").logoutSuccessHandler(this.logoutSuccessHandler())
				.deleteCookies("JSESSIONID", "SPRING_SECURITY_REMEMBER_ME_COOKIE");
	}

	/**
	 * according to javadoc I should use:
	 * "@Bean @Override userDetailsServiceBean()"
	 * but that would allow for this method to be called 2x
	 */
	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		log.info("UserDetailsService using:\n\t" + this.usersFile);
		return new InMemoryUserDetailsManager(propertiesOf(this.ac.getResource(this.usersFile)));
	}
}
