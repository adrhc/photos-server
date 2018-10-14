package image.exifweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by adr on 2/17/18.
 */
@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
public class AsyncAndSchedulingConfig implements AsyncConfigurer, SchedulingConfigurer {

	/**
	 * <task:annotation-driven executor="asyncExecutor"/>
	 * <task:executor id="asyncExecutor" pool-size="1-4" queue-capacity="128" keep-alive="30"/>
	 */
	@Override
	public Executor getAsyncExecutor() {
		return asyncExecutor();
	}

	@Bean
	public ThreadPoolTaskExecutor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(128);
		executor.setThreadNamePrefix("async-");
		executor.setKeepAliveSeconds(30);
		executor.initialize();
		return executor;
	}

	/**
	 * <task:annotation-driven scheduler="scheduler"/>
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(scheduler());
	}

	/**
	 * <task:scheduler id="scheduler" pool-size="1"/>
	 */
	@Bean(destroyMethod = "shutdown")
	public ExecutorService scheduler() {
		return Executors.newScheduledThreadPool(1);
	}
}
