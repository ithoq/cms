package be.ttime.core.config;

import be.ttime.Application;
import be.ttime.core.PebbleExtension;
import be.ttime.core.model.DatabaseMessageSourceBase;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.spring4.extension.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;

import java.util.Properties;

@Configuration
@ComponentScan(basePackageClasses = Application.class, excludeFilters = @Filter({Controller.class, Configuration.class}))
@PropertySource(value = {"classpath:application.properties", 
						 "classpath:persistence.properties",
						 "classpath:email.properties",
})
@PropertySource(value = {"file:config/application.properties",
						 "file:config/persistence.properties",
						 "file:config/email.properties"
						 
}, ignoreResourceNotFound=true)
public class ApplicationConfig {

    @Value("${app.mode}")
    private String appMode;

    @Autowired
    private Environment env;

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

    @Bean
    public JavaMailSenderImpl javaMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(env.getProperty("smtp.host"));
        mailSenderImpl.setPort(env.getProperty("smtp.port", Integer.class));
        mailSenderImpl.setProtocol(env.getProperty("smtp.protocol"));
        mailSenderImpl.setUsername(env.getProperty("smtp.username"));
        mailSenderImpl.setPassword(env.getProperty("smtp.password"));
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "messageSource")
    public MessageSource messageSource() {

        return new DatabaseMessageSourceBase();
    }

}