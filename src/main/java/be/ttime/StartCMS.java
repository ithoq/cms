package be.ttime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import be.ttime.core.config.ApplicationConfig;

@SpringBootApplication
@ComponentScan({ "be.ttime"})
@Import({ApplicationConfig.class})

public class StartCMS extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(StartCMS.class);
	}
	
    public static void main(String[] args) {
        SpringApplication.run(StartCMS.class, args);
        
    }
}
