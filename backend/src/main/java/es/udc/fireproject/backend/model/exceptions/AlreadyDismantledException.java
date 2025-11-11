package es.udc.fireproject.backend.model.exceptions;

public class AlreadyDismantledException extends DomainException {

  public AlreadyDismantledException(String name, String id) {
    super(name, id);
  }
}
