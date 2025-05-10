package es.udc.fireproject.backend.model.exceptions;

public class AlreadyDismantledException extends EntityException {

  public AlreadyDismantledException(String name, String id) {
    super(name, id);
  }
}
