package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    public String getServiceToken() {
        return serviceToken;
    }

    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    @JsonProperty("user")
    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticatedUserDto that = (AuthenticatedUserDto) o;
        return Objects.equals(serviceToken, that.serviceToken) && Objects.equals(userDto, that.userDto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceToken, userDto);
    }

    @Override
    public String toString() {
        return "AuthenticatedUserDto{" +
                "serviceToken='" + serviceToken + '\'' +
                ", userDto=" + userDto +
                '}';
    }
}
