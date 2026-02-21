package es.udc.fireproject.backend.model.exceptions;

public class QuadrantNotLinkedToFireException extends RuntimeException {

  private final Long fireId;
  private final Integer quadrantId;

  public QuadrantNotLinkedToFireException(Long fireId, Integer quadrantId) {
    this.fireId = fireId;
    this.quadrantId = quadrantId;
  }

  public Long getFireId() {
    return fireId;
  }

  public Integer getQuadrantId() {
    return quadrantId;
  }

}
