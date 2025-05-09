package es.udc.fireproject.backend.rest.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


  @Autowired
  private JwtGenerator jwtGenerator;


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
      throws Exception {
    http
        .cors(withDefaults())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilter(new JwtFilter(authenticationManager, jwtGenerator))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/fires").permitAll()
            .requestMatchers(HttpMethod.POST, "/fires").hasAnyRole("COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/fires/{id}").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/fires/{id}/extinguishFire").hasAnyRole("COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/fires/{id}/extinguishQuadrant").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.PUT, "/fires/{id}").hasAnyRole("COORDINATOR")

            .requestMatchers(HttpMethod.GET, "/logs/fires").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/logs/fires/{id}").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/logs/teams").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/logs/vehicles").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/logs/statistics").permitAll()

            .requestMatchers(HttpMethod.POST, "/notices").permitAll()
            .requestMatchers(HttpMethod.GET, "/notices/{id}").permitAll()
            .requestMatchers(HttpMethod.GET, "/notices").permitAll()
            .requestMatchers(HttpMethod.PUT, "/notices/{id}").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/notices/{id}").hasAnyRole("COORDINATOR")
            .requestMatchers(HttpMethod.PUT, "/notices/{id}/status").hasAnyRole("COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/notices/{id}/images").permitAll()

            .requestMatchers(HttpMethod.GET, "/organizations").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/organizations/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.DELETE, "/organizations/{id}").hasAnyRole("COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/organizations").hasAnyRole("COORDINATOR")
            .requestMatchers(HttpMethod.PUT, "/organizations/{id}").hasAnyRole("COORDINATOR")

            .requestMatchers(HttpMethod.GET, "/organizationTypes").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/organizationTypes/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/organizationTypes").hasAnyRole("COORDINATOR")

            .requestMatchers(HttpMethod.GET, "/quadrants").permitAll()
            .requestMatchers(HttpMethod.GET, "/quadrants/{gid}").permitAll()
            .requestMatchers(HttpMethod.GET, "/quadrants/active").permitAll()
            .requestMatchers(HttpMethod.POST, "/quadrants/{gid}/linkFire").hasAnyRole("MANAGER", "COORDINATOR")

            .requestMatchers(HttpMethod.POST, "/teams").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/teams").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/teams/active").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/teams/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/teams/myTeam").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/teams/{id}/addUser").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.DELETE, "/teams/{id}").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/teams/{id}/deleteUser").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/teams/{id}/users").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.PUT, "/teams/{id}").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/teams/{id}/deploy").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/teams/{id}/retract").hasAnyRole("MANAGER", "COORDINATOR")

            .requestMatchers(HttpMethod.POST, "/vehicles").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.DELETE, "/vehicles/{id}").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.PUT, "/vehicles/{id}").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/vehicles/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/vehicles").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.GET, "/vehicles/active").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/vehicles/{id}/deploy").hasAnyRole("MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/vehicles/{id}/retract").hasAnyRole("MANAGER", "COORDINATOR")

            .requestMatchers(HttpMethod.GET, "/users/").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/users/signUp").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/loginFromServiceToken").permitAll()
            .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/users/{id}/changePassword").hasAnyRole("USER", "MANAGER", "COORDINATOR")
            .requestMatchers(HttpMethod.POST, "/users/{id}/updateRole").hasAnyRole("COORDINATOR")

            .anyRequest().permitAll()
        )
        .headers(headers -> headers.frameOptions(frame -> frame.disable()));

    return http.build();
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

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}