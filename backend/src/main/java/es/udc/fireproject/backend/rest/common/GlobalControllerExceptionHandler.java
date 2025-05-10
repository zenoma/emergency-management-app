package es.udc.fireproject.backend.rest.common;

import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.PermissionException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

  private static final String INSTANCE_NOT_FOUND_EXCEPTION_CODE = "project.exceptions.InstanceNotFoundException";
  private static final String DUPLICATE_INSTANCE_EXCEPTION_CODE = "project.exceptions.DuplicateInstanceException";
  private static final String
      PERMISSION_EXCEPTION_CODE = "project.exceptions.PermissionException";
  private static final String ILLEGAL_ARGUMENT_EXCEPTION_CODE = "project.exceptions.IllegalArgumentException";
  private static final String DATA_INTEGRITY_EXCEPTION_CODE = "project.exceptions.DataIntegrityViolationException";
  private static final String ALREADY_DISMANTLED_EXCEPTION_CODE = "project.exceptions.AlreadyDismantledException";
  private static final String ALREADY_EXIST_EXCEPTION_CODE = "project.exceptions.AlreadyExistException";


  @Autowired
  private MessageSource messageSource;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

    List<FieldErrorDto> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage())).collect(Collectors.toList());

    return new ErrorsDto(fieldErrors);

  }

  @ExceptionHandler(InstanceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorsDto handleInstanceNotFoundException(InstanceNotFoundException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
    String errorMessage = messageSource.getMessage(INSTANCE_NOT_FOUND_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getKey().toString()}, INSTANCE_NOT_FOUND_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage);

  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsDto handleIllegalArgumentException(IllegalArgumentException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(ILLEGAL_ARGUMENT_EXCEPTION_CODE, null,
        ILLEGAL_ARGUMENT_EXCEPTION_CODE,
        locale);

    return new ErrorsDto(errorMessage + ": " + exception.getMessage());

  }

  @ExceptionHandler(DuplicateInstanceException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsDto handleDuplicateInstanceException(DuplicateInstanceException exception, Locale locale) {

    String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
    String errorMessage = messageSource.getMessage(DUPLICATE_INSTANCE_EXCEPTION_CODE,
        new Object[]{nameMessage, exception.getKey().toString()}, DUPLICATE_INSTANCE_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage);

  }

  @ExceptionHandler(PermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public ErrorsDto handlePermissionException(PermissionException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(PERMISSION_EXCEPTION_CODE, null, PERMISSION_EXCEPTION_CODE,
        locale);

    return new ErrorsDto(errorMessage);

  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsDto handleDataIntegrityViolationException(DataIntegrityViolationException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(DATA_INTEGRITY_EXCEPTION_CODE, null,
        DATA_INTEGRITY_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage + "\n" + exception.getMessage());

  }

  @ExceptionHandler(AlreadyDismantledException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsDto handleAlreadyDismantledException(AlreadyDismantledException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(ALREADY_DISMANTLED_EXCEPTION_CODE,
        new Object[]{exception.getName(), exception.getId()}, ALREADY_DISMANTLED_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage);

  }

  @ExceptionHandler(AlreadyExistException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsDto handleAlreadyExistException(AlreadyExistException exception, Locale locale) {

    String errorMessage = messageSource.getMessage(ALREADY_EXIST_EXCEPTION_CODE,
        new Object[]{exception.getName(), exception.getId()}, ALREADY_EXIST_EXCEPTION_CODE, locale);

    return new ErrorsDto(errorMessage);

  }


}
