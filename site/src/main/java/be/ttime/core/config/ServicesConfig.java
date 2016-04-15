package be.ttime.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "be.ttime.core.model",
        "be.ttime.core.persistence",
        "be.ttime.core.registration",
        "be.ttime.core.util",
})
@Import({
        PluginsConfig.class
})
public class ServicesConfig {
}
