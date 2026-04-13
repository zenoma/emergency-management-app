package es.udc.emergencyproject.backend.model.exceptions;

public class ExtinguishedEmergencyException extends DomainException {

  public ExtinguishedEmergencyException(String name, String id) {
    super(name, id);
  }
}
