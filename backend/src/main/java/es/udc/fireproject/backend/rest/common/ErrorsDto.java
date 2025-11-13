package es.udc.fireproject.backend.rest.common;

import java.util.List;

public class ErrorsDto {

  private String errorMessage;
  private List<FieldErrorDto> fieldErrors;

  public ErrorsDto(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ErrorsDto(List<FieldErrorDto> fieldErrors) {

    this.fieldErrors = fieldErrors;

  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public List<FieldErrorDto> getFieldErrors() {
    return fieldErrors;
  }

  public void setFieldErrors(List<FieldErrorDto> fieldErrors) {
    this.fieldErrors = fieldErrors;
  }

}
