package es.udc.emergencyproject.backend.rest.config;

public interface JwtGenerator {

  String generate(JwtInfo info);

  JwtInfo getInfo(String token);

}
