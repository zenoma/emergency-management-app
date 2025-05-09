package es.udc.fireproject.backend.rest.dtos;

import javax.validation.constraints.NotNull;
import java.util.Objects;

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

    public void setUserName(String userName) {
        this.userName = userName.trim();
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginParamsDto that = (LoginParamsDto) o;
        return Objects.equals(userName, that.userName) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }

    @Override
    public String toString() {
        return "LoginParamsDto{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
