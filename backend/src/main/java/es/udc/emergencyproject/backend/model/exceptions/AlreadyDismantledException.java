package es.udc.emergencyproject.backend.model.exceptions;

public class AlreadyDismantledException extends DomainException {

  public AlreadyDismantledException(String name, String id) {
    super(name, id);
  }
}
