package es.udc.emergencyproject.backend.model.exceptions;

public class EmergencyAlreadyLinkedToPointException extends DomainException {

  public EmergencyAlreadyLinkedToPointException(String name, String id) {
    super(name, id);
  }

}
