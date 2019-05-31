package image.exifweb;

import image.photos.PhotosConfig;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subtitles.SubtitlesConfig;

/**
 * Created by adr on 2/16/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 * Scanner is including everything other than the listed classes.
 */
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter({Configuration.class,
		Controller.class, ControllerAdvice.class, RestControllerAdvice.class}))
@Import({SubtitlesConfig.class, PhotosConfig.class, WebSecurityConfig.class,
		AsyncAndSchedulingConfig.class, SpringCacheConfig.class, RootContextUtilities.class})
@PropertySource("classpath:/exifweb.properties")
public class RootConfig {
	/**
	 * corepoolsize vs maxpoolsize:
	 * https://stackoverflow.com/questions/1878806/what-is-the-difference-between-corepoolsize-and-maxpoolsize-in-the-spring-thread
	 */
	@Bean(value = {"asyncExecutor", "threadPoolTaskExecutor"})
	public ThreadPoolTaskExecutor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
		executor.setQueueCapacity(executor.getMaxPoolSize() / 2);
		executor.setThreadNamePrefix("async-");
		executor.setKeepAliveSeconds(30);
		executor.initialize();
		return executor;
	}
}
