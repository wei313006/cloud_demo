package common.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author abing
 * @created 2025/3/12 11:16
 */

@Configuration
@EnableAsync
public class AsyncConfig {

    // 默认线程池
    @Bean(name = "defaultPool")
    public Executor defaultExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-Default-");
        executor.initialize();
        return executor;
    }

    // 高优先级线程池
    @Bean(name = "highPriorityPool")
    public Executor highPriorityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Async-HighPriority-");
        executor.initialize();
        return executor;
    }
}
