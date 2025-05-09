package es.udc.fireproject.backend.rest.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtGeneratorImpl implements JwtGenerator {

  @Value("${project.jwt.signKey}")
  private String signKey;

  @Value("${project.jwt.expirationMinutes}")
  private long expirationMinutes;


  @Override
  public String generate(JwtInfo info) {
    return Jwts.builder()
        .claim("userId", info.getUserId())
        .claim("role", info.getRole())
        .setSubject(info.getUserName())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000))
        .signWith(Keys.hmacShaKeyFor(signKey.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  @Override
  public JwtInfo getInfo(String token) {

    SecretKey key = Keys.hmacShaKeyFor(signKey.getBytes(StandardCharsets.UTF_8));

    Claims claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Number userIdNumber = (Number) claims.get("userId");

    return new JwtInfo(
        userIdNumber.longValue(),
        claims.getSubject(),
        (String) claims.get("role"));
  }
}
