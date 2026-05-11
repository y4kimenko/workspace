package by.diplom.workspace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({StaticResourceConfig.class, SecurityConfig.class, SchedulerConfig.class})
public class AppConfig {
}
