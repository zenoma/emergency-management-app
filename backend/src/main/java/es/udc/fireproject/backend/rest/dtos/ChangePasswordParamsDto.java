package es.udc.fireproject.backend.rest.dtos;

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
public class ChangePasswordParamsDto extends BaseDto {

  private static final long serialVersionUID = 1803837440286939975L;

  private String oldPassword;
  private String newPassword;

  public ChangePasswordParamsDto() {
  }

  @NotNull
  public String getOldPassword() {
    return oldPassword;
  }


  @NotNull
  @Size(min = 8, message
      = "Password must contain at least 8 characters")
  public String getNewPassword() {
    return newPassword;
  }


}
