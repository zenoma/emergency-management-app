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

        return new UserDto(user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDni(),
                user.getPhoneNumber(),
                user.getUserRole(),
                teamId);
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getEmail(),
                userDto.getPassword(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getDni(),
                userDto.getPhoneNumber());
    }

    public static AuthenticatedUserDto toAuthenticatedUserDto(String serviceToken, User user) {

        return new AuthenticatedUserDto(serviceToken, toUserDto(user));

    }

}
