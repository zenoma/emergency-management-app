package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRule;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InvalidRecommendationRuleException;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.RecommendationRuleManagementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class RecommendationRuleManagementServiceImplTest extends IntegrationTest {

  private final RecommendationRuleManagementService recommendationRuleManagementService;
  private final EmergencyManagementService emergencyManagementService;

  @Test
  void givenExistingEmergencyType_whenFindRules_thenReturnsNonEmpty() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Assertions.assertFalse(types.isEmpty());

    List<EmergencyTypeRule> rules = recommendationRuleManagementService.findRulesByEmergencyTypeId(
        types.get(0).getId());
    Assertions.assertFalse(rules.isEmpty());
    Assertions.assertTrue(rules.stream().allMatch(r -> r.getEmergencyType().getId().equals(types.get(0).getId())));
  }

  @Test
  void givenInvalidTypeId_whenFindRules_thenReturnsEmpty() {
    List<EmergencyTypeRule> rules = recommendationRuleManagementService.findRulesByEmergencyTypeId(-1L);
    Assertions.assertTrue(rules.isEmpty());
  }

  @Test
  void givenValidRuleId_whenUpdateRule_thenUpdated() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    List<EmergencyTypeRule> rules = recommendationRuleManagementService.findRulesByEmergencyTypeId(
        types.get(0).getId());
    Assertions.assertFalse(rules.isEmpty());

    EmergencyTypeRule rule = rules.get(0);
    String newJson = "{\"when\":{\"type\":\"default\"},\"then\":{\"teams\":5,\"vehicles\":3,\"maxDistanceKm\":200}}";

    EmergencyTypeRule updated = recommendationRuleManagementService.updateRule(
        rule.getId(), 99, newJson);

    Assertions.assertEquals(99, updated.getPriority());
    Assertions.assertEquals(newJson, updated.getRuleJson());
  }

  @Test
  void givenInvalidRuleId_whenUpdateRule_thenThrows() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> recommendationRuleManagementService.updateRule(-1L, 0, "{}"));
  }

  @Test
  void givenInvalidJson_whenUpdateRule_thenThrows() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    List<EmergencyTypeRule> rules = recommendationRuleManagementService.findRulesByEmergencyTypeId(
        types.get(0).getId());
    Assertions.assertFalse(rules.isEmpty());

    EmergencyTypeRule rule = rules.get(0);
    Assertions.assertThrows(InvalidRecommendationRuleException.class,
        () -> recommendationRuleManagementService.updateRule(rule.getId(), 0, "{invalid json}"));
  }

  @Test
  void givenDefaultRules_whenFindRules_thenOrderedByPriority() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Assertions.assertFalse(types.isEmpty());

    List<EmergencyTypeRule> rules = recommendationRuleManagementService.findRulesByEmergencyTypeId(
        types.get(0).getId());
    Assertions.assertFalse(rules.isEmpty());

    for (int i = 1; i < rules.size(); i++) {
      Assertions.assertTrue(rules.get(i - 1).getPriority() <= rules.get(i).getPriority());
    }
  }

  @Test
  void givenMultipleTypes_whenFindRules_thenEachTypeHasRules() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();

    for (EmergencyType type : types) {
      List<EmergencyTypeRule> rules = recommendationRuleManagementService.findRulesByEmergencyTypeId(type.getId());
      Assertions.assertFalse(rules.isEmpty(), "Type " + type.getName() + " must have at least one rule");
    }
  }
}
