package es.udc.emergencyproject.backend.model.exceptions;

public class QuadrantNotLinkedToEmergencyException extends RuntimeException {

  private final Long emergencyId;
  private final Integer quadrantId;

  public QuadrantNotLinkedToEmergencyException(Long emergencyId, Integer quadrantId) {
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
