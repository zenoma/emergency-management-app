package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.fireproject.backend.model.exceptions.PermissionException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.common.ErrorsDto;
import es.udc.fireproject.backend.rest.config.JwtGenerator;
import es.udc.fireproject.backend.rest.config.JwtInfo;
import es.udc.fireproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.fireproject.backend.rest.dtos.ChangePasswordParamsDto;
import es.udc.fireproject.backend.rest.dtos.LoginParamsDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;
import es.udc.fireproject.backend.rest.dtos.UserRoleDto;
import es.udc.fireproject.backend.rest.dtos.conversors.UserConversor;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/users")
public class UserController {

  private static final String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";
  private static final String INCORRECT_PASSWORD_EXCEPTION_CODE = "project.exceptions.IncorrectPasswordException";
  private static final String INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE = "project.exceptions.InsufficientRolePermissionException";

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private JwtGenerator jwtGenerator;

  @Autowired
  private PersonalManagementService personalManagementService;

  @ExceptionHandler(IncorrectLoginException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorsDto handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
        INCORRECT_LOGIN_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage);

  }

  @ExceptionHandler(IncorrectPasswordException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorsDto handleIncorrectPasswordException(IncorrectPasswordException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(INCORRECT_PASSWORD_EXCEPTION_CODE, null,
        INCORRECT_PASSWORD_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage);

  }


  @ExceptionHandler(InsufficientRolePermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public ErrorsDto handleInsufficientRolePermissionException(InsufficientRolePermissionException exception,
      Locale locale) {

    String errorMessage = messageSource.getMessage(INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE, null,
        INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage);

  }

  @GetMapping("/")
  public List<UserDto> findAll(@RequestAttribute Long userId) throws InstanceNotFoundException {

    List<UserDto> userDtos = new ArrayList<>();
    for (User user : personalManagementService.findAllUsers()) {
      userDtos.add(UserConversor.toUserDto(user));
    }

    return userDtos;
  }

  @PostMapping("/signUp")
  public ResponseEntity<AuthenticatedUserDto> signUp(
      @Validated({UserDto.AllValidations.class}) @RequestBody UserDto userDto)
      throws DuplicateInstanceException, InstanceNotFoundException {

    User user = UserConversor.toUser(userDto);

    personalManagementService.signUp(user);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(user.getId()).toUri();

    return ResponseEntity.created(location)
        .body(UserConversor.toAuthenticatedUserDto(generateServiceToken(user), user));

  }

  @PostMapping("/login")
  public AuthenticatedUserDto login(@Validated @RequestBody LoginParamsDto params)
      throws IncorrectLoginException {

    User user = personalManagementService.login(params.getUserName(), params.getPassword());

    return UserConversor.toAuthenticatedUserDto(generateServiceToken(user), user);

  }

  @PostMapping("/loginFromServiceToken")
  public AuthenticatedUserDto loginFromServiceToken(@RequestAttribute Long userId,
      @RequestAttribute String serviceToken) throws InstanceNotFoundException {

    User user = personalManagementService.loginFromId(userId);

    return UserConversor.toAuthenticatedUserDto(serviceToken, user);

  }

  @PutMapping("/{id}")
  public UserDto updateProfile(@RequestAttribute Long userId, @PathVariable Long id,
      @Validated({UserDto.UpdateValidations.class}) @RequestBody UserDto userDto)
      throws InstanceNotFoundException, PermissionException {

    if (!id.equals(userId)) {
      throw new PermissionException();
    }

    return UserConversor.toUserDto(
        personalManagementService.updateProfile(id, userDto.getFirstName(), userDto.getLastName(),
            userDto.getEmail(), userDto.getPhoneNumber(), userDto.getDni()));

  }

  @PostMapping("/{id}/changePassword")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changePassword(@RequestAttribute Long userId, @PathVariable Long id,
      @Validated @RequestBody ChangePasswordParamsDto params)
      throws PermissionException, InstanceNotFoundException, IncorrectPasswordException {

    if (!id.equals(userId)) {
      throw new PermissionException();
    }

    personalManagementService.changePassword(id, params.getOldPassword(), params.getNewPassword());

  }

  @PostMapping("/{id}/updateRole")
  public void updateRole(@RequestAttribute Long userId, @PathVariable Long id,
      @Validated @RequestBody UserRoleDto userRoleDto)
      throws InsufficientRolePermissionException, InstanceNotFoundException {

    UserRole userRole;
    try {
      userRole = UserRole.valueOf(userRoleDto.getUserRole().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("userRole");
    }
    personalManagementService.updateRole(userId, id, userRole);

  }

  private String generateServiceToken(User user) {

    JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getEmail(), user.getUserRole().name());

    return jwtGenerator.generate(jwtInfo);

  }


}
