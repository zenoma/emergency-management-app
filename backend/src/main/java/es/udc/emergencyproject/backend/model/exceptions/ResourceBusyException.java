package es.udc.emergencyproject.backend.model.exceptions;

public class ResourceBusyException extends DomainException {

  public ResourceBusyException(String name, String id) {
    super(name, id);
  }

}
