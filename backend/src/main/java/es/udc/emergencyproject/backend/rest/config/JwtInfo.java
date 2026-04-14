package es.udc.emergencyproject.backend.rest.config;

public record JwtInfo(

    Long userId,
    String userName,
    String role

) {

}
