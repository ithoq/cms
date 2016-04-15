package be.ttime.modules.foo.config;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("be.ttime.modules.foo")
public class FooCmsModuleConfig {

    @Autowired
    private Environment environment;

    @Bean(name = "fooPebbleEngine")
    public PebbleEngine pebbleEngine() {
        final String appMode = environment.getProperty("app.mode");
        final boolean isProduction = StringUtils.equals(appMode, "PRODUCTION");
        return new PebbleEngine.Builder()
                .loader(new ClasspathLoader())
                .cacheActive(isProduction)
                .build();
    }

}
