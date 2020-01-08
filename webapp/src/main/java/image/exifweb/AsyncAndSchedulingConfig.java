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
@EnableAsync
@EnableScheduling
public class AsyncAndSchedulingConfig implements AsyncConfigurer, SchedulingConfigurer {
	/**
	 * <task:annotation-driven executor="asyncExecutor"/>
	 * <task:executor id="asyncExecutor" pool-size="1-4" queue-capacity="128" keep-alive="30"/>
	 */
	@Override
	public Executor getAsyncExecutor() {
		return this.asyncExecutor();
	}

	/**
	 * <task:annotation-driven scheduler="scheduler"/>
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(this.scheduler());
	}

	/**
	 * <task:scheduler id="scheduler" pool-size="1"/>
	 */
	@Bean(destroyMethod = "shutdown")
	public ExecutorService scheduler() {
		return Executors.newScheduledThreadPool(1);
	}

	/**
	 * corepoolsize vs maxpoolsize:
	 * https://stackoverflow.com/questions/1878806/what-is-the-difference-between-corepoolsize-and-maxpoolsize-in-the-spring-thread
	 */
	@Bean(value = {"asyncExecutor", "threadPoolTaskExecutor"})
	public ThreadPoolTaskExecutor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
//		executor.setQueueCapacity(executor.getMaxPoolSize() * 2);
		executor.setThreadNamePrefix("async-");
		executor.setKeepAliveSeconds(30);
		executor.initialize();
		return executor;
	}

	@Bean
	public ExecutorService executorService() {
		return this.asyncExecutor().getThreadPoolExecutor();
	}
}
