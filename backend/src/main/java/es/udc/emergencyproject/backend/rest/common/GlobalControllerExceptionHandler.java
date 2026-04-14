package es.udc.emergencyproject.backend.rest.common;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyExistException;
import es.udc.emergencyproject.backend.model.exceptions.DomainException;
import es.udc.emergencyproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.emergencyproject.backend.model.exceptions.EmergencyAlreadyLinkedToPointException;
import es.udc.emergencyproject.backend.model.exceptions.EmergencyAlreadyLinkedToQuadrantsException;
import es.udc.emergencyproject.backend.model.exceptions.ExtinguishedEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.FileUploadException;
import es.udc.emergencyproject.backend.model.exceptions.ImageAlreadyUploadedException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeUpdateStatusException;
import es.udc.emergencyproject.backend.model.exceptions.PermissionException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantAlreadyLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantNotLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.UserWithoutTeamException;
import es.udc.emergencyproject.backend.rest.dtos.ErrorDto;
import es.udc.emergencyproject.backend.rest.dtos.FieldErrorDto;
import es.udc.emergencyproject.backend.rest.exceptions.ImageRequiredException;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
  private static final String EXTINGUISHED_EMERGENCY_EXCEPTION_CODE = "project.exceptions.ExtinguishedEmergencyException";

  private static final String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";
  private static final String INCORRECT_PASSWORD_EXCEPTION_CODE = "project.exceptions.IncorrectPasswordException";
  private static final String INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE = "project.exceptions.InsufficientRolePermissionException";
  private static final String IMAGE_ALREADY_UPLOADED_EXCEPTION_CODE = "project.exceptions.ImageAlreadyUploadedException";
  private static final String NOTICE_CHECK_STATUS_EXCEPTION_CODE = "project.exceptions.NoticeCheckStatusException";
  private static final String NOTICE_UPDATE_STATUS_EXCEPTION_CODE = "project.exceptions.NoticeUpdateStatusException";
  private static final String NOTICE_DELETE_STATUS_EXCEPTION_CODE = "project.exceptions.NoticeDeleteStatusException";
  private static final String CONSTRAINT_VIOLATION_EXCEPTION_CODE = "project.exceptions.ConstraintViolationException";
  private static final String IMAGE_REQUIRED_EXCEPTION_CODE = "project.exceptions.ImageRequiredException";
  private static final String DOMAIN_EXCEPTION_CODE = "project.exceptions.DomainException";
  private static final String FILE_UPLOAD_EXCEPTION_CODE = "project.exceptions.FileUploadException";
  private static final String QUADRANT_NOT_LINKED_TO_EMERGENCY_EXCEPTION_CODE = "project.exceptions.QuadrantNotLinkedToEmergencyException";
  private static final String QUADRANT_ALREADY_LINKED_TO_EMERGENCY_EXCEPTION_CODE = "project.exceptions.QuadrantAlreadyLinkedToEmergencyException";

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

    String nameMessage = messageSource.getMessage(exception.getMessage(), null, exception.getMessage(), locale);
    String errorMessage = messageSource.getMessage(METHOD_ARGUMENT_NOT_VALID_EXCEPTION,
        new Object[]{nameMessage, exception.getBody()}, METHOD_ARGUMENT_NOT_VALID_EXCEPTION, locale);

    List<FieldErrorDto> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage())).collect(Collectors.toList());

    ErrorDto errorDto = new ErrorDto(errorMessage);
    errorDto.setFieldErrors(fieldErrors);
    return errorDto;

  }

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorDto handleNoResourceFoundException(NoResourceFoundException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getMessage(), null, null, null);
    String errorMessage = messageSource.getMessage(RESOURCE_NOT_FOUND_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getBody()}, RESOURCE_NOT_FOUND_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);

  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex,
      Locale locale) {

    Throwable cause = ex.getCause();

    if (cause instanceof ValueInstantiationException vie) {

      Throwable rootCause = vie.getCause();
      String detail = rootCause != null
          ? rootCause.getMessage()
          : vie.getMessage();

      String errorMessage = messageSource.getMessage(
          "error.enum.invalid",
          new Object[]{detail},
          detail,
          locale
      );

      return new ErrorDto(errorMessage);
    }

    // Fallback genérico
    String errorMessage = messageSource.getMessage(
        BAD_REQUEST_EXCEPTION_CODE,
        null,
        BAD_REQUEST_EXCEPTION_CODE,
        locale
    );

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

    return new ErrorDto(errorMessage);

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
  @ResponseStatus(HttpStatus.FORBIDDEN)
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

    return new ErrorDto(errorMessage);

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

  @ExceptionHandler(ExtinguishedEmergencyException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleExtinguishedEmergencyException(ExtinguishedEmergencyException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(EXTINGUISHED_EMERGENCY_EXCEPTION_CODE,
        new Object[]{exception.getId()}, EXTINGUISHED_EMERGENCY_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(EmergencyAlreadyLinkedToQuadrantsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleEmergencyAlreadyLinkedToQuadrantsException(
      EmergencyAlreadyLinkedToQuadrantsException exception,
      Locale locale) {

    String errorMessage = messageSource.getMessage("project.exceptions.EmergencyAlreadyLinkedToQuadrantsException",
        new Object[]{exception.getName(), exception.getId()},
        "project.exceptions.EmergencyAlreadyLinkedToQuadrantsException",
        locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(EmergencyAlreadyLinkedToPointException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleEmergencyAlreadyLinkedToPointException(
      EmergencyAlreadyLinkedToPointException exception,
      Locale locale) {

    String errorMessage = messageSource.getMessage("project.exceptions.EmergencyAlreadyLinkedToPointException",
        new Object[]{exception.getName(), exception.getId()},
        "project.exceptions.EmergencyAlreadyLinkedToPointException",
        locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(IncorrectLoginException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ErrorDto handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {
    String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
        INCORRECT_LOGIN_EXCEPTION_CODE, locale);
    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(IncorrectPasswordException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ErrorDto handleIncorrectPasswordException(IncorrectPasswordException exception, Locale locale) {
    String errorMessage = messageSource.getMessage(INCORRECT_PASSWORD_EXCEPTION_CODE, null,
        INCORRECT_PASSWORD_EXCEPTION_CODE, locale);
    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(InsufficientRolePermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
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

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleConstraintViolationException(ConstraintViolationException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(CONSTRAINT_VIOLATION_EXCEPTION_CODE, null,
        CONSTRAINT_VIOLATION_EXCEPTION_CODE, locale);

    List<FieldErrorDto> fieldErrors = exception.getConstraintViolations().stream()
        .map(violation -> new FieldErrorDto(violation.getPropertyPath().toString(), violation.getMessage()))
        .collect(Collectors.toList());

    ErrorDto errorDto = new ErrorDto(errorMessage);
    errorDto.setFieldErrors(fieldErrors);
    return errorDto;
  }

  @ExceptionHandler(ImageRequiredException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleImageRequiredException(ImageRequiredException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(IMAGE_REQUIRED_EXCEPTION_CODE, null,
        IMAGE_REQUIRED_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(DomainException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleDomainException(DomainException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(DOMAIN_EXCEPTION_CODE,
        new Object[]{exception.getName(), exception.getId()}, DOMAIN_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(QuadrantNotLinkedToEmergencyException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleQuadrantNotLinkedToEmergencyException(QuadrantNotLinkedToEmergencyException exception,
      Locale locale) {

    String errorMessage = messageSource.getMessage(QUADRANT_NOT_LINKED_TO_EMERGENCY_EXCEPTION_CODE,
        new Object[]{exception.getQuadrantId(), exception.getEmergencyId()},
        QUADRANT_NOT_LINKED_TO_EMERGENCY_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(QuadrantAlreadyLinkedToEmergencyException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDto handleQuadrantAlreadyLinkedToEmergencyException(
      QuadrantAlreadyLinkedToEmergencyException exception,
      Locale locale) {

    String errorMessage = messageSource.getMessage(QUADRANT_ALREADY_LINKED_TO_EMERGENCY_EXCEPTION_CODE,
        new Object[]{exception.getEmergencyId(), exception.getQuadrantId()},
        QUADRANT_ALREADY_LINKED_TO_EMERGENCY_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

  @ExceptionHandler(FileUploadException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDto handleFileUploadException(FileUploadException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(FILE_UPLOAD_EXCEPTION_CODE, null,
        FILE_UPLOAD_EXCEPTION_CODE, locale);

    return new ErrorDto(errorMessage);
  }

}
