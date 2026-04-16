package es.udc.emergencyproject.backend.model.exceptions;

public class ResolvedEmergencyException extends DomainException {

  public ResolvedEmergencyException(String name, String id) {
    super(name, id);
  }
}
