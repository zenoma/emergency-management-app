package es.udc.emergencyproject.backend.model.exceptions;

public class AssignmentAlreadyInStatusException extends DomainException {

  public AssignmentAlreadyInStatusException(String status) {
    super(status, "");
  }

}
