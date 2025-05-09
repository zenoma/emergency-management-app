package es.udc.fireproject.backend.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilter(new JwtFilter(authenticationManager(), jwtGenerator))
                .authorizeRequests()

                .antMatchers(HttpMethod.GET, "/fires").permitAll()
                .antMatchers(HttpMethod.POST, "/fires").hasAnyRole("COORDINATOR")
                .antMatchers(HttpMethod.GET, "/fires/{id}").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/fires/{id}/extinguishFire").hasAnyRole("COORDINATOR")
                .antMatchers(HttpMethod.POST, "/fires/{id}/extinguishQuadrant").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.PUT, "/fires/{id}").hasAnyRole("COORDINATOR")

                .antMatchers(HttpMethod.GET, "/logs/fires").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/logs/fires/{id}").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/logs/teams").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/logs/vehicles").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/logs/statistics").permitAll()

                .antMatchers(HttpMethod.POST, "/notices").permitAll()
                .antMatchers(HttpMethod.GET, "/notices/{id}").permitAll()
                .antMatchers(HttpMethod.GET, "/notices").permitAll()
                .antMatchers(HttpMethod.PUT, "/notices/{id}").permitAll()
                .antMatchers(HttpMethod.DELETE, "/notices/{id}").hasAnyRole("COORDINATOR")
                .antMatchers(HttpMethod.PUT, "/notices/{id}/status").hasAnyRole("COORDINATOR")
                .antMatchers(HttpMethod.POST, "/notices/{id}/images").permitAll()

                .antMatchers(HttpMethod.GET, "/organizations").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/organizations/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.DELETE, "/organizations/{id}").hasAnyRole("COORDINATOR")
                .antMatchers(HttpMethod.POST, "/organizations").hasAnyRole("COORDINATOR")
                .antMatchers(HttpMethod.PUT, "/organizations/{id}").hasAnyRole("COORDINATOR")

                .antMatchers(HttpMethod.GET, "/organizationTypes").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/organizationTypes/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/organizationTypes").hasAnyRole("COORDINATOR")

                .antMatchers(HttpMethod.GET, "/quadrants").permitAll()
                .antMatchers(HttpMethod.GET, "/quadrants/{gid}").permitAll()
                .antMatchers(HttpMethod.GET, "/quadrants/active").permitAll()
                .antMatchers(HttpMethod.POST, "/quadrants/{gid}/linkFire").hasAnyRole("MANAGER", "COORDINATOR")

                .antMatchers(HttpMethod.POST, "/teams").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/teams").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/teams/active").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/teams/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/teams/myTeam").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/teams/{id}/addUser").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.DELETE, "/teams/{id}").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/teams/{id}/deleteUser").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/teams/{id}/users").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.PUT, "/teams/{id}").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/teams/{id}/deploy").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/teams/{id}/retract").hasAnyRole("MANAGER", "COORDINATOR")

                .antMatchers(HttpMethod.POST, "/vehicles").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.DELETE, "/vehicles/{id}").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.PUT, "/vehicles/{id}").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/vehicles/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/vehicles").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.GET, "/vehicles/active").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/vehicles/{id}/deploy").hasAnyRole("MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/vehicles/{id}/retract").hasAnyRole("MANAGER", "COORDINATOR")

                .antMatchers(HttpMethod.GET, "/users/").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/users/signUp").permitAll()
                .antMatchers(HttpMethod.POST, "/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/users/loginFromServiceToken").permitAll()
                .antMatchers(HttpMethod.PUT, "/users/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/users/{id}/changePassword").hasAnyRole("USER", "MANAGER", "COORDINATOR")
                .antMatchers(HttpMethod.POST, "/users/{id}/updateRole").hasAnyRole("COORDINATOR")

                .anyRequest().permitAll();

        http.headers().frameOptions().disable();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);

        return source;

    }

}
