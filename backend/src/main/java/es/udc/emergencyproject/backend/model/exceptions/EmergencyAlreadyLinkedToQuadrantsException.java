package es.udc.emergencyproject.backend.model.exceptions;

public class EmergencyAlreadyLinkedToQuadrantsException extends DomainException {

  public EmergencyAlreadyLinkedToQuadrantsException(String name, String id) {
    super(name, id);
  }

}
