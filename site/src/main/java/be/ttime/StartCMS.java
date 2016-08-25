package be.ttime;

import be.ttime.core.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        ApplicationConfig.class,
        TestConfig.class,
        CachingConfig.class,
        SecurityConfig.class,
        WebMvcConfig.class
})
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
public class StartCMS extends SpringBootServletInitializer {

    public StartCMS() {
        super();
        //setRegisterErrorPageFilter(false);
    }

    public static void main(String[] args) {

        SpringApplication springApplication =
                new SpringApplication(StartCMS.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StartCMS.class);
    }
}
