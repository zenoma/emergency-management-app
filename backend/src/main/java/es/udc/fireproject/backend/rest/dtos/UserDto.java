package es.udc.fireproject.backend.rest.dtos;

import es.udc.fireproject.backend.model.entities.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class, UpdateValidations.class})
  @Email(groups = {AllValidations.class, UpdateValidations.class})
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email.trim();
  }

  @NotNull(groups = {AllValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class})
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class, UpdateValidations.class})
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName.trim();
  }

  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 1, max = 60, groups = {AllValidations.class, UpdateValidations.class})
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName.trim();
  }

  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
  @Size(min = 9, max = 9, groups = {AllValidations.class, UpdateValidations.class})
  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  @NotNull(groups = {AllValidations.class, UpdateValidations.class})
//    @Size(min = 9, max = 9, groups = {AllValidations.class, UpdateValidations.class})
  public Integer getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(Integer phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public Long getTeamId() {
    return teamId;
  }

  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDto userDto = (UserDto) o;
    return Objects.equals(id, userDto.id) && Objects.equals(email, userDto.email) && Objects.equals(password,
        userDto.password) && Objects.equals(firstName, userDto.firstName) && Objects.equals(lastName, userDto.lastName)
        && Objects.equals(dni, userDto.dni) && Objects.equals(phoneNumber, userDto.phoneNumber)
        && userRole == userDto.userRole;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, password, firstName, lastName, dni, phoneNumber, userRole);
  }

  @Override
  public String toString() {
    return "UserDto{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", dni='" + dni + '\'' +
        ", phoneNumber=" + phoneNumber +
        ", userRole=" + userRole +
        ", teamId=" + teamId +
        '}';
  }

  public interface AllValidations {

  }

  public interface UpdateValidations {

  }

}
