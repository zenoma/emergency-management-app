package es.udc.emergencyproject.backend.model.exceptions;

public class IncorrectLoginException extends RuntimeException {

  private final String userName;

  public IncorrectLoginException(String userName) {

    this.userName = userName;

  }

  public String getUserName() {
    return userName;
  }

}
