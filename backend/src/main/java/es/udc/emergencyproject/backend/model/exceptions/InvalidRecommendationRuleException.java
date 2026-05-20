package es.udc.emergencyproject.backend.model.exceptions;

public class InvalidRecommendationRuleException extends DomainException {

  public InvalidRecommendationRuleException(String name, String id) {
    super(name, id);
  }
}
