package es.udc.emergencyproject.backend.model.exceptions;

public class UserAlreadyRegisteredException extends DomainException {

  public UserAlreadyRegisteredException(String name, String id) {
    super(name, id);
  }
}
