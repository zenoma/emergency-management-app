package es.udc.fireproject.backend.rest.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class LoginParamsDto extends BaseDto {

  private static final long serialVersionUID = 8764926106493209546L;

  private String userName;
  private String password;

  public LoginParamsDto() {
  }

  @NotNull
  public String getUserName() {
    return userName;
  }


  @NotNull
  public String getPassword() {
    return password;
  }


}
