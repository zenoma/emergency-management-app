package es.udc.fireproject.backend.rest.config;

public record JwtInfo(

    Long userId,
    String userName,
    String role

) {

}
