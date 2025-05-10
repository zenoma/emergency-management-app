package es.udc.fireproject.backend.model.exceptions;

public class AlreadyExistException extends EntityException {

  public AlreadyExistException(String name, String id) {
    super(name, id);
  }
}
