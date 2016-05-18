package be.ttime.modules.foo.config;

import be.ttime.core.config.EntitiesBasePackages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
<<<<<<< Updated upstream
@EnableJpaRepositories(basePackages = "be.ttime.modules.foo.repository")
public class FooJpaConfig {

    @Bean(name = "fooEntitiesBasePackages")
    public EntitiesBasePackages fooEntitiesBasePackages() {
        return new EntitiesBasePackages("be.ttime.modules.foo.model");
    }

=======
@EnableJpaRepositories(
        basePackages = "be.ttime.modules.foo.repository",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
public class FooJpaConfig {
//
//    @Bean(name = "fooEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final @Qualifier("primaryJpaProperties") Properties jpaProperties,
//                                                                       final @Qualifier("dataSource") DataSource dataSource) {
//        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setDataSource(dataSource);
//        entityManagerFactoryBean.setPersistenceUnitName("foo");
//        entityManagerFactoryBean.setPackagesToScan("be.ttime.modules.foo.model");
//        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        entityManagerFactoryBean.setJpaProperties(jpaProperties);
//
//        return entityManagerFactoryBean;
//    }
//
//    @Bean(name = "fooTransactionManager")
//    public PlatformTransactionManager transactionManager(final @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//
>>>>>>> Stashed changes
}
