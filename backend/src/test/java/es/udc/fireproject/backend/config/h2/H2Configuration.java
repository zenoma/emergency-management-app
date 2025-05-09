package es.udc.fireproject.backend.config.h2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("test")
public class H2Configuration {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:fireproject");
        dataSourceBuilder.username("fireuser");
        dataSourceBuilder.password("fireuser");
        return dataSourceBuilder.build();
    }

    @ConfigurationProperties(prefix = "h2.console")
    public static class H2Console {
        Boolean enable = true;
    }

}
