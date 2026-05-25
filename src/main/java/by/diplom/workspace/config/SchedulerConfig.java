package by.diplom.workspace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    /**
     * Пул потоков для планировщика задач.
     * poolSize = 3 — достаточно для одновременного удаления нескольких почт;
     * при большой нагрузке (много регистраций одновременно) увеличьте значение.
     */
    @Bean(name = "emailVerificationTaskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3);
        scheduler.setThreadNamePrefix("email-expiry-");
        scheduler.setErrorHandler(throwable ->
                // Ошибки в задачах не должны убивать поток планировщика
                System.err.println("[email-expiry] Ошибка при удалении почты: " + throwable.getMessage())
        );
        scheduler.initialize();
        return scheduler;
    }
}

