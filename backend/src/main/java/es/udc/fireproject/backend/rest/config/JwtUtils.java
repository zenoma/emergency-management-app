package es.udc.fireproject.backend.rest.config;

import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class JwtUtils {

  public static Optional<String> getToken() {
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs != null) {
      Object token = attrs.getRequest().getAttribute("serviceToken");
      if (token instanceof String s) {
        return Optional.of(s);
      }
    }
    return Optional.empty();
  }

  public static Optional<JwtInfo> getJwtInfo() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof JwtInfo jwtInfo) {
      return Optional.of(jwtInfo);
    }
    return Optional.empty();
  }

  public static Long getUserId() {
    return getJwtInfo()
        .map(JwtInfo::userId)
        .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
  }


  public static Optional<String> getUserName() {
    return getJwtInfo().map(JwtInfo::userName);
  }


  public static Optional<String> getRole() {
    return getJwtInfo().map(JwtInfo::role);
  }
}
