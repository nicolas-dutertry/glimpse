/*
 * © 1996-2014 Sopra HR Software. All rights reserved
 */
package org.glimpse.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import net.sf.ehcache.CacheManager;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author ndutertry
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.glimpse.server")
public class AppConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Properties hibernateProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("glimpse");
        em.setDataSource(dataSource);

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties);

        return em;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource(org.apache.commons.configuration.Configuration configuration) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("net.bull.javamelody.JdbcDriver");
        dataSource.setUrl(configuration.getString("datasource.url"));
        dataSource.setUsername(configuration.getString("datasource.username"));
        dataSource.setPassword(configuration.getString("datasource.password"));
        dataSource.setConnectionProperties("driver=" + configuration.getString("datasource.driver"));
        dataSource.setTestOnBorrow(configuration.getBoolean("datasource.testOnBorrow", true));
        dataSource.setTestOnCreate(configuration.getBoolean("datasource.testOnCreate", false));
        dataSource.setTestOnReturn(configuration.getBoolean("datasource.testOnReturn", false));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(name = "hibernateProperties")
    public Properties hibernateProperties(GlimpseManager glimpseManager) throws IOException {
        FileReader reader = null;
        try {
            Properties properties = new Properties();
            File file = new File(glimpseManager.getConfigurationDirectory(), "hibernate.properties");
            if(file.exists()) {
                reader = new FileReader(file);
                properties.load(reader);
            }
            return properties;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Bean
    public BasicPasswordEncryptor passwordEncryptor() {
        return new BasicPasswordEncryptor();
    }

    @Bean
    public org.apache.commons.configuration.Configuration configuration(GlimpseManager glimpseManager) {
        return glimpseManager.getConfiguration();
    }

    @Bean
    public CacheManager cacheManager() {
        return CacheManager.create();
    }
    
    @Bean(destroyMethod = "close")
    public HttpClient httpClient(HttpRoutePlanner httpRoutePlanner) {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setRoutePlanner(httpRoutePlanner);
        return builder.build();
    }
}
