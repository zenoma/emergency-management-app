package es.udc.fireproject.backend.rest.common;

import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.ImageAlreadyUploadedException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.fireproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeUpdateStatusException;
import es.udc.fireproject.backend.model.exceptions.PermissionException;
import es.udc.fireproject.backend.model.exceptions.UserWithoutTeamException;
import es.udc.fireproject.backend.rest.dtos.ErrorDto;
import es.udc.fireproject.backend.rest.dtos.FieldErrorDto;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerExceptionHandler {

  private static final String GLOBAL_ERROR_EXCEPTION = "project.exceptions.GlobalErrorException";
  private static final String INSTANCE_NOT_FOUND_EXCEPTION_CODE = "project.exceptions.InstanceNotFoundException";
  private static final String USER_WITHOUT_TEAM_EXCEPTION_CODE = "project.exceptions.UserWithoutTeamException";
  private static final String BAD_REQUEST_EXCEPTION_CODE = "project.exceptions.BadRequestException";
  private static final String METHOD_ARGUMENT_NOT_VALID_EXCEPTION = "project.exceptions.MethodArgumentNotValidException";
  private static final String RESOURCE_NOT_FOUND_EXCEPTION_CODE = "project.exceptions.ResourceNotFoundException";
  private static final String DUPLICATE_INSTANCE_EXCEPTION_CODE = "project.exceptions.DuplicateInstanceException";
  private static final String
      PERMISSION_EXCEPTION_CODE = "project.exceptions.PermissionException";
  private static final String ILLEGAL_ARGUMENT_EXCEPTION_CODE = "project.exceptions.IllegalArgumentException";
  private static final String DATA_INTEGRITY_EXCEPTION_CODE = "project.exceptions.DataIntegrityViolationException";
  private static final String ALREADY_DISMANTLED_EXCEPTION_CODE = "project.exceptions.AlreadyDismantledException";
  private static final String ALREADY_EXIST_EXCEPTION_CODE = "project.exceptions.AlreadyExistException";
  private static final String EXTINGUISHED_FIRE_EXCEPTION_CODE = "project.exceptions.ExtinguishedFireException";

  private static final String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";
  private static final String INCORRECT_PASSWORD_EXCEPTION_CODE = "project.exceptions.IncorrectPasswordException";
  private static final String INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE = "project.exceptions.InsufficientRolePermissionException";
  private static final String IMAGE_ALREADY_UPLOADED_EXCEPTION_CODE = "project.exceptions.ImageAlreadyUploadedException";
  private static final String NOTICE_CHECK_STATUS_EXCEPTION_CODE = "project.exceptions.NoticeCheckStatusException";
  private static final String NOTICE_UPDATE_STATUS_EXCEPTION_CODE = "project.exceptions.NoticeUpdateStatusException";
  private static final String NOTICE_DELETE_STATUS_EXCEPTION_CODE = "project.exceptions.NoticeDeleteStatusException";

  private final MessageSource messageSource;

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDto handleUnhandledException(Exception exception, Locale locale) {

    String errorMessage = messageSource.getMessage(GLOBAL_ERROR_EXCEPTION, null, null, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getMessage(), null, null, null);
    String errorMessage = messageSource.getMessage(METHOD_ARGUMENT_NOT_VALID_EXCEPTION,
        new Object[]{nameMessage, exception.getBody()}, METHOD_ARGUMENT_NOT_VALID_EXCEPTION, locale);

    List<FieldErrorDto> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage())).collect(Collectors.toList());

    ErrorDto errorDto = new ErrorDto(errorMessage);
    errorDto.setFieldErrors(fieldErrors);
    return errorDto;

  }

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleNoResourceFoundException(NoResourceFoundException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getMessage(), null, null, null);
    String errorMessage = messageSource.getMessage(RESOURCE_NOT_FOUND_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getBody()}, RESOURCE_NOT_FOUND_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }


  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleNoResourceFoundException(BadRequestException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getMessage(), null, null, null);
    String errorMessage = messageSource.getMessage(BAD_REQUEST_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getMessage()}, BAD_REQUEST_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(InstanceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorDto handleInstanceNotFoundException(InstanceNotFoundException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
    String errorMessage = messageSource.getMessage(INSTANCE_NOT_FOUND_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getKey().toString()}, INSTANCE_NOT_FOUND_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(UserWithoutTeamException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorDto handleUserWithoutTeamException(UserWithoutTeamException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
    String errorMessage = messageSource.getMessage(USER_WITHOUT_TEAM_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getKey().toString()}, USER_WITHOUT_TEAM_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleIllegalArgumentException(IllegalArgumentException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(ILLEGAL_ARGUMENT_EXCEPTION_CODE, null,
        ILLEGAL_ARGUMENT_EXCEPTION_CODE,
        locale);

    return new ErrorDto(errorMessage + ": " + exception.getMessage());

  }

  @ExceptionHandler(DuplicateInstanceException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleDuplicateInstanceException(DuplicateInstanceException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
    String errorMessage = messageSource.getMessage(DUPLICATE_INSTANCE_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getKey().toString()}, DUPLICATE_INSTANCE_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(PermissionException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ErrorDto handlePermissionException(PermissionException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(PERMISSION_EXCEPTION_CODE, null, PERMISSION_EXCEPTION_CODE,
        locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleDataIntegrityViolationException(DataIntegrityViolationException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(DATA_INTEGRITY_EXCEPTION_CODE, null,
        DATA_INTEGRITY_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage + "\n" + exception.getMessage());

  }

  @ExceptionHandler(AlreadyDismantledException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleAlreadyDismantledException(AlreadyDismantledException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(ALREADY_DISMANTLED_EXCEPTION_CODE,
        new Object[]{exception.getName(), exception.getId()}, ALREADY_DISMANTLED_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(AlreadyExistException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleAlreadyExistException(AlreadyExistException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(ALREADY_EXIST_EXCEPTION_CODE,
        new Object[]{exception.getName(), exception.getId()}, ALREADY_EXIST_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(ExtinguishedFireException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleExtinguishedFireException(ExtinguishedFireException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(EXTINGUISHED_FIRE_EXCEPTION_CODE,
        new Object[]{exception.getId()}, EXTINGUISHED_FIRE_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(IncorrectLoginException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDto handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {
    String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
        INCORRECT_LOGIN_EXCEPTION_CODE, locale);
    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(IncorrectPasswordException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDto handleIncorrectPasswordException(IncorrectPasswordException exception, Locale locale) {
    String errorMessage = messageSource.getMessage(INCORRECT_PASSWORD_EXCEPTION_CODE, null,
        INCORRECT_PASSWORD_EXCEPTION_CODE, locale);
    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(InsufficientRolePermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorDto handleInsufficientRolePermissionException(InsufficientRolePermissionException exception,
      Locale locale) {
    String errorMessage = messageSource.getMessage(INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE, null,
        INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE, locale);
    return new ErrorDto(errorMessage);
  }


  @ExceptionHandler(ImageAlreadyUploadedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleImageAlreadyUploadedException(ImageAlreadyUploadedException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(IMAGE_ALREADY_UPLOADED_EXCEPTION_CODE,
        new Object[]{exception.getId()}, IMAGE_ALREADY_UPLOADED_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(NoticeCheckStatusException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleNoticeCheckStatusException(NoticeCheckStatusException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(NOTICE_CHECK_STATUS_EXCEPTION_CODE,
        new Object[]{exception.getId(), exception.getStatus()}, NOTICE_CHECK_STATUS_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(NoticeUpdateStatusException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleNoticeUpdateStatusException(NoticeUpdateStatusException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(NOTICE_UPDATE_STATUS_EXCEPTION_CODE,
        new Object[]{exception.getId(), exception.getStatus()}, NOTICE_UPDATE_STATUS_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(NoticeDeleteStatusException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleNoticeDeleteStatusException(NoticeDeleteStatusException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(NOTICE_DELETE_STATUS_EXCEPTION_CODE,
        new Object[]{exception.getId(), exception.getStatus()}, NOTICE_DELETE_STATUS_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

}
