package image.exifweb;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by adr on 2/17/18.
 */
@Configuration
@EnableAsync(mode = AdviceMode.ASPECTJ)
@EnableScheduling
public class AsyncAndSchedulingConfig implements AsyncConfigurer, SchedulingConfigurer {

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
