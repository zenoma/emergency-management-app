package es.udc.fireproject.backend.rest.exceptions;

import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.fireproject.backend.rest.common.ErrorsDto;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  private static final String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";
  private static final String INCORRECT_PASSWORD_EXCEPTION_CODE = "project.exceptions.IncorrectPasswordException";
  private static final String INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE = "project.exceptions.InsufficientRolePermissionException";

  @Autowired
  private MessageSource messageSource;

  @ExceptionHandler(IncorrectLoginException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorsDto handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {
    String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
        INCORRECT_LOGIN_EXCEPTION_CODE, locale);
    return new ErrorsDto(errorMessage);
  }

  @ExceptionHandler(IncorrectPasswordException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorsDto handleIncorrectPasswordException(IncorrectPasswordException exception, Locale locale) {
    String errorMessage = messageSource.getMessage(INCORRECT_PASSWORD_EXCEPTION_CODE, null,
        INCORRECT_PASSWORD_EXCEPTION_CODE, locale);
    return new ErrorsDto(errorMessage);
  }

  @ExceptionHandler(InsufficientRolePermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorsDto handleInsufficientRolePermissionException(InsufficientRolePermissionException exception,
      Locale locale) {
    String errorMessage = messageSource.getMessage(INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE, null,
        INSUFFICIENT_ROLE_PERMISSION_EXCEPTION_CODE, locale);
    return new ErrorsDto(errorMessage);
  }
}
