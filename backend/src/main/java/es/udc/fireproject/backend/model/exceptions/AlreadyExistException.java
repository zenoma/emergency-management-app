package es.udc.fireproject.backend.model.exceptions;

public class AlreadyExistException extends DomainException {

  public AlreadyExistException(String name, String id) {
    super(name, id);
  }
}
