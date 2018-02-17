package image.exifweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by adr on 2/16/18.
 */
@Configuration
@EnableAsync(mode = AdviceMode.ASPECTJ)
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true, mode = AdviceMode.ASPECTJ)
@EnableCaching(mode = AdviceMode.ASPECTJ)
@Import(DBConfig.class)
@ComponentScan(basePackageClasses = WebConfig.class, basePackages = "subtitles",
		excludeFilters = {@ComponentScan.Filter(Controller.class),
				@ComponentScan.Filter(ControllerAdvice.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
						classes = subtitles.App.class)})
public class RootConfig implements AsyncConfigurer, SchedulingConfigurer {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer p =
				new PropertySourcesPlaceholderConfigurer();
		p.setLocations(new ClassPathResource("exifweb.properties"),
				new ClassPathResource("subs-extract-app-config"),
				new ClassPathResource("classpath*:exifweb-overwrite.properties"));
		p.setIgnoreResourceNotFound(true);
		p.setIgnoreUnresolvablePlaceholders(true);
		return p;
	}

	/**
	 * http://www.baeldung.com/spring-cache-tutorial
	 * <p>
	 * <bean p:name="covers" class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean"/>
	 */
	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("covers");
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy"));
		Hibernate4Module hm = new Hibernate4Module();
		hm.disable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		hm.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
		objectMapper.registerModule(hm);
		return objectMapper;
	}

	/**
	 * <task:annotation-driven executor="asyncExecutor"/>
	 * <task:executor id="asyncExecutor" pool-size="1-4" queue-capacity="128" keep-alive="30"/>
	 *
	 * @return
	 */
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(128);
		executor.setThreadNamePrefix("async-");
		executor.setKeepAliveSeconds(30);
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		// null can be returned to keep the default settings (see EnableAsync javadoc)
		return null;
	}

	/**
	 * <task:annotation-driven scheduler="scheduler"/>
	 *
	 * @param taskRegistrar
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(scheduler());
	}

	/**
	 * <task:scheduler id="scheduler" pool-size="1"/>
	 *
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
	public Executor scheduler() {
		return Executors.newScheduledThreadPool(1);
	}
}
