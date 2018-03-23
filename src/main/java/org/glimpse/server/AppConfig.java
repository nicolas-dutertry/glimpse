/*
 * © 1996-2014 Sopra HR Software. All rights reserved
 */
package org.glimpse.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import net.sf.ehcache.CacheManager;
import org.apache.commons.io.IOUtils;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
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
@ImportResource("classpath:org/glimpse/server/cxf.xml")
public class AppConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(Properties hibernateProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("glimpse");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties);

        return em;
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
			reader = new FileReader(file);
			properties.load(reader);
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
}
