package be.ttime.core.config;

import be.ttime.core.persistence.interceptor.FileInterceptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.EmptyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "be.ttime.core.persistence.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@Import({
        EntitiesBasePackagesConfig.class
})
public class JpaConfig {

    @Autowired
    private Environment env;

    @Autowired
    private Set<EntitiesBasePackages> entitiesBasePackages;

    /**
     * HikariCP is used as default connection pool in the generated application. The default configuration is used
     */
    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(env.getProperty("dataSource.driverClassName"));
        config.setJdbcUrl(env.getProperty("dataSource.url"));
        config.setUsername(env.getProperty("dataSource.username"));
        config.setPassword(env.getProperty("dataSource.password"));
        config.setLeakDetectionThreshold(8000);
        config.setIdleTimeout(10000);
        config.setConnectionTimeout(10000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        int poolsize= Integer.parseInt(env.getProperty("hikari.pool.size"));
        config.setMaximumPoolSize(poolsize); // for a little website
        // maybe interseting : config.setIdleTimeout();

        return new HikariDataSource(config);
    }

    @Bean(name = "jpaProperties")
    public Properties jpaProperties() {
        final Properties jpaProperties = new Properties();
        jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, env.getProperty("hibernate.dialect"));
        jpaProperties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, env.getProperty("hibernate.hbm2ddl.auto")); // create drop all , update, update ;)
        jpaProperties.put(org.hibernate.cfg.Environment.USE_SECOND_LEVEL_CACHE, "false");
        jpaProperties.put(org.hibernate.cfg.Environment.USE_QUERY_CACHE, "false");
        jpaProperties.put(org.hibernate.cfg.Environment.SHOW_SQL, "false");
        jpaProperties.put(org.hibernate.cfg.Environment.FORMAT_SQL, "true");
        jpaProperties.put(org.hibernate.cfg.Environment.USE_SQL_COMMENTS, "true");
        jpaProperties.put("org.hibernate.type", "TRACE");

        jpaProperties.put("hibernate.ejb.interceptor", hibernateInterceptor());

        return jpaProperties;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final @Qualifier("jpaProperties") Properties jpaProperties,
                                                                       final @Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPersistenceUnitName("core");
        final Set<String> packages = entitiesBasePackages
                .stream()
                .flatMap(entitiesPackageConfig -> Arrays.asList(entitiesPackageConfig.getBasePackages()).stream())
                .collect(Collectors.toSet());
        entityManagerFactoryBean.setPackagesToScan(packages.toArray(new String[packages.size()]));
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        //        String test = env.getProperty("hibernate.dialect");
        //        jpaProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(final @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public EmptyInterceptor hibernateInterceptor() {
        return new FileInterceptor();
    }

}
