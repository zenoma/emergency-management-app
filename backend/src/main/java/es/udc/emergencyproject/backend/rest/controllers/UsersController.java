package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.entities.user.UserRole;
import es.udc.emergencyproject.backend.model.exceptions.PermissionException;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.rest.config.JwtGenerator;
import es.udc.emergencyproject.backend.rest.config.JwtInfo;
import es.udc.emergencyproject.backend.rest.config.JwtUtils;
import es.udc.emergencyproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.emergencyproject.backend.rest.dtos.ChangePasswordParamsDto;
import es.udc.emergencyproject.backend.rest.dtos.LoginParamsDto;
import es.udc.emergencyproject.backend.rest.dtos.MobileDeviceRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.UserDto;
import es.udc.emergencyproject.backend.rest.dtos.UserRoleRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.UserSignUpRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.UserUpdateRequestDto;
import es.udc.emergencyproject.backend.rest.mappers.UserMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {


  private final PersonalManagementFacade personalManagementFacade;

  private final JwtGenerator jwtGenerator;


  @Override
  public ResponseEntity<List<UserDto>> getUsers() {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    List<UserDto> userDtos = new ArrayList<>();
    for (User user : personalManagementFacade.findAllUsers()) {
      userDtos.add(UserMapper.toUserDto(user));
    }

    return ResponseEntity.ok(userDtos);
  }

  @Override
  public ResponseEntity<AuthenticatedUserDto> postUsersSignUp(UserSignUpRequestDto userSignUpRequestDto) {

    final User user = personalManagementFacade.signUp(userSignUpRequestDto.getEmail(),
        userSignUpRequestDto.getPassword(),
        userSignUpRequestDto.getFirstName(),
        userSignUpRequestDto.getLastName(),
        userSignUpRequestDto.getPhoneNumber(),
        userSignUpRequestDto.getDni());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(user.getId()).toUri();

    return ResponseEntity.created(location)
        .body(UserMapper.toAuthenticatedUserDto(generateServiceToken(user), user));

  }

  @Override
  public ResponseEntity<AuthenticatedUserDto> postUsersLogin(LoginParamsDto params) {
    User user = personalManagementFacade.login(params.getUserName(), params.getPassword());

    return ResponseEntity.ok(UserMapper.toAuthenticatedUserDto(generateServiceToken(user), user));

  }

  @Override
  public ResponseEntity<AuthenticatedUserDto> postLoginFromServiceToken() {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    final Long userId = jwtInfo.get().userId();
    final String token = JwtUtils.getToken().orElse("");

    User dto = personalManagementFacade.loginFromId(userId);
    return ResponseEntity.ok(UserMapper.toAuthenticatedUserDto(token, dto));
  }

  @Override
  public ResponseEntity<UserDto> putUser(Long id, UserUpdateRequestDto userUpdateRequestDto) {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    final Long userId = jwtInfo.get().userId();

    if (!id.equals(userId)) {
      throw new PermissionException();
    }

    return ResponseEntity.ok(UserMapper.toUserDto(
        personalManagementFacade.updateProfile(id, userUpdateRequestDto.getFirstName(),
            userUpdateRequestDto.getLastName(),
            userUpdateRequestDto.getEmail(), Integer.valueOf(userUpdateRequestDto.getPhoneNumber()),
            userUpdateRequestDto.getDni())));

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

    personalManagementFacade.changePassword(id,
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
      userRole = UserRole.valueOf(userRoleRequestDto.getUserRole().toString());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("userRole");
    }
    personalManagementFacade.updateRole(userId, id, userRole);

    return ResponseEntity.noContent().build();

  }

  @Override
  public ResponseEntity<Void> postUserMobileDevice(Long id, MobileDeviceRequestDto mobileDeviceRequestDto) {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    if (!id.equals(jwtInfo.get().userId())) {
      throw new PermissionException();
    }

    personalManagementFacade.registerMobileDevice(id, mobileDeviceRequestDto.getFcmToken());
    return ResponseEntity.noContent().build();
  }

  private String generateServiceToken(User user) {

    JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getEmail(), user.getUserRole().name());

    return jwtGenerator.generate(jwtInfo);

  }

}
