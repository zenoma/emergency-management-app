package es.udc.fireproject.backend.rest.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserRoleDto extends BaseDto {

  private static final long serialVersionUID = 5979000421332929762L;

  private String userRole;

  UserRoleDto() {

  }

}
