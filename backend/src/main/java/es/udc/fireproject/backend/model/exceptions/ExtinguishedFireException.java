package es.udc.fireproject.backend.model.exceptions;

public class ExtinguishedFireException extends DomainException {

  public ExtinguishedFireException(String name, String id) {
    super(name, id);
  }
}
