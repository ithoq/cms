package be.ttime.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = "be.ttime.modules",
        useDefaultFilters = false,
        includeFilters = {@ComponentScan.Filter(Configuration.class)}
)
public class PluginsConfig {
}
