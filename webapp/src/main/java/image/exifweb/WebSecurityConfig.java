package image.exifweb;

import image.exifweb.web.security.AuthFailureHandler;
import image.exifweb.web.security.AuthSuccessHandler;
import image.exifweb.web.security.LogoutSuccessHandler;
import image.exifweb.web.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static exifweb.util.PropertiesUtils.propertiesOf;

/**
 * Created by adr on 2/17/18.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private ApplicationContext ac;
	@Value("${users.file}")
	private String usersFile;

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
		http.csrf().disable();
		http.httpBasic()
				.authenticationEntryPoint(this.restAuthenticationEntryPoint());
		http.formLogin()
				.loginProcessingUrl("/app/login")
				.passwordParameter("password").usernameParameter("userName")
				.successHandler(this.authSuccessHandler()).failureHandler(this.authFailureHandler());
		http.rememberMe()
				.userDetailsService(userDetailsService())
				.tokenValiditySeconds(1296000)
				.key("rememberMeKeyForExifWeb");
		http.logout()
				.logoutUrl("/app/logout").logoutSuccessHandler(this.logoutSuccessHandler())
				.deleteCookies("JSESSIONID", "SPRING_SECURITY_REMEMBER_ME_COOKIE");
	}

	/**
	 * "@Bean" usage: see javadoc for this override
	 * <p>
	 * exposing as @Bean the return of super.authenticationManagerBean()
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	/**
	 * according to javadoc I should use "@Bean @Override userDetailsServiceBean()"
	 * but that would allow for this method to be called 2x
	 *
	 * @return
	 */
	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(propertiesOf(this.ac.getResource(this.usersFile)));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
}
