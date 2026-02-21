package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;

public class UserMapper {


  private UserMapper() {
  }

  public static UserDto toUserDto(User user) {

    UserDto dto = new UserDto(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getPhoneNumber().toString(),
        user.getDni(),
        user.getUserRole().toString()
    );

    if (user.getTeam() != null) {
      dto.setTeamId(user.getTeam().getId());
      dto.setTeamCode(user.getTeam().getCode());
    }

    return dto;
  }


  public static AuthenticatedUserDto toAuthenticatedUserDto(String serviceToken, User user) {

    return new AuthenticatedUserDto(serviceToken, toUserDto(user));

  }

}
