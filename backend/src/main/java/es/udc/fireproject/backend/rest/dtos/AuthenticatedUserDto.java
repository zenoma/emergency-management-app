package es.udc.fireproject.backend.rest.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AuthenticatedUserDto extends BaseDto {

  private static final long serialVersionUID = 2434434469498232163L;

  private String serviceToken;
  private UserDto userDto;

  public AuthenticatedUserDto() {
  }

  public AuthenticatedUserDto(String serviceToken, UserDto userDto) {

    this.serviceToken = serviceToken;
    this.userDto = userDto;

  }
}
