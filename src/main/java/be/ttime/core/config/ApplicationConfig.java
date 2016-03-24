package be.ttime.core.config;

import be.ttime.Application;
import be.ttime.core.PebbleExtension;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(basePackageClasses = Application.class, excludeFilters = @Filter({Controller.class, Configuration.class}))
@PropertySource(value = "classpath:application.properties")
class ApplicationConfig {

    @Value("${app.mode}")
    private String appMode;

    @Autowired
    private PebbleExtension pebbleExtension;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    @Bean
    public StringLoader stringLoader() {
        StringLoader loader = new StringLoader();
        loader.setCharset("UTF-8");
        return loader;
    }

    @Bean(name = "pebbleStringEngine")
    public PebbleEngine pebbleStringEngine() {
        return new PebbleEngine.Builder()
                .cacheActive(appMode.equals("PRODUCTION"))
                .loader(stringLoader())
                .extension(pebbleExtension)
                .extension(springExtension())
                .build();
    }
}