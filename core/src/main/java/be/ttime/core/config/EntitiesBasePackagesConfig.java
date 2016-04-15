package be.ttime.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntitiesBasePackagesConfig {

    @Bean(name = "primaryEntitiesBasePackages")
    public EntitiesBasePackages primaryEntitiesBasePackages() {
        return new EntitiesBasePackages("be.ttime.core.persistence.model");
    }

}
