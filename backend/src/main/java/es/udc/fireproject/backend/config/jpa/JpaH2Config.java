package es.udc.fireproject.backend.config.jpa;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Properties;

@Profile("h2")
@Configuration
public class JpaH2Config implements JpaConfig {

    @Override
    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.spatial.dialect.h2geodb.GeoDBDialect");

        return properties;
    }

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:fireproject");
        dataSourceBuilder.username("fireuser");
        dataSourceBuilder.password("fireuser");
        return dataSourceBuilder.build();
    }


}
