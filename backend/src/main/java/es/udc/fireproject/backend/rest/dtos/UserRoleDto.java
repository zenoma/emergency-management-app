package es.udc.fireproject.backend.rest.dtos;

import java.util.Objects;

public class UserRoleDto extends BaseDto {

  private static final long serialVersionUID = 5979000421332929762L;

  private String userRole;

  UserRoleDto() {

  }


  public String getUserRole() {
    return userRole;
  }

  public void setUserRole(String userRole) {
    this.userRole = userRole;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserRoleDto that = (UserRoleDto) o;
    return Objects.equals(userRole, that.userRole);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userRole);
  }

  @Override
  public String toString() {
    return "UserRoleDto{" +
        "userRole='" + userRole + '\'' +
        '}';
  }
}
