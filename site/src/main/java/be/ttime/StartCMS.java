package be.ttime;

import be.ttime.core.config.ApplicationConfig;
import be.ttime.core.config.CachingConfig;
import be.ttime.core.config.PluginsConfig;
import be.ttime.core.config.SecurityConfig;
import be.ttime.core.config.ServicesConfig;
import be.ttime.core.config.WebMvcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        ApplicationConfig.class,
        CachingConfig.class,
        SecurityConfig.class,
        ServicesConfig.class,
        WebMvcConfig.class,
        PluginsConfig.class
})
public class StartCMS extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StartCMS.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(StartCMS.class, args);

    }
}
