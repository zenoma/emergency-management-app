package es.udc.emergencyproject.backend.model.exceptions;

public class QuadrantAlreadyLinkedToEmergencyException extends RuntimeException {

  private final Long emergencyId;
  private final Integer quadrantId;

  public QuadrantAlreadyLinkedToEmergencyException(Long emergencyId, Integer quadrantId) {
    this.emergencyId = emergencyId;
    this.quadrantId = quadrantId;
  }

  public Long getEmergencyId() {
    return emergencyId;
  }

  public Integer getQuadrantId() {
    return quadrantId;
  }

}
