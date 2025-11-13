package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.exceptions.PermissionException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.config.JwtGenerator;
import es.udc.fireproject.backend.rest.config.JwtInfo;
import es.udc.fireproject.backend.rest.config.JwtUtils;
import es.udc.fireproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.fireproject.backend.rest.dtos.ChangePasswordParamsDto;
import es.udc.fireproject.backend.rest.dtos.LoginParamsDto;
import es.udc.fireproject.backend.rest.dtos.RequestUserDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;
import es.udc.fireproject.backend.rest.dtos.UserRoleRequestDto;
import es.udc.fireproject.backend.rest.dtos.conversors.UserConversor;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {


  private final PersonalManagementService personalManagementService;

  private final JwtGenerator jwtGenerator;


  @Override
  public ResponseEntity<List<UserDto>> getUsers() {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    List<UserDto> userDtos = new ArrayList<>();
    for (User user : personalManagementService.findAllUsers()) {
      userDtos.add(UserConversor.toUserDto(user));
    }

    return ResponseEntity.ok(userDtos);
  }

  @Override
  public ResponseEntity<AuthenticatedUserDto> postUsersSignUp(RequestUserDto userDto) {

    final User user = personalManagementService.signUp(userDto.getEmail(),
        userDto.getPassword(),
        userDto.getFirstName(),
        userDto.getLastName(),
        userDto.getPhoneNumber(),
        userDto.getDni());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(user.getId()).toUri();

    return ResponseEntity.created(location)
        .body(UserConversor.toAuthenticatedUserDto(generateServiceToken(user), user));

  }

  @Override
  public ResponseEntity<AuthenticatedUserDto> postUsersLogin(LoginParamsDto params) {
    User user = personalManagementService.login(params.getUserName(), params.getPassword());

    return ResponseEntity.ok(UserConversor.toAuthenticatedUserDto(generateServiceToken(user), user));

  }

  @Override
  public ResponseEntity<AuthenticatedUserDto> postLoginFromServiceToken() {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    final Long userId = jwtInfo.get().userId();
    final String token = JwtUtils.getToken().orElse("");

    User dto = personalManagementService.loginFromId(userId);
    return ResponseEntity.ok(UserConversor.toAuthenticatedUserDto(token, dto));
  }

  @Override
  public ResponseEntity<UserDto> putUser(Long id, UserDto userDto) {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    final Long userId = jwtInfo.get().userId();

    if (!id.equals(userId)) {
      throw new PermissionException();
    }

    return ResponseEntity.ok(UserConversor.toUserDto(
        personalManagementService.updateProfile(id, userDto.getFirstName(), userDto.getLastName(),
            userDto.getEmail(), Integer.valueOf(userDto.getPhoneNumber()), userDto.getDni())));

  }

  @Override
  public ResponseEntity<Void> postChangePassword(Long id, ChangePasswordParamsDto changePasswordParamsDto) {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    final Long userId = jwtInfo.get().userId();

    if (!id.equals(userId)) {
      throw new PermissionException();
    }

    personalManagementService.changePassword(id,
        changePasswordParamsDto.getOldPassword(),
        changePasswordParamsDto.getNewPassword());

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> postUpdateUserRole(Long id, UserRoleRequestDto userRoleRequestDto) {

    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    final Long userId = jwtInfo.get().userId();

    UserRole userRole;
    try {
      userRole = UserRole.valueOf(userRoleRequestDto.getUserRole().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("userRole");
    }
    personalManagementService.updateRole(userId, id, userRole);

    return ResponseEntity.noContent().build();

  }

  private String generateServiceToken(User user) {

    JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getEmail(), user.getUserRole().name());

    return jwtGenerator.generate(jwtInfo);

  }

}
