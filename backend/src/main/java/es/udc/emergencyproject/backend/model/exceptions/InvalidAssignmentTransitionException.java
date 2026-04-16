package es.udc.emergencyproject.backend.model.exceptions;

public class InvalidAssignmentTransitionException extends DomainException {

  public InvalidAssignmentTransitionException(String from, String to) {
    super(from, to);
  }

}
