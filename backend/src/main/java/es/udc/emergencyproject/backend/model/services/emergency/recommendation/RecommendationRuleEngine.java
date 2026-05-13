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
      return new RuleEvaluationResult(0, 0, null, null, "No protocol configured");
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
          Double maxDistanceKm = then.has("maxDistanceKm") ? then.path("maxDistanceKm").asDouble() : null;
          String preferredOrganizationType = then.hasNonNull("preferredOrganizationType")
              ? then.path("preferredOrganizationType").asText(null)
              : null;
          return new RuleEvaluationResult(teams, vehicles, maxDistanceKm, preferredOrganizationType,
              buildReason(when, teams, vehicles, maxDistanceKm, preferredOrganizationType));
        }
      }
    } catch (Exception ex) {
      return new RuleEvaluationResult(0, 0, null, null, "Failed to evaluate protocol");
    }

    return new RuleEvaluationResult(0, 0, null, null, "No matching rule");
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

  private String buildReason(JsonNode when, int teams, int vehicles, Double maxDistanceKm, String preferredOrganizationType) {
    String value = when.path("value").asText("default");
    StringBuilder sb = new StringBuilder();
    sb.append("Rule matched: ").append(value)
        .append(" -> teams=").append(teams)
        .append(", vehicles=").append(vehicles);
    if (maxDistanceKm != null) {
      sb.append(", maxDistanceKm=").append(maxDistanceKm);
    }
    if (preferredOrganizationType != null) {
      sb.append(", preferredOrganizationType=").append(preferredOrganizationType);
    }
    return sb.toString();
  }
}
