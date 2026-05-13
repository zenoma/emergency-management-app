package es.udc.emergencyproject.backend.model.services.emergency.recommendation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRule;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendationRuleEngine {

  private final ObjectMapper objectMapper;

  public RuleEvaluationResult evaluate(String emergencyTypeName, Iterable<EmergencyTypeRule> rules) {
    if (emergencyTypeName == null || rules == null) {
      return new RuleEvaluationResult(0, 0, "No protocol configured");
    }

    try {
      String typeName = emergencyTypeName.toLowerCase();
      for (EmergencyTypeRule ruleEntity : rules) {
        if (ruleEntity == null || ruleEntity.getRuleJson() == null) {
          continue;
        }
        JsonNode rule = objectMapper.readTree(ruleEntity.getRuleJson());
        JsonNode when = rule.get("when");
        JsonNode then = rule.get("then");
        if (when == null || then == null) {
          continue;
        }

        if (matches(typeName, when)) {
          int teams = then.path("teams").asInt(0);
          int vehicles = then.path("vehicles").asInt(0);
          return new RuleEvaluationResult(teams, vehicles, buildReason(when, teams, vehicles));
        }
      }
    } catch (Exception ex) {
      return new RuleEvaluationResult(0, 0, "Failed to evaluate protocol");
    }

    return new RuleEvaluationResult(0, 0, "No matching rule");
  }

  private boolean matches(String typeName, JsonNode when) {
    String conditionType = when.path("type").asText("");
    String value = when.path("value").asText("");
    if ("default".equals(conditionType)) {
      return true;
    }
    if ("name_contains".equals(conditionType)) {
      return !value.isBlank() && typeName.contains(value.toLowerCase());
    }
    return false;
  }

  private String buildReason(JsonNode when, int teams, int vehicles) {
    String value = when.path("value").asText("default");
    return "Rule matched: " + value + " -> teams=" + teams + ", vehicles=" + vehicles;
  }
}
