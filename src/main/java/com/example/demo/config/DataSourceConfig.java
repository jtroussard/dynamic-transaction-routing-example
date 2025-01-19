package com.example.demo.config;

import com.example.demo.enums.DataSourceType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Component
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        basePackages = {
                "com.example.demo.repository.animal"
        }
)
public class DataSourceConfig {

    private final DatabaseProperties databaseProperties;

    public DataSourceConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    private DataSource createHikariDataSource(DatabaseProperties.DataSourceConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName(config.getDriverClassName());
        hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        return createHikariDataSource(databaseProperties.getPrimary());
    }

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource() {
        return createHikariDataSource(databaseProperties.getSecondary());
    }

    @Bean(name = "routingDataSource")
    public TransactionRoutingDataSource routingDataSource(
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            @Qualifier("secondaryDataSource") DataSource secondaryDataSource)
    {
        log.info("[TUNA-ROUTING-DATASOURCE] Initializing RoutingDataSource...");
        TransactionRoutingDataSource routingDataSource = new TransactionRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.READ_WRITE, primaryDataSource);
        targetDataSources.put(DataSourceType.READ_ONLY, secondaryDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("transactionAwareDataSource") DataSource transactionAwareDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(transactionAwareDataSource); // Now using the meaningful name
        em.setPackagesToScan("com.example.demo.entity"); // Update with your entity package
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        em.setJpaPropertyMap(jpaProperties);
        return em;
    }


    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        log.info("[TUNA-JPATRANSACTION-MANAGER] TransactionManager uses EntityManagerFactory: {}", entityManagerFactory);
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "transactionAwareDataSource")
    @DependsOn({"primaryDataSource", "secondaryDataSource", "routingDataSource"})
    public DataSource transactionAwareDataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }


}
