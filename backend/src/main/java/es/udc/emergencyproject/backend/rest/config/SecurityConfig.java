package es.udc.emergencyproject.backend.rest.config;

import static org.springframework.security.config.Customizer.withDefaults;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


  private static final String COORDINATOR_ROLE = "COORDINATOR";
  private static final String MANAGER_ROLE = "MANAGER";
  private static final String USER_ROLE = "USER";

  private final JwtGenerator jwtGenerator;

  @Value("${app.cors.allowed-origins:}")
  private String allowedOriginsProperty;


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
      throws Exception {
    http
        .cors(withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilter(new JwtFilter(authenticationManager, jwtGenerator))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/emergencies").permitAll()
            .requestMatchers(HttpMethod.GET, "/emergencies/types").permitAll()
            .requestMatchers(HttpMethod.POST, "/emergencies").hasAnyRole(COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/emergencies/{id}").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/emergencies/{id}/resolveEmergency").hasAnyRole(COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/emergencies/{id}/removeQuadrant")
            .hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.PUT, "/emergencies/{id}").hasAnyRole(COORDINATOR_ROLE)

            .requestMatchers(HttpMethod.GET, "/logs/emergencies").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/logs/emergencies/{id}").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/logs/teams").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/logs/vehicles").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/logs/statistics/{id}").permitAll()

            .requestMatchers(HttpMethod.POST, "/notices").permitAll()
            .requestMatchers(HttpMethod.GET, "/notices/{id}").permitAll()
            .requestMatchers(HttpMethod.GET, "/notices").permitAll()
            .requestMatchers(HttpMethod.PUT, "/notices/{id}").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/notices/{id}").hasAnyRole(COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.PUT, "/notices/{id}/status").hasAnyRole(COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/notices/{id}/images").permitAll()
            .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
            // Allow access to OpenAPI / Swagger UI (public)
            .requestMatchers(
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-ui/index.html",
                "/swagger-ui/swagger-config",
                "/webjars/**"
            ).permitAll()

            .requestMatchers(HttpMethod.GET, "/organizations").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/organizations/{id}")
            .hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.DELETE, "/organizations/{id}").hasAnyRole(COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/organizations").hasAnyRole(COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.PUT, "/organizations/{id}").hasAnyRole(COORDINATOR_ROLE)

            .requestMatchers(HttpMethod.GET, "/organizationTypes").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/organizationTypes/{id}")
            .hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/organizationTypes").hasAnyRole(COORDINATOR_ROLE)

            .requestMatchers(HttpMethod.GET, "/quadrants").permitAll()
            .requestMatchers(HttpMethod.GET, "/quadrants/{gid}").permitAll()
            .requestMatchers(HttpMethod.GET, "/quadrants/active").permitAll()
            .requestMatchers(HttpMethod.GET, "/quadrants/findByCoordinates").permitAll()
            .requestMatchers(HttpMethod.POST, "/quadrants/{gid}/linkEmergency")
            .hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)

            .requestMatchers(HttpMethod.POST, "/teams").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/teams").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/teams/active").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/teams/{id}").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/teams/myTeam").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/teams/{id}/addUser").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.DELETE, "/teams/{id}").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/teams/{id}/deleteUser").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/teams/{id}/users").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.PUT, "/teams/{id}").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/teams/{id}/deploy").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/teams/{id}/retract").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)

            .requestMatchers(HttpMethod.POST, "/vehicles").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.DELETE, "/vehicles/{id}").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.PUT, "/vehicles/{id}").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/vehicles/{id}").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/vehicles").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.GET, "/vehicles/active").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/vehicles/{id}/deploy").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/vehicles/{id}/retract").hasAnyRole(MANAGER_ROLE, COORDINATOR_ROLE)

            .requestMatchers(HttpMethod.GET, "/users/").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/users/signUp").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/loginFromServiceToken").permitAll()
            .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/users/{id}/changePassword")
            .hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/users/{id}/updateRole").hasAnyRole(COORDINATOR_ROLE)
            .requestMatchers(HttpMethod.POST, "/users/{id}/mobileDevice")
            .hasAnyRole(USER_ROLE, MANAGER_ROLE, COORDINATOR_ROLE)

            .anyRequest().authenticated()
        )
        .headers(headers -> headers.frameOptions(frame -> frame.disable()));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    config.setAllowCredentials(true);
    if (allowedOriginsProperty == null || allowedOriginsProperty.isBlank()) {
      config.addAllowedOriginPattern("*");
    } else {
      String[] origins = allowedOriginsProperty.split(",");
      for (String origin : origins) {
        String o = origin.trim();
        if (!o.isEmpty()) {
          config.addAllowedOrigin(o);
        }
      }
    }
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
