package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;

public class UserConversor {


  private UserConversor() {
  }

  public static UserDto toUserDto(User user) {
    Long teamId = null;

    if (user.getTeam() != null) {
      teamId = user.getTeam().getId();
    }

    return new UserDto();
  }

  public static User toUser(UserDto userDto) {
    return new User();
  }

  public static AuthenticatedUserDto toAuthenticatedUserDto(String serviceToken, User user) {

    return new AuthenticatedUserDto(serviceToken, toUserDto(user));

  }

}
