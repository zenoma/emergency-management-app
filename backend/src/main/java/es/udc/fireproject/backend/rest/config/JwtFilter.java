package es.udc.fireproject.backend.rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.fireproject.backend.rest.dtos.ErrorDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtFilter extends BasicAuthenticationFilter {

  private final JwtGenerator jwtGenerator;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public JwtFilter(AuthenticationManager authenticationManager, JwtGenerator jwtGenerator) {

    super(authenticationManager);

    this.jwtGenerator = jwtGenerator;

  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeaderValue == null || !authHeaderValue.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {

      String serviceToken = authHeaderValue.replace("Bearer ", "");

      JwtInfo jwtInfo = jwtGenerator.getInfo(serviceToken);
      request.setAttribute("serviceToken", serviceToken);
      request.setAttribute("userId", jwtInfo.userId());

      configureSecurityContext(jwtInfo);

    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      ErrorDto errorDto = new ErrorDto("Invalid or expired authentication token.");
      objectMapper.writeValue(response.getOutputStream(), errorDto);
      return;
    }

    filterChain.doFilter(request, response);

  }

  private void configureSecurityContext(JwtInfo jwtInfo) {

    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + jwtInfo.role()));

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(jwtInfo, null, authorities));
  }


}
