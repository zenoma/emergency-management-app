package es.udc.emergencyproject.backend.model.services.emergency.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RuleEvaluationResult {

  private final int teams;
  private final int vehicles;
  private final String reason;
}
