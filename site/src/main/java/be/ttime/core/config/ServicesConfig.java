package be.ttime.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "be.ttime.core.model",
        "be.ttime.core.persistence",
        "be.ttime.core.registration",
        "be.ttime.core.util",
})
public class ServicesConfig {
}
