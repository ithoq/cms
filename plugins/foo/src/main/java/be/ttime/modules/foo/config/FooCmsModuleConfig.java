package be.ttime.modules.foo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("be.ttime.modules.foo")
public class FooCmsModuleConfig {

    @Autowired
    private Environment environment;
/*
    @Bean(name = "fooPebbleEngine")
    public PebbleEngine pebbleEngine() {
        final String appMode = environment.getProperty("app.mode");
        final boolean isProduction = StringUtils.equals(appMode, "PRODUCTION");
        return new PebbleEngine.Builder()
                .loader(new ClasspathLoader())
                .cacheActive(isProduction)
                .build();
    }*/

}
