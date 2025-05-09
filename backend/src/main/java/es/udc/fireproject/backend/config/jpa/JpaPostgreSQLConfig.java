package es.udc.fireproject.backend.config.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@Profile("postgresql")
public class JpaPostgreSQLConfig implements JpaConfig {

    @Override
    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.spatial.dialect.postgis.PostgisDialect");

        return properties;
    }

    @Bean
    public DataSource getDataSource() {

        DriverManagerDataSource driver = new DriverManagerDataSource();
        driver.setDriverClassName("org.postgresql.Driver");
        driver.setUrl("jdbc:postgresql:postgis_database");
        driver.setUsername("fireuser");
        driver.setPassword("fireuser");
        return driver;
    }
}