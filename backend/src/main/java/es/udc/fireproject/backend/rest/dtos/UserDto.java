package es.udc.fireproject.backend.rest.dtos;

import es.udc.fireproject.backend.model.entities.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserDto extends BaseDto {

  private static final long serialVersionUID = -2717277403627016569L;

  private Long id;
  private String email;
  private String password;
  private String firstName;
  private String lastName;
  private String dni;
  private Integer phoneNumber;
  private UserRole userRole;
  private Long teamId;


  public UserDto() {
  }

  public UserDto(Long id, String email, String password, String firstName, String lastName, String dni,
      Integer phoneNumber, UserRole userRole) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dni = dni;
    this.phoneNumber = phoneNumber;
    this.userRole = userRole;
  }

  public UserDto(Long id, String email, String firstName, String lastName, String dni, Integer phoneNumber,
      UserRole userRole, Long teamId) {
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dni = dni;
    this.phoneNumber = phoneNumber;
    this.userRole = userRole;
    this.teamId = teamId;
  }


  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class, UpdateValidations.class})
  @Email(groups = {AllValidations.class, UpdateValidations.class})
  public String getEmail() {
    return email;
  }

  @NotNull(groups = {AllValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class})
  public String getPassword() {
    return password;
  }

  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class, UpdateValidations.class})
  public String getFirstName() {
    return firstName;
  }

  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class, UpdateValidations.class})
  public String getLastName() {
    return lastName;
  }


  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 9, max = 9, groups = {AllValidations.class, UpdateValidations.class})
  public String getDni() {
    return dni;
  }


  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
//    @Size(min = 9, max = 9, groups = {AllValidations.class, UpdateValidations.class})
  public Integer getPhoneNumber() {
    return phoneNumber;
  }

  public interface AllValidations {

  }

  public interface UpdateValidations {

  }

}
